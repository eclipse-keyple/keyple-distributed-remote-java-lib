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

/**
 * Adapter of {@link RemoteReaderClient}.
 *
 * @since 2.0.0
 */
class RemoteReaderClientAdapter extends AbstractRemoteReaderAdapter implements RemoteReaderClient {

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
  RemoteReaderClientAdapter(
      int clientCoreApiLevel,
      String remoteReaderName,
      String localReaderName,
      String sessionId,
      String clientNodeId,
      AbstractNodeAdapter node) {
    super(
        MessageDto.API_LEVEL,
        clientCoreApiLevel,
        remoteReaderName,
        localReaderName,
        null,
        sessionId,
        clientNodeId,
        node);
  }
}
