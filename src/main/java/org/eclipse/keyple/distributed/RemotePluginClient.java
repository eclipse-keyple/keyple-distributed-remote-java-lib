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

import org.eclipse.keyple.core.common.KeyplePluginExtension;

/**
 * API of the <b>Remote Plugin Client</b> associated to a <b>Local Service Server</b> to be used in
 * the <b>Reader Server Side</b> configuration mode.
 *
 * <p>This plugin must be registered as a standard plugin by the application installed on a
 * <b>Client</b> not having local access to the smart card reader and that wishes to control the
 * reader remotely.
 *
 * <p>It is a {@link KeyplePluginExtension} of a Keyple <b>Plugin</b>, <b>ObservablePlugin</b> or
 * <b>PoolPlugin</b>.
 *
 * @since 2.0
 */
public interface RemotePluginClient extends KeyplePluginExtension {

  /**
   * Gets the associated {@link AsyncNodeClient} if the service is configured with an asynchronous
   * network protocol.
   *
   * @return A not null reference.
   * @throws IllegalStateException If the service is not configured with an asynchronous network
   *     protocol.
   * @since 2.0
   */
  AsyncNodeClient getAsyncNode();
}
