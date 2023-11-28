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

import org.eclipse.keyple.core.distributed.remote.spi.RemotePoolPluginSpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;

/**
 * Adapter of pool {@link RemotePluginClient}.
 *
 * @since 2.0.0
 */
class RemotePoolPluginClientAdapter extends AbstractRemotePluginClientAdapter
    implements RemotePoolPluginSpi {

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @since 2.0.0
   */
  RemotePoolPluginClientAdapter(String remotePluginName) {
    super(remotePluginName);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public final RemoteReaderSpi createRemoteReader(String remoteReaderName, String localReaderName) {
    return new RemoteReaderClientAdapter(
        getCoreApiLevel(),
        remoteReaderName,
        localReaderName,
        getGlobalSessionId(),
        getNode().getNodeId(),
        getNode());
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onMessage(MessageDto message) {
    throw new UnsupportedOperationException("onMessage");
  }
}
