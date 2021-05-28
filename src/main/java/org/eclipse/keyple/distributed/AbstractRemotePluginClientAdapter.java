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

/**
 * (package-private)<br>
 * Abstract class of all {@link RemotePluginClient} adapters.
 *
 * @since 2.0
 */
abstract class AbstractRemotePluginClientAdapter extends AbstractRemotePluginAdapter
    implements RemotePluginClient {

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @since 2.0
   */
  AbstractRemotePluginClientAdapter(String remotePluginName) {
    super(remotePluginName);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public final AsyncNodeClient getAsyncNode() {
    AbstractNodeAdapter node = getNode();
    if (node instanceof AsyncNodeClient) {
      return (AsyncNodeClient) node;
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin '%s' is not configured with an asynchronous network protocol.",
            getName()));
  }
}
