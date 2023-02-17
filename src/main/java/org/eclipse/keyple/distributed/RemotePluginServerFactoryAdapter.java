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

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @param executorService The executor service to be used (optional).
   * @param asyncEndpointServerSpi The async endpoint server to bind.
   * @since 2.0.0
   */
  RemotePluginServerFactoryAdapter(
      String remotePluginName,
      ExecutorService executorService,
      AsyncEndpointServerSpi asyncEndpointServerSpi) {
    super(remotePluginName);
    this.executorService = executorService;
    this.asyncEndpointServerSpi = asyncEndpointServerSpi;
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
    logger.info(
        "Create a new 'RemotePluginServer' with name='{}', nodeType='{}'.",
        getRemotePluginName(),
        nodeType);

    if (asyncEndpointServerSpi == null) {
      remotePlugin.bindSyncNodeServer();
    } else {
      remotePlugin.bindAsyncNodeServer(asyncEndpointServerSpi);
    }

    return remotePlugin;
  }
}
