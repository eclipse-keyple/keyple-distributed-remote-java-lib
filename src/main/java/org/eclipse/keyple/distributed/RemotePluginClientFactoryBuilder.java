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

import static org.eclipse.keyple.distributed.ServerPushEventStrategyAdapter.*;

import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;

/**
 * Builder of {@link RemotePluginClientFactory} for Keyple <b>Plugin</b> or <b>ObservablePlugin</b>
 * type.
 *
 * @since 2.0.0
 */
public final class RemotePluginClientFactoryBuilder {

  /**
   * (private)<br>
   * Constructor
   */
  private RemotePluginClientFactoryBuilder() {}

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
   * Step to configure the node associated with the service.
   *
   * @since 2.0.0
   */
  public interface NodeStep {

    /**
     * Configures the service with a {@link SyncNodeClient} node.
     *
     * @param endpoint The {@link SyncEndpointClientSpi} network endpoint to use.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the provided endpoint is null.
     * @since 2.0.0
     */
    SyncNodePluginStep withSyncNode(SyncEndpointClientSpi endpoint);

    /**
     * Configures the service with a {@link AsyncNodeClient} node.
     *
     * <p>The network channel is opened once and must remain open for the entire lifecycle of the
     * plugin until it is unregistered.
     *
     * @param endpoint The {@link AsyncEndpointClientSpi} network endpoint to use.
     * @param timeoutSeconds This timeout (in seconds) defines how long the async client waits for a
     *     server response before cancelling the global transaction.
     * @return Next configuration step.
     * @throws IllegalArgumentException If the endpoint is null or the timeout {@code <} 1.
     * @since 2.0.0
     */
    BuilderStep withAsyncNode(AsyncEndpointClientSpi endpoint, int timeoutSeconds);
  }

  /**
   * Step to activate the plugin observation for sync protocol.
   *
   * @since 2.0.0
   */
  public interface SyncNodePluginStep {

    /**
     * Activates the plugin observation.
     *
     * @return Next configuration step.
     * @since 2.0.0
     */
    ServerPushPluginEventStrategyStep withPluginObservation();

    /**
     * Do not activate the plugin observation.
     *
     * @return Next configuration step.
     * @since 2.0.0
     */
    SyncNodeReaderStep withoutPluginObservation();
  }

  /**
   * Step to configure the plugin observation for sync protocol.
   *
   * @since 2.0.0
   */
  public interface ServerPushPluginEventStrategyStep {

    /**
     * Polling strategy : The client requests the server every X milliseconds to check if there are
     * any events.<br>
     * This mode is non-blocking server side and not very demanding on the server's resources
     * because if there are no events, then the server immediately responds to the client.
     *
     * @param requestFrequencyMillis The request frequency duration (in milliseconds).
     * @return Next configuration step.
     * @throws IllegalArgumentException If the frequency is {@code <} 1.
     * @since 2.0.0
     */
    SyncNodeReaderStep withPluginPollingStrategy(int requestFrequencyMillis);

    /**
     * Long polling strategy : The client requests continuously the server to check for events.<br>
     * This mode is blocking server side and more costly in resource for the server because if there
     * is no event, then the server keeps the hand during X milliseconds in case an event would
     * occurs before responds to the client.<br>
     * This mode has the advantage of being more reactive.
     *
     * @param requestTimeoutMillis The request timeout duration (in milliseconds).
     * @return Next configuration step.
     * @throws IllegalArgumentException If the timeout is {@code <} 1.
     * @since 2.0.0
     */
    SyncNodeReaderStep withPluginLongPollingStrategy(int requestTimeoutMillis);
  }

  /**
   * Step to activate the reader observation for sync protocol.
   *
   * @since 2.0.0
   */
  public interface SyncNodeReaderStep {

    /**
     * Activates the reader observation.
     *
     * @return Next configuration step.
     * @since 2.0.0
     */
    ServerPushReaderEventStrategyStep withReaderObservation();

    /**
     * Do not activate the reader observation.
     *
     * @return Next configuration step.
     * @since 2.0.0
     */
    BuilderStep withoutReaderObservation();
  }

  /**
   * Step to configure the reader observation for sync protocol.
   *
   * @since 2.0.0
   */
  public interface ServerPushReaderEventStrategyStep {

    /**
     * Polling strategy : The client requests the server every X milliseconds to check if there are
     * any events.<br>
     * This mode is non-blocking server side and not very demanding on the server's resources
     * because if there are no events, then the server immediately responds to the client.
     *
     * @param requestFrequencyMillis The request frequency duration (in milliseconds).
     * @return Next configuration step.
     * @throws IllegalArgumentException If the frequency is {@code <} 1.
     * @since 2.0.0
     */
    BuilderStep withReaderPollingStrategy(int requestFrequencyMillis);

