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
 * API of the <b>Remote Plugin Server</b> associated to a <b>Local Service Client</b> to be used in
 * the <b>Reader Client Side</b> configuration mode.
 *
 * <p>This plugin must be registered as a standard plugin by the application installed on a
 * <b>Server</b> not having local access to the smart card reader and that wishes to control the
 * reader remotely.
 *
 * <p>It is a {@link KeyplePluginExtension} of a Keyple <b>ObservablePlugin</b> which provides some
 * specific features.
 *
 * <p>Please note that <b>this plugin is observable only to trigger ticketing services</b> on the
 * server side, but does not allow observation on the local plugin (reader
 * connection/disconnection).
 *
 * <p>Note also that its provided remote readers <b>are not observable</b>. If it is necessary to
 * observe the local readers, it is the responsibility of the local application to do so.
 *
 * <p><u>How to use it ?</u><br>
 *
 * <ol>
 *   <li>Register the plugin.
 *   <li>Subscribe to plugin observation.
 *   <li>Wait to be notified of a plugin event of type "READER_CONNECTED".
 *   <li>Retrieve the name of the first reader contained in the event readers list.
 *   <li>Retrieve the remote reader from the plugin.
 *   <li>Retrieve the service id from the reader using the method {@link
 *       RemoteReaderServer#getServiceId()}.
 *   <li>Execute the ticketing service identified by the service id.
 *   <li>During the ticketing service execution, you can retrieve from the reader the initial smart
 *       card content transmitted by the client using the method {@link
 *       RemoteReaderServer#getInitialCardContent()} and/or the additional input data using the
 *       method {@link RemoteReaderServer#getInputData(Class)}.
 *   <li>To end the remote ticketing service, invoke on the plugin the method {@link
 *       RemotePluginServer#endRemoteService(String, Object)} by providing the reader name and
 *       optionally a output data to transmit to the client.
 * </ol>
 *
 * @since 2.0
 */
public interface RemotePluginServer extends KeyplePluginExtension {

  /**
   * Gets the associated {@link SyncNodeServer} if the service is configured with a synchronous
   * network protocol.
   *
   * @return A not null reference.
   * @throws IllegalStateException If the service is not configured with a synchronous network
   *     protocol.
   * @since 2.0
   */
  SyncNodeServer getSyncNode();

  /**
   * Gets the associated {@link AsyncNodeServer} if the service is configured with an asynchronous
   * network protocol.
   *
   * @return A not null reference.
   * @throws IllegalStateException If the service is not configured with an asynchronous network
   *     protocol.
   * @since 2.0
   */
  AsyncNodeServer getAsyncNode();

  /**
   * Ends the remote ticketing service associated to the provided remote reader name and returns to
   * the client the provided optional output data.
   *
   * <p>This method uses Class.getClass() to get the type for the specified object, but the
   * getClass() loses the generic type information because of the Type Erasure feature of Java.
   *
   * <p>Note that this method works fine if the any of the object fields are of generic type, just
   * the object itself should not be of a generic type.
   *
   * @param remoteReaderName The remote reader name.
   * @param outputData The object containing output data (optional).
   * @throws IllegalArgumentException If the remote reader name is null, empty or unknown.
   * @since 2.0
   */
  void endRemoteService(String remoteReaderName, Object outputData);
}
