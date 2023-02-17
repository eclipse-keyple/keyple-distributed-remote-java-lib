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

import static org.eclipse.keyple.distributed.MessageDto.Action;

import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemotePluginSpi;

/**
 * Adapter of an observable {@link RemotePluginClient}.
 *
 * @since 2.0.0
 */
final class ObservableRemotePluginClientAdapter extends RemotePluginClientAdapter
    implements ObservableRemotePluginSpi {

  private ObservableRemotePluginApi observableRemotePluginApi;

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param isReaderObservationEnabled Is reader observation enabled ?
   * @since 2.0.0
   */
  ObservableRemotePluginClientAdapter(String remotePluginName, boolean isReaderObservationEnabled) {
    super(remotePluginName, isReaderObservationEnabled);
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
    return null; // Not applicable
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStartObservation() {
    getNode().onStartPluginsObservation();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStopObservation() {
    getNode().onStopPluginsObservation();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onMessage(MessageDto message) {
    if (Action.PLUGIN_EVENT.name().equals(message.getAction())) {
      observableRemotePluginApi.onPluginEvent(message.getBody());
    } else {
      super.onMessage(message);
    }
  }
}
