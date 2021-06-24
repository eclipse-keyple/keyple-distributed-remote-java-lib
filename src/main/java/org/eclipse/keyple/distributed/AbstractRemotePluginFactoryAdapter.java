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

import org.eclipse.keyple.core.common.CommonApiProperties;
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

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin to build.
   * @since 2.0
   */
  AbstractRemotePluginFactoryAdapter(String remotePluginName) {
    this.remotePluginName = remotePluginName;
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
  public String getCommonApiVersion() {
    return CommonApiProperties.VERSION;
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
}