    /**
     * Long polling strategy : The client requests continuously the server to check for events.<br>
     * This mode is blocking server side and more costly in resource for the server because if there
     * is no event, then the server keeps the hand during X milliseconds in case an event would
     * occurs before responds to the client.<br>
     * This mode has the advantage of being more reactive.
     *
     * @param requestTimeoutMillis The request timeout duration (in milliseconds).
     * @return Next configuration step.
     * @throws IllegalArgumentException If the timeout is {@code <} 1.
     * @since 2.0.0
     */
    BuilderStep withReaderLongPollingStrategy(int requestTimeoutMillis);
  }

  /**
   * Last step : build a new instance.
   *
   * @since 2.0.0
   */
  public interface BuilderStep {

    /**
     * Creates a new instance of {@link RemotePluginClientFactory} using the current configuration.
     *
     * @return A not null reference.
     * @since 2.0.0
     */
    RemotePluginClientFactory build();
  }

  /**
   * (private)<br>
   * The internal step builder.
   */
  private static final class Builder
      implements NodeStep,
          SyncNodePluginStep,
          ServerPushPluginEventStrategyStep,
          SyncNodeReaderStep,
          ServerPushReaderEventStrategyStep,
          BuilderStep {

    private final String remotePluginName;
    private boolean isPluginObservationEnabled;
    private boolean isReaderObservationEnabled;
    private SyncEndpointClientSpi syncEndpoint;
    private ServerPushEventStrategyAdapter syncPluginObservationStrategy;
    private ServerPushEventStrategyAdapter syncReaderObservationStrategy;
    private AsyncEndpointClientSpi asyncEndpoint;
    private int asyncNodeClientTimeoutSeconds;

    private Builder(String remotePluginName) {
      Assert.getInstance().notEmpty(remotePluginName, "remotePluginName");
      this.remotePluginName = remotePluginName;
      this.isPluginObservationEnabled = true;
      this.isReaderObservationEnabled = true;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public SyncNodePluginStep withSyncNode(SyncEndpointClientSpi endpoint) {
      Assert.getInstance().notNull(endpoint, "endpoint");
      this.syncEndpoint = endpoint;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
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
     * @since 2.0.0
     */
    @Override
    public ServerPushPluginEventStrategyStep withPluginObservation() {
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public SyncNodeReaderStep withoutPluginObservation() {
      this.isPluginObservationEnabled = false;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public SyncNodeReaderStep withPluginPollingStrategy(int requestFrequencyMillis) {
      Assert.getInstance().greaterOrEqual(requestFrequencyMillis, 1, "requestFrequencyMillis");
      this.syncPluginObservationStrategy =
          new ServerPushEventStrategyAdapter(Type.POLLING, requestFrequencyMillis);
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public SyncNodeReaderStep withPluginLongPollingStrategy(int requestTimeoutMillis) {
      Assert.getInstance().greaterOrEqual(requestTimeoutMillis, 1, "requestTimeoutMillis");
      this.syncPluginObservationStrategy =
          new ServerPushEventStrategyAdapter(Type.LONG_POLLING, requestTimeoutMillis);
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public ServerPushReaderEventStrategyStep withReaderObservation() {
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public BuilderStep withoutReaderObservation() {
      this.isReaderObservationEnabled = false;
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public BuilderStep withReaderPollingStrategy(int requestFrequencyMillis) {
      Assert.getInstance().greaterOrEqual(requestFrequencyMillis, 1, "requestFrequencyMillis");
      this.syncReaderObservationStrategy =
          new ServerPushEventStrategyAdapter(Type.POLLING, requestFrequencyMillis);
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public BuilderStep withReaderLongPollingStrategy(int requestTimeoutMillis) {
      Assert.getInstance().greaterOrEqual(requestTimeoutMillis, 1, "requestTimeoutMillis");
      this.syncReaderObservationStrategy =
          new ServerPushEventStrategyAdapter(Type.LONG_POLLING, requestTimeoutMillis);
      return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    public RemotePluginClientFactory build() {
      return new RemotePluginClientFactoryAdapter(
          remotePluginName,
          false,
          isPluginObservationEnabled,
          isReaderObservationEnabled,
          syncEndpoint,
          syncPluginObservationStrategy,
          syncReaderObservationStrategy,
          asyncEndpoint,
          asyncNodeClientTimeoutSeconds);
    }
  }
}
