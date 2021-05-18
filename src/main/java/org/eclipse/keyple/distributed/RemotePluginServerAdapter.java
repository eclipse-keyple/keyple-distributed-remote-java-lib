/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.distributed;

import static org.eclipse.keyple.distributed.MessageDto.*;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Adapter of {@link RemotePluginServer}.
 *
 * @since 2.0
 */
final class RemotePluginServerAdapter extends AbstractRemotePluginAdapter
    implements RemotePluginServer {

  private static final Logger logger = LoggerFactory.getLogger(RemotePluginServerAdapter.class);

  private final Map<String, RemoteReaderServerAdapter> readers;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param asyncEndpointServerSpi The async endpoint server to bind.
   * @since 2.0
   */
  RemotePluginServerAdapter(
      String remotePluginName, AsyncEndpointServerSpi asyncEndpointServerSpi) {

    super(remotePluginName, true);
    this.readers = new ConcurrentHashMap<String, RemoteReaderServerAdapter>();

    // Logging
    String nodeType = asyncEndpointServerSpi != null ? "AsyncNodeServer" : "SyncNodeServer";
    logger.info(
        "Create a new 'RemotePluginServer' with name='{}', nodeType='{}'.",
        remotePluginName,
        nodeType);

    if (asyncEndpointServerSpi == null) {
      bindSyncNodeServer();
    } else {
      bindAsyncNodeServer(asyncEndpointServerSpi);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public SyncNodeServer getSyncNode() {
    AbstractNodeAdapter node = getNode();
    if (node instanceof SyncNodeServer) {
      return (SyncNodeServer) node;
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin '%s' is not configured with a synchronous network protocol.",
            getName()));
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public AsyncNodeServer getAsyncNode() {
    AbstractNodeAdapter node = getNode();
    if (node instanceof AsyncNodeServer) {
      return (AsyncNodeServer) node;
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin '%s' is not configured with an asynchronous network protocol.",
            getName()));
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public void endRemoteService(String remoteReaderName, Object userOutputData) {

    // Clean the readers map.
    RemoteReaderServerAdapter reader = readers.remove(remoteReaderName);

    // Unregister the reader.
    ((ObservableRemotePluginApi) getRemotePluginApi()).unregisterReader(remoteReaderName);

    // Build the message
    JsonObject body = new JsonObject();
    body.addProperty(
        JsonProperty.USER_OUTPUT_DATA.name(), JsonUtil.getParser().toJson(userOutputData));

    MessageDto message =
        new MessageDto()
            .setAction(Action.END_REMOTE_SERVICE.name())
            .setRemoteReaderName(remoteReaderName)
            .setSessionId(reader.getSessionId())
            .setClientNodeId(reader.getClientNodeId())
            .setBody(body.toString());

    // Send the message
    getNode().sendMessage(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public RemoteReaderSpi createRemoteReader(String localReaderName, boolean isObservable) {
    throw new UnsupportedOperationException("createRemoteReader");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void startPluginsObservation() {
    // NOP
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void stopPluginsObservation() {
    // NOP
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void onMessage(MessageDto message) {
    switch (Action.valueOf(message.getAction())) {
      case EXECUTE_REMOTE_SERVICE:
        RemoteReaderServerAdapter masterReader = createMasterReader(message);
        readers.put(masterReader.getName(), masterReader);
        ((ObservableRemotePluginApi) getRemotePluginApi()).registerMasterReader(masterReader);
        break;
      case READER_EVENT:
        Assert.getInstance().notNull(message.getRemoteReaderName(), "remoteReaderName");
        RemoteReaderServerAdapter slaveReader = createSlaveReader(message);
        readers.put(slaveReader.getName(), slaveReader);
        String readerEvent = extractReaderEvent(message);
        ((ObservableRemotePluginApi) getRemotePluginApi())
            .registerSlaveReader(slaveReader, message.getRemoteReaderName(), readerEvent);
        break;
      default:
        throw new IllegalStateException(String.format("Message not supported : %s", message));
    }
  }

  /**
   * (private)<br>
   * Creates a master reader based on the incoming message.
   *
   * @param message The incoming message.
   * @return A not null reference.
   */
  private RemoteReaderServerAdapter createMasterReader(MessageDto message) {

    JsonObject body = JsonUtil.getParser().fromJson(message.getBody(), JsonObject.class);

    // Service ID
    String serviceId = body.get(JsonProperty.SERVICE_ID.name()).getAsString();

    // User input data
    String userInputData =
        body.has(JsonProperty.USER_INPUT_DATA.name())
            ? body.get(JsonProperty.USER_INPUT_DATA.name()).getAsString()
            : null;

    // Initial card content
    String initialCardContent = null;
    String initialCardContentClassName = null;
    if (body.has(JsonProperty.INITIAL_CARD_CONTENT.name())) {
      initialCardContent = body.get(JsonProperty.INITIAL_CARD_CONTENT.name()).getAsString();
      initialCardContentClassName =
          body.get(JsonProperty.INITIAL_CARD_CONTENT_CLASS_NAME.name()).getAsString();
    }

    // Is reader observable ?
    boolean isReaderObservable = body.get(JsonProperty.IS_READER_OBSERVABLE.name()).getAsBoolean();

    // Other fields
    String remoteReaderName = UUID.randomUUID().toString();

    if (logger.isDebugEnabled()) {
      logger.debug(
          "Remote plugin '{}' creates the master remote reader '{}' with serviceId='{}', isObservable={}, sessionId='{}', clientNodeId='{}'.",
          getName(),
          remoteReaderName,
          serviceId,
          isReaderObservable,
          message.getSessionId(),
          message.getClientNodeId());
    }

    return new RemoteReaderServerAdapter(
        remoteReaderName,
        message.getLocalReaderName(),
        message.getSessionId(),
        message.getClientNodeId(),
        isReaderObservable,
        getNode(),
        serviceId,
        userInputData,
        initialCardContent,
        initialCardContentClassName);
  }

  /**
   * (private)<br>
   * Creates a slave reader to handle the communication in the session of the event notification.
   *
   * @param message The incoming reader event message.
   * @return A not null reference.
   */
  private RemoteReaderServerAdapter createSlaveReader(MessageDto message) {

    JsonObject body = JsonUtil.getParser().fromJson(message.getBody(), JsonObject.class);

    // User input data
    String userInputData =
        body.has(JsonProperty.USER_INPUT_DATA.name())
            ? body.get(JsonProperty.USER_INPUT_DATA.name()).getAsString()
            : null;

    // Other fields
    String remoteReaderName = UUID.randomUUID().toString();

    if (logger.isDebugEnabled()) {
      logger.debug(
          "Remote plugin '{}' creates the slave remote reader '{}' with sessionId='{}', clientNodeId='{}'.",
          getName(),
          remoteReaderName,
          message.getSessionId(),
          message.getClientNodeId());
    }

    return new RemoteReaderServerAdapter(
        remoteReaderName,
        message.getLocalReaderName(),
        message.getSessionId(),
        message.getClientNodeId(),
        true,
        getNode(),
        null,
        userInputData,
        null,
        null);
  }

  /**
   * (private)<br>
   * Extracts the reader event from the incoming message.
   *
   * @param message The incoming message.
   * @return A not empty string.
   */
  private String extractReaderEvent(MessageDto message) {
    return JsonUtil.getParser()
        .fromJson(message.getBody(), JsonObject.class)
        .get(JsonProperty.READER_EVENT.name())
        .getAsString();
  }
}
