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
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;

/**
 * (package-private)<br>
 * Adapter of {@link RemotePluginServerFactory}.
 *
 * @since 2.0
 */
final class RemotePluginServerFactoryAdapter extends AbstractRemotePluginFactoryAdapter
    implements RemotePluginServerFactory {

  private final AsyncEndpointServerSpi asyncEndpointServerSpi;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @param asyncEndpointServerSpi The async endpoint server to bind.
   * @since 2.0
   */
  RemotePluginServerFactoryAdapter(
      String remotePluginName, AsyncEndpointServerSpi asyncEndpointServerSpi) {
    super(remotePluginName, false);
    this.asyncEndpointServerSpi = asyncEndpointServerSpi;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public RemotePluginSpi getRemotePlugin() {
    return new RemotePluginServerAdapter(getRemotePluginName(), asyncEndpointServerSpi);
  }
}
