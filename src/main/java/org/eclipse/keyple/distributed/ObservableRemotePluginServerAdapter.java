/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
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
import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.RemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemotePluginSpi;
import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemoteReaderSpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link RemotePluginServer}.
 *
 * @since 2.0.0
 */
final class ObservableRemotePluginServerAdapter extends AbstractRemotePluginAdapter
    implements RemotePluginServer, ObservableRemotePluginSpi {

  private static final Logger logger =
      LoggerFactory.getLogger(ObservableRemotePluginServerAdapter.class);

  private final ExecutorService executorService;
  private final Map<String, RemoteReaderServerAdapter> readers;

  private ObservableRemotePluginApi observableRemotePluginApi;

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param executorService The custom service to be used to asynchronously notify remote reader
   *     connection events.
   * @since 2.0.0
   */
  ObservableRemotePluginServerAdapter(String remotePluginName, ExecutorService executorService) {
    super(remotePluginName);
    this.executorService = executorService;
    readers = new ConcurrentHashMap<>();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public SyncNodeServer getSyncNode() {
    if (isBoundToSyncNode()) {
      return (SyncNodeServer) getNode();
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin [%s] is not configured with a synchronous network protocol", getName()));
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public AsyncNodeServer getAsyncNode() {
    if (!isBoundToSyncNode()) {
      return (AsyncNodeServer) getNode();
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin [%s] is not configured with an asynchronous network protocol",
            getName()));
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void endRemoteService(String remoteReaderName, Object outputData) {

    Assert.getInstance().notEmpty(remoteReaderName, "remoteReaderName");

    // Clean the readers map.
    RemoteReaderServerAdapter reader = readers.remove(remoteReaderName);

    if (reader == null) {
      throw new IllegalArgumentException(
          String.format("No reader exists with name [%s]", remoteReaderName));
    }

    // Unregister the remote reader.
    observableRemotePluginApi.removeRemoteReader(remoteReaderName);

    // Build the message
    JsonObject body = new JsonObject();
    if (reader.getClientCoreApiLevel() != 0) {
      body.addProperty(JsonProperty.CORE_API_LEVEL.getKey(), reader.getClientCoreApiLevel());
    }
    if (reader.getClientDistributedApiLevel() != 0) {
      body.add(JsonProperty.OUTPUT_DATA.getKey(), JsonUtil.getParser().toJsonTree(outputData));
    } else {
      body.addProperty(JsonProperty.OUTPUT_DATA.name(), JsonUtil.toJson(outputData));
    }

    MessageDto message =
        new MessageDto()
            .setApiLevel(reader.getClientDistributedApiLevel())
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
   * @since 2.0.0
   */
  @Override
  public String executeRemotely(String jsonData) {
    // NOP. Invoked only once during the plugin registration process.
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onUnregister() {
    // NOP
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void connect(RemotePluginApi remotePluginApi) {
    // NOP because there is no usage of this API here.
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public RemoteReaderSpi createRemoteReader(String remoteReaderName, String localReaderName) {
    throw new UnsupportedOperationException("createRemoteReader");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public ObservableRemoteReaderSpi createObservableRemoteReader(
      String remoteReaderName, String localReaderName) {
    throw new UnsupportedOperationException("createObservableRemoteReader");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void connect(ObservableRemotePluginApi observableRemotePluginApi) {
    this.observableRemotePluginApi = observableRemotePluginApi;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.1.0
   */
  @Override
  public ExecutorService getExecutorService() {
    return executorService;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStartObservation() {
    // NOP
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStopObservation() {
    // NOP
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onMessage(MessageDto message) {

    if (!Action.EXECUTE_REMOTE_SERVICE.name().equals(message.getAction())) {
      throw new IllegalStateException(String.format("Message not supported : %s", message));
    }

    // Creates a remote reader based on the incoming message.
    JsonObject body = JsonUtil.getParser().fromJson(message.getBody(), JsonObject.class);

    // The API level is retrieved from the wrapper, as the body content has been created by the
    // Distributed client layer.
    int clientDistributedApiLevel;
    if (message.getApiLevel() != 0) {
      clientDistributedApiLevel = message.getApiLevel();
    } else if (body.has(JsonProperty.SERVICE_ID.getKey())) {
      clientDistributedApiLevel = 1;
    } else {
      clientDistributedApiLevel = 0;
    }

    // In this particular case, the API level contained in the body does not reflect the version of
    // the body, but that of the Core client layer.
    int clientCoreApiLevel;
    if (body.has(JsonProperty.CORE_API_LEVEL.getKey())) {
      clientCoreApiLevel = body.get(JsonProperty.CORE_API_LEVEL.getKey()).getAsInt();
    } else {
      clientCoreApiLevel = -1; // Unknown at this step
    }

    String serviceId;
    Boolean isReaderContactless = null;
    String initialCardContent = null;
    String initialCardContentClassName = null;
    String inputData = null;

    if (clientDistributedApiLevel != 0) {
      // Service ID
      serviceId = body.get(JsonProperty.SERVICE_ID.getKey()).getAsString();
      // Is local reader contactless?
      if (clientDistributedApiLevel >= 3) {
        isReaderContactless = body.get(JsonProperty.IS_READER_CONTACTLESS.getKey()).getAsBoolean();
      }
      // Initial card content
      if (body.has(JsonProperty.INITIAL_CARD_CONTENT.getKey())) {
        initialCardContent =
            body.getAsJsonObject(JsonProperty.INITIAL_CARD_CONTENT.getKey()).toString();
        initialCardContentClassName =
            body.get(JsonProperty.INITIAL_CARD_CONTENT_CLASS_NAME.getKey()).getAsString();
      }
      // Input data
      if (body.has(JsonProperty.INPUT_DATA.getKey())) {
        inputData = body.getAsJsonObject(JsonProperty.INPUT_DATA.getKey()).toString();
      }
    } else {
      // Service ID
      serviceId = body.get(JsonProperty.SERVICE_ID.name()).getAsString();
      // Initial card content
      if (body.has(JsonProperty.INITIAL_CARD_CONTENT.name())) {
        initialCardContent = body.get(JsonProperty.INITIAL_CARD_CONTENT.name()).getAsString();
        initialCardContentClassName =
            body.get(JsonProperty.INITIAL_CARD_CONTENT_CLASS_NAME.name()).getAsString();
      }
      // Input data
      if (body.has(JsonProperty.INPUT_DATA.name())) {
        inputData = body.get(JsonProperty.INPUT_DATA.name()).getAsString();
      }
    }

    // Other fields
    String remoteReaderName = UUID.randomUUID().toString();

    logger.info(
        "Plugin [{}] create new remote reader (remoteReaderName: {}, serviceId: {}, sessionId: {}, clientNodeId: {})",
        getName(),
        remoteReaderName,
        serviceId,
        message.getSessionId(),
        message.getClientNodeId());

    RemoteReaderServerAdapter remoteReader =
        new RemoteReaderServerAdapter(
            clientDistributedApiLevel,
            clientCoreApiLevel,
            remoteReaderName,
            message.getLocalReaderName(),
            isReaderContactless,
            message.getSessionId(),
            message.getClientNodeId(),
            getNode(),
            serviceId,
            initialCardContent,
            initialCardContentClassName,
            inputData);

    // Add the new remote reader to the readers map.
    readers.put(remoteReader.getName(), remoteReader);

    // Register the remote reader and notify observers.
    observableRemotePluginApi.addRemoteReader(remoteReader, clientCoreApiLevel);
  }
}
