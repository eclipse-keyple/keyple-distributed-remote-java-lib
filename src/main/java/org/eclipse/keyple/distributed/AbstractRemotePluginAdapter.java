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
import org.eclipse.keyple.core.distributed.remote.RemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginSpi;

/**
 * (package-private)<br>
 * Abstract class for all remote plugin adapters.
 *
 * @since 2.0
 */
abstract class AbstractRemotePluginAdapter extends AbstractMessageHandlerAdapter
    implements RemotePluginSpi {

  private final String remotePluginName;
  private final boolean isObservable;
  private RemotePluginApi remotePluginApi;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @param isObservable True if the remote plugin is observable.
   * @since 2.0
   */
  AbstractRemotePluginAdapter(String remotePluginName, boolean isObservable) {
    this.remotePluginName = remotePluginName;
    this.isObservable = isObservable;
  }

  /**
   * (package-private)<br>
   * Gets the bound {@link RemotePluginApi}.
   *
   * @return Null if the plugin is not registered to the Keyple main service.
   * @since 2.0
   */
  final RemotePluginApi getRemotePluginApi() {
    return remotePluginApi;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final void connect(RemotePluginApi remotePluginApi) {
    this.remotePluginApi = remotePluginApi;
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
            .setSessionId(UUID.randomUUID().toString())
            .setAction(MessageDto.Action.CMD.name())
            .setBody(jsonData);

    // Send the message as a request.
    MessageDto response = getNode().sendRequest(message);

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
}
