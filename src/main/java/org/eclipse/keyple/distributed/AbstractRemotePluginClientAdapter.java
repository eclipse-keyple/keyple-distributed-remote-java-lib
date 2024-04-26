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

/**
 * Abstract class of all {@link RemotePluginClient} adapters.
 *
 * @since 2.0.0
 */
abstract class AbstractRemotePluginClientAdapter extends AbstractRemotePluginAdapter
    implements RemotePluginClient {

  private String globalSessionId;

  /**
   * Constructor.
   *
   * @param remotePluginName The name of the remote plugin.
   * @since 2.0.0
   */
  AbstractRemotePluginClientAdapter(String remotePluginName) {
    super(remotePluginName);
  }

  /**
   * Gets the global session ID value if is set.
   *
   * @return Null if no global session ID is set.
   * @since 2.0.0
   */
  final String getGlobalSessionId() {
    return globalSessionId;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public final AsyncNodeClient getAsyncNode() {
    if (!isBoundToSyncNode()) {
      return (AsyncNodeClient) getNode();
    }
    throw new IllegalStateException(
        String.format(
            "Remote plugin [%s] is not configured with an asynchronous network protocol",
            getName()));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the session ID is not set, then initialize a new one for the entire plugin's lifecycle
   * and try to open a new session.<br>
   * This is required for async node.
   *
   * @since 2.0.0
   */
  @Override
  public final String executeRemotely(String jsonData) {
    if (isBoundToSyncNode()) {
      // Sync node => use a temporal session ID.
      String sessionId = generateSessionId();
      try {
        getNode().openSession(sessionId);
        return executeRemotely(jsonData, sessionId);
      } finally {
        getNode().closeSessionSilently(sessionId);
      }
    } else {
      // Async node => use a global session ID.
      if (globalSessionId == null) {
        globalSessionId = generateSessionId();
        getNode().openSession(globalSessionId);
      }
      return executeRemotely(jsonData, globalSessionId);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public final void onUnregister() {
    if (globalSessionId != null) {
      try {
        getNode().closeSessionSilently(globalSessionId);
      } finally {
        globalSessionId = null;
      }
    }
  }
}
