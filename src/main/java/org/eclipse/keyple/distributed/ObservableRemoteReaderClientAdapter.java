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

import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemoteReaderSpi;

/**
 * Adapter of observable {@link RemoteReaderClient}.
 *
 * @since 2.0.0
 */
final class ObservableRemoteReaderClientAdapter extends RemoteReaderClientAdapter
    implements ObservableRemoteReaderSpi {

  /**
   * Constructor.
   *
   * @param clientCoreApiLevel The API level of the client Core layer.
   * @param remoteReaderName The name of the remote reader.
   * @param localReaderName The name of the associated local reader.
   * @param sessionId The associated session ID.
   * @param clientNodeId The associated client node ID.
   * @param node The associated node.
   * @since 2.0.0
   */
  ObservableRemoteReaderClientAdapter(
      int clientCoreApiLevel,
      String remoteReaderName,
      String localReaderName,
      String sessionId,
      String clientNodeId,
      AbstractNodeAdapter node) {
    super(clientCoreApiLevel, remoteReaderName, localReaderName, sessionId, clientNodeId, node);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStartObservation() {
    getNode().onStartReaderObservation();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onStopObservation() {
    getNode().onStopReaderObservation();
  }
}
