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

import static org.eclipse.keyple.distributed.MessageDto.*;

import java.util.UUID;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;

/**
 * (package-private)<br>
 * Abstract class for all remote reader adapters.
 *
 * @since 2.0
 */
abstract class AbstractRemoteReaderAdapter implements RemoteReaderSpi {

  private final String remotePluginName;
  private final String remoteReaderName;
  private final String localReaderName;
  private final String sessionId;
  private final String clientNodeId;
  private final boolean isObservable;
  private final AbstractNodeAdapter node;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the associated remote plugin.
   * @param remoteReaderName The name of the remote reader.
   * @param localReaderName The name of the associated local reader.
   * @param sessionId The associated session ID.
   * @param clientNodeId The associated client node ID.
   * @param isObservable True if the remote reader is observable.
   * @param node The associated node.
   * @since 2.0
   */
  AbstractRemoteReaderAdapter(
      String remotePluginName,
      String remoteReaderName,
      String localReaderName,
      String sessionId,
      String clientNodeId,
      boolean isObservable,
      AbstractNodeAdapter node) {
    this.remotePluginName = remotePluginName;
    this.remoteReaderName = remoteReaderName;
    this.localReaderName = localReaderName;
    this.sessionId = sessionId;
    this.clientNodeId = clientNodeId;
    this.isObservable = isObservable;
    this.node = node;
  }

  /**
   * (package-private)<br>
   * Gets the associated local reader name.
   *
   * @return A not empty string.
   * @since 2.0
   */
  final String getLocalReaderName() {
    return localReaderName;
  }

  /**
   * (package-private)<br>
   * Gets the associated session ID.
   *
   * @return Null if no session ID is set.
   * @since 2.0
   */
  final String getSessionId() {
    return sessionId;
  }

  /**
   * (package-private)<br>
   * Gets the associated client node ID.
   *
   * @return A not empty string.
   * @since 2.0
   */
  final String getClientNodeId() {
    return clientNodeId;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final String getName() {
    return remoteReaderName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final String executeRemotely(String jsonData) {

    // Build the message.
    MessageDto message =
        new MessageDto()
            .setSessionId(sessionId != null ? sessionId : UUID.randomUUID().toString())
            .setAction(Action.CMD.name())
            .setRemoteReaderName(remoteReaderName)
            .setLocalReaderName(localReaderName)
            .setClientNodeId(clientNodeId)
            .setBody(jsonData);

    // Send the message as a request.
    MessageDto response = node.sendRequest(message);

    // Check if the result is an error raised by the distributed layer.
    AbstractMessageHandlerAdapter.checkError(response);

    // Return the body content.
    return response.getBody();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final boolean isObservable() {
    return isObservable;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void startReaderObservation() {
    node.startReadersObservation();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void stopReaderObservation() {
    node.stopReadersObservation();
  }
}
