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

import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;

/**
 * Builder of {@link RemotePluginServerFactory} for Keyple <b>ObservablePlugin</b> type.
 *
 * @since 2.0.0
 */
public final class RemotePluginServerFactoryBuilder {

  /** Constructor */
  private RemotePluginServerFactoryBuilder() {}

  /**
   * Gets the first step of the builder to use in order to create a new factory instance.
   *
   * @param remotePluginName The identifier of the remote plugin.
   * @return Next configuration step.
   * @throws IllegalArgumentException If the plugin name is null or empty.
   * @since 2.0.0
   */
  public static NodeStep builder(String remotePluginName) {
    return new Builder(remotePluginName);
  }

  /**
   * Gets the first step of the builder to use in order to create a new factory instance.
   *
   * @param remotePluginName The identifier of the remote plugin.
   * @param executorService The custom service to be used to asynchronously notify remote reader
   *     connection events.
   * @return Next configuration step.
   * @throws IllegalArgumentException If the plugin name is null or empty or if the executor service
   *     is null.
   * @since 2.1.0
   */
  public static NodeStep builder(String remotePluginName, ExecutorService executorService) {
    return new Builder(remotePluginName, executorService);
  }

  /**
   * Step to configure the node associated with the service.
   *
   * @since 2.0.0
   */
  public interface NodeStep {

    /**
     * Configures the service with a {@link SyncNodeServer} node.
     *
     * <p>Note that the default awaiting timeout is 20 seconds. The timeout defines the maximum time
     * the client can wait for the server's response, as well as the maximum time the server can
     * wait between two client calls.
     *
     * @return Next configuration step.
     * @see #withSyncNode(int)
     * @since 2.0.0
     */
    BuilderStep withSyncNode();

    /**
     * Configures the service with a {@link SyncNodeServer} node configured with the specified
     * awaiting timeout.
     *
     * @param timeoutSeconds The timeout (in seconds) defines the maximum time the client can wait
     *     for the server's response, as well as the maximum time the server can wait between two
     *     client calls.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the timeout has a negative value.
     * @see #withSyncNode()
     * @since 2.4.0
     */
    BuilderStep withSyncNode(int timeoutSeconds);

    /**
     * Configures the service with a {@link AsyncNodeServer} node.
     *
     * <p>Note that the default awaiting timeout is 20 seconds. The timeout defines the maximum time
     * the client can wait for the server's response, as well as the maximum time the server can
     * wait between two client calls.
     *
     * @param endpoint The {@link AsyncEndpointServerSpi} network endpoint to use.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the endpoint is null.
     * @see #withAsyncNode(AsyncEndpointServerSpi, int)
     * @since 2.0.0
     */
    BuilderStep withAsyncNode(AsyncEndpointServerSpi endpoint);

    /**
     * Configures the service with a {@link AsyncNodeServer} node configured with the specified
     * awaiting timeout.
     *
     * @param endpoint The {@link AsyncEndpointServerSpi} network endpoint to use.
     * @param timeoutSeconds The timeout (in seconds) defines the maximum time the client can wait
     *     for the server's response, as well as the maximum time the server can wait between two
     *     client calls.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the endpoint is null or if the timeout has a negative
     *     value.
     * @see #withAsyncNode(AsyncEndpointServerSpi)
     * @since 2.4.0
     */
    BuilderStep withAsyncNode(AsyncEndpointServerSpi endpoint, int timeoutSeconds);
  }

  /**
   * Last step : builds a new instance.
   *
   * @since 2.0.0
   */
  public interface BuilderStep {

    /**
     * Creates a new instance of {@link RemotePluginServerFactory} using the current configuration.
     *
     * @return A not null reference.
     * @since 2.0.0
     */
    RemotePluginServerFactory build();
  }

  /** The internal step builder. */
  private static final class Builder implements NodeStep, BuilderStep {

    private final String remotePluginName;
    private final ExecutorService executorService;
    private AsyncEndpointServerSpi asyncEndpoint;
    private int timeoutSeconds = 20;

    public Builder(String remotePluginName) {
      Assert.getInstance().notEmpty(remotePluginName, "remotePluginName");
      this.remotePluginName = remotePluginName;
      this.executorService = null;
    }

    private Builder(String remotePluginName, ExecutorService executorService) {
      Assert.getInstance()
          .notEmpty(remotePluginName, "remotePluginName")
          .notNull(executorService, "executorService");
      this.remotePluginName = remotePluginName;
      this.executorService = executorService;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public BuilderStep withSyncNode() {
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.4.0
     */
    @Override
    public BuilderStep withSyncNode(int timeoutSeconds) {
      Assert.getInstance().greaterOrEqual(timeoutSeconds, 0, "timeoutSeconds");
      this.timeoutSeconds = timeoutSeconds;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public BuilderStep withAsyncNode(AsyncEndpointServerSpi endpoint) {
      Assert.getInstance().notNull(endpoint, "endpoint");
      this.asyncEndpoint = endpoint;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.4.0
     */
    @Override
    public BuilderStep withAsyncNode(AsyncEndpointServerSpi endpoint, int timeoutSeconds) {
      Assert.getInstance()
          .notNull(endpoint, "endpoint")
          .greaterOrEqual(timeoutSeconds, 0, "timeoutSeconds");
      this.asyncEndpoint = endpoint;
      this.timeoutSeconds = timeoutSeconds;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public RemotePluginServerFactory build() {
      return new RemotePluginServerFactoryAdapter(
          remotePluginName, executorService, asyncEndpoint, timeoutSeconds);
    }
  }
}
