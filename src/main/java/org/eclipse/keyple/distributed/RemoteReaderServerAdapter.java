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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link RemoteReaderServer}.
 *
 * @since 2.0.0
 */
final class RemoteReaderServerAdapter extends AbstractRemoteReaderAdapter
    implements RemoteReaderServer {

  private static final Logger logger = LoggerFactory.getLogger(RemoteReaderServerAdapter.class);

  /** Names of the smart card interfaces that the initial card content class must implement. */
  private static final List<String> SMART_CARD_INTERFACE_NAMES =
      Arrays.asList(
          "org.eclipse.keypop.reader.selection.spi.SmartCard",
          "org.calypsonet.terminal.reader.selection.spi.SmartCard");

  private final String serviceId;
  private final String initialCardContentJson;
  private final String initialCardContentClassName;
  private final String inputDataJson;

  /**
   * Constructor.
   *
   * @param clientDistributedApiLevel The API level of the client Distributed layer.
   * @param clientCoreApiLevel The API level of the client Core layer.
   * @param remoteReaderName The name of the remote reader.
   * @param localReaderName The name of the associated local reader.
   * @param isContactless Is local reader contactless (null if unknown).
   * @param sessionId The associated session ID.
   * @param clientNodeId The associated client node ID.
   * @param node The associated node.
   * @param serviceId The service ID.
   * @param initialCardContentJson The optional initial card content as a JSON string.
   * @param initialCardContentClassName The class name of the optional initial card content.
   * @param inputDataJson The optional input data as a JSON string.
   * @since 2.0.0
   */
  RemoteReaderServerAdapter( // NOSONAR
      int clientDistributedApiLevel,
      int clientCoreApiLevel,
      String remoteReaderName,
      String localReaderName,
      Boolean isContactless,
      String sessionId,
      String clientNodeId,
      AbstractNodeAdapter node,
      String serviceId,
      String initialCardContentJson,
      String initialCardContentClassName,
      String inputDataJson) {
    super(
        clientDistributedApiLevel,
        clientCoreApiLevel,
        remoteReaderName,
        localReaderName,
        isContactless,
        sessionId,
        clientNodeId,
        node);
    this.serviceId = serviceId;
    this.initialCardContentJson = initialCardContentJson;
    this.initialCardContentClassName = initialCardContentClassName;
    this.inputDataJson = inputDataJson;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public String getServiceId() {
    return serviceId;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   * @deprecated Use {@link #getInitialCardContent(Class)} instead.
   */
  @Deprecated
  @Override
  public Object getInitialCardContent() {
    if (initialCardContentJson != null) {
      try {
        Class<?> classOfInitialCardContent = loadInitialCardContentClass();
        if (!isSupportedInitialCardContentType(classOfInitialCardContent)) {
          logger.error(
              "Class is not supported [initialCardContentClassName={}]",
              initialCardContentClassName);
          return null;
        }
        return JsonUtil.getParser().fromJson(initialCardContentJson, classOfInitialCardContent);
      } catch (ClassNotFoundException e) {
        logger.error(
            "Class not found [initialCardContentClassName={}]", initialCardContentClassName, e);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.6.0
   */
  @Override
  public <T> T getInitialCardContent(Class<T> initialCardContentClass) {
    Assert.getInstance().notNull(initialCardContentClass, "initialCardContentClass");
    if (initialCardContentJson == null) {
      return null;
    }
    Class<?> classOfInitialCardContent;
    try {
      classOfInitialCardContent = loadInitialCardContentClass();
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(
          "Initial card content class '" + initialCardContentClassName + "' is not found", e);
    }
    if (!initialCardContentClass.isAssignableFrom(classOfInitialCardContent)
        || !isSupportedInitialCardContentType(classOfInitialCardContent)) {
      throw new IllegalStateException(
          "Initial card content class '"
              + initialCardContentClassName
              + "' is not supported or is not of type '"
              + initialCardContentClass.getName()
              + "'");
    }
    return initialCardContentClass.cast(
        JsonUtil.getParser().fromJson(initialCardContentJson, classOfInitialCardContent));
  }

  /**
   * Loads the class of the initial card content, without initializing it.
   *
   * @return A not null reference.
   * @throws ClassNotFoundException If the class is not found.
   */
  private Class<?> loadInitialCardContentClass() throws ClassNotFoundException {
    return Class.forName(
        initialCardContentClassName, false, RemoteReaderServerAdapter.class.getClassLoader());
  }

  /**
   * Checks if the provided type is supported as initial card content: a smart card, or a {@link
   * Properties} object (used by the clients of the Server JSON API to transmit a processed card
   * selection scenario).
   *
   * @param type The type to check.
   * @return True if the type is supported.
   */
  private static boolean isSupportedInitialCardContentType(Class<?> type) {
    return type == Properties.class || isSmartCard(type);
  }

  /**
   * Checks if the provided type is or inherits from one of the smart card interfaces.
   *
   * @param type The type to check.
   * @return True if the type is a smart card.
   */
  private static boolean isSmartCard(Class<?> type) {
    if (type == null) {
      return false;
    }
    if (SMART_CARD_INTERFACE_NAMES.contains(type.getName()) || isSmartCard(type.getSuperclass())) {
      return true;
    }
    for (Class<?> interfaceType : type.getInterfaces()) {
      if (isSmartCard(interfaceType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public <T> T getInputData(Class<T> inputDataClass) {
    Assert.getInstance().notNull(inputDataClass, "inputDataClass");
    return inputDataJson != null
        ? JsonUtil.getParser().fromJson(inputDataJson, inputDataClass)
        : null;
  }
}
