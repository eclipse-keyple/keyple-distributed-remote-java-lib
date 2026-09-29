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

import org.eclipse.keyple.core.common.KeypleReaderExtension;

/**
 * API of the <b>Remote Reader Server</b> provided by the <b>Remote Plugin Server</b> to be used in
 * the <b>Reader Client Side</b> configuration mode.
 *
 * <p>It is a {@link KeypleReaderExtension} of a Keyple <b>Reader</b> (not observable) which
 * provides some specific features.
 *
 * @since 2.0.0
 */
public interface RemoteReaderServer extends KeypleReaderExtension {

  /**
   * Gets the ID of the remote service to execute server side.
   *
   * @return A not empty string.
   * @since 2.0.0
   */
  String getServiceId();

  /**
   * Gets the initial content of the smart card if it is set.
   *
   * <p>The returned <b><code>org.calypsonet.terminal.reader.selection.SmartCard</code></b> object
   * can be cast into the expected type.
   *
   * @return Null if there is no initial card content.
   * @since 2.0.0
   * @deprecated Use {@link #getInitialCardContent(Class)} instead, which lets the server
   *     application define the expected type of the initial card content.
   */
  @Deprecated
  Object getInitialCardContent();

  /**
   * Gets the initial content of the smart card if it is set, as an instance of the expected type.
   *
   * <p>The expected type is usually the interface of the smart card provided by the card extension
   * (e.g. <b><code>org.eclipse.keypop.calypso.card.card.CalypsoCard</code></b>), or {@link
   * java.util.Properties} for the clients using the Server JSON API (the initial card content then
   * contains the processed card selection scenario). The initial card content must be a smart card
   * or a {@link java.util.Properties} object of the expected type.
   *
   * @param initialCardContentClass The expected type of the initial card content.
   * @param <T> The expected type of the initial card content.
   * @return Null if there is no initial card content.
   * @throws IllegalArgumentException If the provided class is null.
   * @throws IllegalStateException If the initial card content is not a smart card or a {@link
   *     java.util.Properties} object of the expected type.
   * @since 2.6.0
   */
  <T> T getInitialCardContent(Class<T> initialCardContentClass);

  /**
   * Gets the input data if it is set.
   *
   * @param inputDataClass The expected input data type.
   * @param <T> The type of the expected input data.
   * @return Null if there is no input data.
   * @throws IllegalArgumentException If the provided class is null.
   * @since 2.0.0
   */
  <T> T getInputData(Class<T> inputDataClass);
}
