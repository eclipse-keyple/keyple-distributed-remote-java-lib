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

import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.distributed.remote.spi.AbstractRemotePluginSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link RemotePluginServerFactory}.
 *
 * @since 2.0.0
 */
final class RemotePluginServerFactoryAdapter extends AbstractRemotePluginFactoryAdapter
    implements RemotePluginServerFactory {

  private static final Logger logger =
      LoggerFactory.getLogger(RemotePluginServerFactoryAdapter.class);

  private final ExecutorService executorService;
  private final AsyncEndpointServerSpi asyncEndpointServerSpi;
  private final Integer timeoutSeconds;

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @param executorService The executor service to be used (optional).
   * @param asyncEndpointServerSpi The async endpoint server to bind.
   * @param timeoutSeconds The timeout in seconds (optional).
   * @since 2.0.0
   */
  RemotePluginServerFactoryAdapter(
      String remotePluginName,
      ExecutorService executorService,
      AsyncEndpointServerSpi asyncEndpointServerSpi,
      Integer timeoutSeconds) {
    super(remotePluginName);
    this.executorService = executorService;
    this.asyncEndpointServerSpi = asyncEndpointServerSpi;
    this.timeoutSeconds = timeoutSeconds;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public AbstractRemotePluginSpi getRemotePlugin() {

    // Create the remote plugin.
    ObservableRemotePluginServerAdapter remotePlugin =
        new ObservableRemotePluginServerAdapter(getRemotePluginName(), executorService);

    // Bind the node.
    String nodeType = asyncEndpointServerSpi != null ? "AsyncNodeServer" : "SyncNodeServer";
    String timeoutSecondsStr = timeoutSeconds != null ? timeoutSeconds.toString() : "20";
    logger.info(
        "Create new 'RemotePluginServer' (name: {}, nodeType: {}, timeoutSeconds: {})",
        getRemotePluginName(),
        nodeType,
        timeoutSecondsStr);

    if (asyncEndpointServerSpi == null) {
      if (timeoutSeconds != null) {
        remotePlugin.bindSyncNodeServer(timeoutSeconds);
      } else {
        remotePlugin.bindSyncNodeServer();
      }
    } else {
      if (timeoutSeconds != null) {
        remotePlugin.bindAsyncNodeServer(asyncEndpointServerSpi, timeoutSeconds);
      } else {
        remotePlugin.bindAsyncNodeServer(asyncEndpointServerSpi);
      }
    }

    return remotePlugin;
  }
}
