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

import org.eclipse.keyple.core.common.CommonsApiProperties;
import org.eclipse.keyple.core.distributed.remote.DistributedRemoteApiProperties;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;

/**
 * (package-private)<br>
 * Abstract class of all remote plugin factory adapters.
 *
 * @since 2.0
 */
abstract class AbstractRemotePluginFactoryAdapter implements RemotePluginFactorySpi {

  private final String remotePluginName;
  private final boolean isPoolPlugin;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @param isPoolPlugin Is pool plugin ?
   * @since 2.0
   */
  AbstractRemotePluginFactoryAdapter(String remotePluginName, boolean isPoolPlugin) {
    this.remotePluginName = remotePluginName;
    this.isPoolPlugin = isPoolPlugin;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public String getDistributedRemoteApiVersion() {
    return DistributedRemoteApiProperties.VERSION;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public String getCommonsApiVersion() {
    return CommonsApiProperties.VERSION;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public String getRemotePluginName() {
    return remotePluginName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public boolean isPoolPlugin() {
    return isPoolPlugin;
  }
}
