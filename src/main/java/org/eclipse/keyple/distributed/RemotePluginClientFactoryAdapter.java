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

import org.eclipse.keyple.core.distributed.remote.spi.AbstractRemotePluginSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link RemotePluginClientFactory}.
 *
 * @since 2.0.0
 */
final class RemotePluginClientFactoryAdapter extends AbstractRemotePluginFactoryAdapter
    implements RemotePluginClientFactory {

  private static final Logger logger =
      LoggerFactory.getLogger(RemotePluginClientFactoryAdapter.class);

  private final boolean isPoolPlugin;
  private final boolean isPluginObservationEnabled;
  private final boolean isReaderObservationEnabled;
  private final SyncEndpointClientSpi syncEndpointClientSpi;
  private final ServerPushEventStrategyAdapter syncPluginObservationStrategy;
  private final ServerPushEventStrategyAdapter syncReaderObservationStrategy;
  private final AsyncEndpointClientSpi asyncEndpointClientSpi;
  private final int asyncNodeClientTimeoutSeconds;

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @param isPoolPlugin Is a pool plugin ?
   * @param isPluginObservationEnabled Is plugin observation enabled ?
   * @param isReaderObservationEnabled Is reader observation enabled ?
   * @param syncEndpointClientSpi The sync endpoint client to bind.
   * @param syncPluginObservationStrategy The plugin observation strategy to use for sync protocol.
   * @param syncReaderObservationStrategy The reader observation strategy to use for sync protocol.
   * @param asyncEndpointClientSpi The async endpoint client to bind.
   * @param asyncNodeClientTimeoutSeconds The client timeout to use for async protocol (in seconds).
   * @since 2.0.0
   */
  RemotePluginClientFactoryAdapter( // NOSONAR
      String remotePluginName,
      boolean isPoolPlugin,
      boolean isPluginObservationEnabled,
      boolean isReaderObservationEnabled,
      SyncEndpointClientSpi syncEndpointClientSpi,
      ServerPushEventStrategyAdapter syncPluginObservationStrategy,
      ServerPushEventStrategyAdapter syncReaderObservationStrategy,
      AsyncEndpointClientSpi asyncEndpointClientSpi,
      int asyncNodeClientTimeoutSeconds) {
    super(remotePluginName);
    this.isPoolPlugin = isPoolPlugin;
    this.isPluginObservationEnabled = isPluginObservationEnabled;
    this.isReaderObservationEnabled = isReaderObservationEnabled;
    this.syncEndpointClientSpi = syncEndpointClientSpi;
    this.syncPluginObservationStrategy = syncPluginObservationStrategy;
    this.syncReaderObservationStrategy = syncReaderObservationStrategy;
    this.asyncEndpointClientSpi = asyncEndpointClientSpi;
    this.asyncNodeClientTimeoutSeconds = asyncNodeClientTimeoutSeconds;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public AbstractRemotePluginSpi getRemotePlugin() {

    AbstractRemotePluginClientAdapter remotePlugin;

    // Create the remote plugin.
    if (isPoolPlugin) {
      remotePlugin = new RemotePoolPluginClientAdapter(getRemotePluginName());
    } else if (isPluginObservationEnabled) {
      remotePlugin =
          new ObservableRemotePluginClientAdapter(
              getRemotePluginName(), isReaderObservationEnabled);
    } else {
      remotePlugin =
          new RemotePluginClientAdapter(getRemotePluginName(), isReaderObservationEnabled);
    }

    // Bind the node.
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
          getRemotePluginName(),
          isPluginObservationEnabled,
          pluginObservationStrategy,
          isReaderObservationEnabled,
          readerObservationStrategy);

      remotePlugin.bindSyncNodeClient(
          syncEndpointClientSpi, syncPluginObservationStrategy, syncReaderObservationStrategy);

    } else {
      logger.info(
          "Create a new 'RemotePluginClient' with name='{}', nodeType='AsyncNodeClient', timeoutSeconds={}, isPluginObservationEnabled={}, isReaderObservationEnabled={}.",
          getRemotePluginName(),
          asyncNodeClientTimeoutSeconds,
          isPluginObservationEnabled,
          isReaderObservationEnabled);

      remotePlugin.bindAsyncNodeClient(asyncEndpointClientSpi, asyncNodeClientTimeoutSeconds);
    }

    return remotePlugin;
  }
}
