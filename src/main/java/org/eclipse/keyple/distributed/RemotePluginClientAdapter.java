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

import org.eclipse.keyple.core.distributed.remote.RemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemoteReaderSpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginSpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;

/**
 * (package-private)<br>
 * Adapter of {@link RemotePluginClient}.
 *
 * @since 2.0
 */
class RemotePluginClientAdapter extends AbstractRemotePluginClientAdapter
    implements RemotePluginSpi {

  private final boolean isReaderObservationEnabled;

  private RemotePluginApi remotePluginApi;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param isReaderObservationEnabled Is reader observation enabled ?
   * @since 2.0
   */
  RemotePluginClientAdapter(String remotePluginName, boolean isReaderObservationEnabled) {
    super(remotePluginName);
    this.isReaderObservationEnabled = isReaderObservationEnabled;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public void connect(RemotePluginApi remotePluginApi) {
    this.remotePluginApi = remotePluginApi;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final RemoteReaderSpi createRemoteReader(String remoteReaderName, String localReaderName) {
    return new RemoteReaderClientAdapter(
        remoteReaderName, localReaderName, getGlobalSessionId(), getNode().getNodeId(), getNode());
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final ObservableRemoteReaderSpi createObservableRemoteReader(
      String remoteReaderName, String localReaderName) {
    if (!isReaderObservationEnabled) {
      throw new IllegalStateException(
          "Cannot create the observable remote reader because the reader observation strategy is not configured.");
    }
    return new ObservableRemoteReaderClientAdapter(
        remoteReaderName, localReaderName, getGlobalSessionId(), getNode().getNodeId(), getNode());
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void onMessage(MessageDto message) {
    if (Action.READER_EVENT.name().equals(message.getAction())) {
      remotePluginApi.onReaderEvent(message.getBody());
    }
  }
}
