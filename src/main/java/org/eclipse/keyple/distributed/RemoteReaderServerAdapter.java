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

import org.eclipse.keyple.core.common.KeypleSmartCard;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Adapter of {@link RemoteReaderServer}.
 *
 * @since 2.0
 */
final class RemoteReaderServerAdapter extends AbstractRemoteReaderAdapter
    implements RemoteReaderServer {

  private static final Logger logger = LoggerFactory.getLogger(RemoteReaderServerAdapter.class);

  private final String serviceId;
  private final String userInputDataJson;
  private final String initialCardContentJson;
  private final String initialCardContentClassName;

  /**
   * (package-private)<br>
   * Constructor.
   *
   * @param remotePluginName The name of the associated remote plugin.
   * @param remoteReaderName The name of the remote reader.
   * @param localReaderName The name of the associated local reader.
   * @param sessionId The associated session ID.
   * @param clientNodeId The associated client node ID.
   * @param isObservable True if remote reader is observable.
   * @param node The associated node.
   * @param serviceId The service ID.
   * @param userInputDataJson The optional user input data as a JSON string.
   * @param initialCardContentJson The optional initial card content as a JSON string.
   * @param initialCardContentClassName The class name of the optional initial card content.
   * @since 2.0
   */
  RemoteReaderServerAdapter( // NOSONAR
      String remotePluginName,
      String remoteReaderName,
      String localReaderName,
      String sessionId,
      String clientNodeId,
      boolean isObservable,
      AbstractNodeAdapter node,
      String serviceId,
      String userInputDataJson,
      String initialCardContentJson,
      String initialCardContentClassName) {
    super(
        remotePluginName,
        remoteReaderName,
        localReaderName,
        sessionId,
        clientNodeId,
        isObservable,
        node);
    this.serviceId = serviceId;
    this.userInputDataJson = userInputDataJson;
    this.initialCardContentJson = initialCardContentJson;
    this.initialCardContentClassName = initialCardContentClassName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public String getServiceId() {
    return serviceId;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public <T> T getUserInputData(Class<T> classOfUserInputData) {
    Assert.getInstance().notNull(classOfUserInputData, "classOfUserInputData");
    return userInputDataJson != null
        ? JsonUtil.getParser().fromJson(userInputDataJson, classOfUserInputData)
        : null;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public KeypleSmartCard getInitialCardContent() {
    if (initialCardContentJson != null) {
      try {
        Class<?> classOfInitialCardContent = Class.forName(initialCardContentClassName);
        return (KeypleSmartCard)
            JsonUtil.getParser().fromJson(initialCardContentJson, classOfInitialCardContent);
      } catch (ClassNotFoundException e) {
        logger.error("Class not found for name : {}", initialCardContentClassName, e);
      }
    }
    return null;
  }
}
