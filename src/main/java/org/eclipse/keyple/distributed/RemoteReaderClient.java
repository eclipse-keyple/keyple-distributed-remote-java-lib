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
 * API of the <b>Remote Reader Client</b> provided by the <b>Remote Plugin Client</b> to be used in
 * the <b>Reader Server Side</b> configuration mode.
 *
 * <p>It is a {@link KeypleReaderExtension} of a Keyple <b>Reader</b> or <b>ObservableReader</b>.
 *
 * @since 2.0.0
 */
public interface RemoteReaderClient extends KeypleReaderExtension {}
