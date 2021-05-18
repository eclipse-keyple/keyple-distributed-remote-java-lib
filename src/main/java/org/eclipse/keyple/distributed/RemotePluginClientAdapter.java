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

import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Adapter of {@link RemotePluginClient}.
 *
 * @since 2.0
 */
final class RemotePluginClientAdapter extends AbstractRemotePluginAdapter
    implements RemotePluginClient {

  private static final Logger logger = LoggerFactory.getLogger(RemotePluginClientAdapter.class);

  private final boolean isReaderObservationEnabled;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param isPluginObservationEnabled Is plugin observation enabled ?
   * @param isReaderObservationEnabled Is reader observation enabled ?
   * @param syncEndpointClientSpi The sync endpoint client to bind.
   * @param syncPluginObservationStrategy The plugin observation strategy to use for sync protocol.
   * @param syncReaderObservationStrategy The reader observation strategy to use for sync protocol.
   * @param asyncEndpointClientSpi The async endpoint client to bind.
   * @param asyncNodeClientTimeoutSeconds The client timeout to use for async protocol (in seconds).
   * @since 2.0
   */
  RemotePluginClientAdapter( // NOSONAR
      String remotePluginName,
      boolean isPluginObservationEnabled,
      boolean isReaderObservationEnabled,
      SyncEndpointClientSpi syncEndpointClientSpi,
      ServerPushEventStrategyAdapter syncPluginObservationStrategy,
      ServerPushEventStrategyAdapter syncReaderObservationStrategy,
      AsyncEndpointClientSpi asyncEndpointClientSpi,
      int asyncNodeClientTimeoutSeconds) {

    super(remotePluginName, isPluginObservationEnabled);
    this.isReaderObservationEnabled = isReaderObservationEnabled;

    if (syncEndpointClientSpi != null) {
      String pluginObservationStrategy =
          syncPluginObservationStrategy != null
              ? syncPluginObservationStrategy.getType().name()
                  + "_"
                  + syncPluginObservationStrategy.getDurationMillis()
                  + "_millis"
              : null;
      String readerObservationStrategy =
          syncReaderObservationStrategy != null
              ? syncReaderObservationStrategy.getType().name()
                  + "_"
                  + syncReaderObservationStrategy.getDurationMillis()
                  + "_millis"
              : null;
      logger.info(
          "Create a new 'RemotePluginClient' with name='{}', nodeType='SyncNodeClient', isPluginObservationEnabled={}, syncPluginObservationStrategy={}, isReaderObservationEnabled={}, syncReaderObservationStrategy={}.",
          remotePluginName,
          isPluginObservationEnabled,
          pluginObservationStrategy,
          isReaderObservationEnabled,
          readerObservationStrategy);
      bindSyncNodeClient(
          syncEndpointClientSpi, syncPluginObservationStrategy, syncReaderObservationStrategy);
    } else {
      logger.info(
          "Create a new 'RemotePluginClient' with name='{}', nodeType='AsyncNodeClient', timeoutSeconds={}, isPluginObservationEnabled={}, isReaderObservationEnabled={}.",
          remotePluginName,
          asyncNodeClientTimeoutSeconds,
          isPluginObservationEnabled,
          isReaderObservationEnabled);
      bindAsyncNodeClient(asyncEndpointClientSpi, asyncNodeClientTimeoutSeconds);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public AsyncNodeClient getAsyncNode() {
    AbstractNodeAdapter node = getNode();
    if (node instanceof AsyncNodeClient) {
      return (AsyncNodeClient) node;
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
  public RemoteReaderSpi createRemoteReader(String localReaderName, boolean isObservable) {
    if (isObservable && !isReaderObservationEnabled) {
      throw new IllegalStateException(
          "Cannot create the remote reader because the reader observation strategy is not configured.");
    }
    return new RemoteReaderClientAdapter(
        localReaderName, localReaderName, null, getNode().getNodeId(), isObservable, getNode());
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void startPluginsObservation() {

    // Start local observation.
    getNode()
        .sendMessage(
            new MessageDto()
                .setAction(Action.START_PLUGINS_OBSERVATION.name())
                .setSessionId(generateSessionId()));

    // Start remote observation.
    getNode().startPluginsObservation();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void stopPluginsObservation() {

    // Stop remote observation.
    getNode().stopPluginsObservation();

    // Stop local observation.
    getNode()
        .sendMessage(
            new MessageDto()
                .setAction(Action.STOP_PLUGINS_OBSERVATION.name())
                .setSessionId(generateSessionId()));
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void onMessage(MessageDto message) {
    if (Action.valueOf(message.getAction()) == Action.PLUGIN_EVENT) {
      // Plugin event.
      ((ObservableRemotePluginApi) getRemotePluginApi()).onPluginEvent(message.getBody());
    } else {
      // Reader event.
      getRemotePluginApi().onReaderEvent(message.getBody());
    }
  }
}
