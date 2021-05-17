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

import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;

/**
 * (package-private)<br>
 * Adapter of {@link RemotePluginClientFactory}.
 *
 * @since 2.0
 */
final class RemotePluginClientFactoryAdapter extends AbstractRemotePluginFactoryAdapter
    implements RemotePluginClientFactory {

  private final boolean isPluginObservationEnabled;
  private final boolean isReaderObservationEnabled;
  private final SyncEndpointClientSpi syncEndpointClientSpi;
  private final ServerPushEventStrategyAdapter syncPluginObservationStrategy;
  private final ServerPushEventStrategyAdapter syncReaderObservationStrategy;
  private final AsyncEndpointClientSpi asyncEndpointClientSpi;
  private final int asyncNodeClientTimeoutSeconds;

  /**
   * (package-private)<br>
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
   * @since 2.0
   */
  RemotePluginClientFactoryAdapter(
      String remotePluginName,
      boolean isPoolPlugin,
      boolean isPluginObservationEnabled,
      boolean isReaderObservationEnabled,
      SyncEndpointClientSpi syncEndpointClientSpi,
      ServerPushEventStrategyAdapter syncPluginObservationStrategy,
      ServerPushEventStrategyAdapter syncReaderObservationStrategy,
      AsyncEndpointClientSpi asyncEndpointClientSpi,
      int asyncNodeClientTimeoutSeconds) {
    super(remotePluginName, isPoolPlugin);
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
   * @since 2.0
   */
  @Override
  public RemotePluginSpi getRemotePlugin() {
    return new RemotePluginClientAdapter(
        getRemotePluginName(),
        isPluginObservationEnabled,
        isReaderObservationEnabled,
        syncEndpointClientSpi,
        syncPluginObservationStrategy,
        syncReaderObservationStrategy,
        asyncEndpointClientSpi,
        asyncNodeClientTimeoutSeconds);
  }
}
