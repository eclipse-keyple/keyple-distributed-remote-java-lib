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

import org.eclipse.keyple.core.common.KeypleReaderExtension;

/**
 * (package-private)<br>
 * Adapter of {@link RemoteReaderClient}.
 *
 * @since 2.0
 */
final class RemoteReaderClientAdapter extends AbstractRemoteReaderAdapter
    implements RemoteReaderClient, KeypleReaderExtension {

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the associated remote plugin.
   * @param remoteReaderName The name of the remote reader.
   * @param localReaderName The name of the associated local reader.
   * @param sessionId The associated session ID.
   * @param clientNodeId The associated client node ID.
   * @param isObservable True if remote reader is observable.
   * @param node The associated node.
   * @since 2.0
   */
  RemoteReaderClientAdapter(
      String remotePluginName,
      String remoteReaderName,
      String localReaderName,
      String sessionId,
      String clientNodeId,
      boolean isObservable,
      AbstractNodeAdapter node) {
    super(
        remotePluginName,
        remoteReaderName,
        localReaderName,
        sessionId,
        clientNodeId,
        isObservable,
        node);
  }
}
