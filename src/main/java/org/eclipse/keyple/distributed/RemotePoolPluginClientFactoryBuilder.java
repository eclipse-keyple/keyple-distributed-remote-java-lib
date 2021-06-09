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

import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;

/**
 * Builder of {@link RemotePluginClientFactory} for Keyple <b>PoolPlugin</b> type.
 *
 * @since 2.0
 */
public final class RemotePoolPluginClientFactoryBuilder {

  /**
   * (private)<br>
   * Constructor
   */
  private RemotePoolPluginClientFactoryBuilder() {}

  /**
   * Gets the first step of the builder to use in order to create a new factory instance.
   *
   * @param remotePluginName The identifier of the remote pool plugin.
   * @return Next configuration step.
   * @throws IllegalArgumentException If the pool plugin name is null or empty.
   * @since 2.0
   */
  public static NodeStep builder(String remotePluginName) {
    return new Builder(remotePluginName);
  }

  /**
   * Step to configure the node associated with the service.
   *
   * @since 2.0
   */
  public interface NodeStep {

    /**
     * Configures the service with a {@link SyncNodeClient} node.
     *
     * @param endpoint The {@link SyncEndpointClientSpi} network endpoint to use.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the provided endpoint is null.
     * @since 2.0
     */
    BuilderStep withSyncNode(SyncEndpointClientSpi endpoint);

    /**
     * Configures the service with a {@link AsyncNodeClient} node.
     *
     * @param endpoint The {@link AsyncEndpointClientSpi} network endpoint to use.
     * @param timeoutSeconds This timeout (in seconds) defines how long the async client waits for a
     *     server order before cancelling the global transaction.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the endpoint is null or the timeout {@code <} 1.
     * @since 2.0
     */
    BuilderStep withAsyncNode(AsyncEndpointClientSpi endpoint, int timeoutSeconds);
  }

  /**
   * Last step : build a new instance.
   *
   * @since 2.0
   */
  public interface BuilderStep {

    /**
     * Creates a new instance of {@link RemotePluginClientFactory} using the current configuration.
     *
     * @return A not null reference.
     * @since 2.0
     */
    RemotePluginClientFactory build();
  }

  /**
   * (private)<br>
   * The internal step builder.
   */
  private static final class Builder implements NodeStep, BuilderStep {

    private final String remotePluginName;
    private SyncEndpointClientSpi syncEndpoint;
    private AsyncEndpointClientSpi asyncEndpoint;
    private int asyncNodeClientTimeoutSeconds;

    private Builder(String remotePluginName) {
      Assert.getInstance().notEmpty(remotePluginName, "remotePluginName");
      this.remotePluginName = remotePluginName;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0
     */
    @Override
    public BuilderStep withSyncNode(SyncEndpointClientSpi endpoint) {
      Assert.getInstance().notNull(endpoint, "endpoint");
      this.syncEndpoint = endpoint;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0
     */
    @Override
    public BuilderStep withAsyncNode(AsyncEndpointClientSpi endpoint, int timeoutSeconds) {
      Assert.getInstance()
          .notNull(endpoint, "endpoint")
          .greaterOrEqual(timeoutSeconds, 1, "timeoutSeconds");
      this.asyncEndpoint = endpoint;
      this.asyncNodeClientTimeoutSeconds = timeoutSeconds;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0
     */
    @Override
    public RemotePluginClientFactory build() {
      return new RemotePluginClientFactoryAdapter(
          remotePluginName,
          true,
          false,
          false,
          syncEndpoint,
          null,
          null,
          asyncEndpoint,
          asyncNodeClientTimeoutSeconds);
    }
  }
}
