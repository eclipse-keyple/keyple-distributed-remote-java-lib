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

import org.eclipse.keyple.core.distributed.remote.spi.AbstractRemotePluginSpi;

/**
 * (package-private)<br>
 * Abstract class for all remote plugin adapters.
 *
 * @since 2.0.0
 */
abstract class AbstractRemotePluginAdapter extends AbstractMessageHandlerAdapter
    implements AbstractRemotePluginSpi {

  private final String remotePluginName;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @since 2.0.0
   */
  AbstractRemotePluginAdapter(String remotePluginName) {
    this.remotePluginName = remotePluginName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public final String getName() {
    return remotePluginName;
  }

  /**
   * (package-private)<br>
   * Executes remotely the provided JSON data command using the provided session ID.
   *
   * @param jsonData The JSON data to send.
   * @param sessionId The session ID to use.
   * @return A JSON string containing the response received from the distributed local service. It
   *     can be empty if the command returns nothing.
   * @since 2.0.0
   */
  final String executeRemotely(String jsonData, String sessionId) {

    // Build the message.
    MessageDto message =
        new MessageDto()
            .setAction(MessageDto.Action.CMD.name())
            .setSessionId(sessionId)
            .setBody(jsonData);

    // Send the message as a request.
    MessageDto response = getNode().sendRequest(message);

    // Check if the result is an error raised by the distributed layer.
    AbstractMessageHandlerAdapter.checkError(response);

    // Return the body content.
    return response.getBody();
  }
}
