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

import java.util.UUID;
import org.eclipse.keyple.core.distributed.remote.spi.AbstractRemotePluginSpi;

/**
 * (package-private)<br>
 * Abstract class for all remote plugin adapters.
 *
 * @since 2.0
 */
abstract class AbstractRemotePluginAdapter extends AbstractMessageHandlerAdapter
    implements AbstractRemotePluginSpi {

  private final String remotePluginName;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @since 2.0
   */
  AbstractRemotePluginAdapter(String remotePluginName) {
    this.remotePluginName = remotePluginName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final String getName() {
    return remotePluginName;
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
            .setAction(MessageDto.Action.CMD.name())
            .setSessionId(UUID.randomUUID().toString())
            .setBody(jsonData);

    // Send the message as a request.
    MessageDto response = getNode().sendRequest(message);

    // Check if the result is an error raised by the distributed layer.
    AbstractMessageHandlerAdapter.checkError(response);

    // Return the body content.
    return response.getBody();
  }
}
