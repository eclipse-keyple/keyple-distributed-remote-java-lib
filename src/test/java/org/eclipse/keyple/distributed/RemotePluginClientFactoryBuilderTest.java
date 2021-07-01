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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.junit.BeforeClass;
import org.junit.Test;

public class RemotePluginClientFactoryBuilderTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";

  static SyncEndpointClientSpi syncEndpointClientSpi;
  static AsyncEndpointClientSpi asyncEndpointClientSpi;

  @BeforeClass
  public static void beforeClass() {
    syncEndpointClientSpi = mock(SyncEndpointClientSpi.class);
    asyncEndpointClientSpi = mock(AsyncEndpointClientSpi.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenRemotePluginNameIsNull_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(null)
        .withAsyncNode(asyncEndpointClientSpi, 10)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenRemotePluginNameIsEmpty_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder("").withAsyncNode(asyncEndpointClientSpi, 10).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndEndpointIsNull_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withSyncNode(null)
        .withoutPluginObservation()
        .withoutReaderObservation()
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndPluginObservationAndPollingIsLessThan1_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withSyncNode(syncEndpointClientSpi)
        .withPluginObservation()
        .withPluginPollingStrategy(0)
        .withoutReaderObservation()
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndPluginObservationAndLongPollingIsLessThan1_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withSyncNode(syncEndpointClientSpi)
        .withPluginObservation()
        .withPluginLongPollingStrategy(0)
        .withoutReaderObservation()
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndReaderObservationAndPollingIsLessThan1_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withSyncNode(syncEndpointClientSpi)
        .withoutPluginObservation()
        .withReaderObservation()
        .withReaderPollingStrategy(0)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndReaderObservationAndLongPollingIsLessThan1_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withSyncNode(syncEndpointClientSpi)
        .withoutPluginObservation()
        .withReaderObservation()
        .withReaderLongPollingStrategy(0)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAsyncNodeAndEndpointIsNull_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withAsyncNode(null, 1).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAsyncNodeAndTimeoutIsLessThan1_shouldThrowIAE() {
    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withAsyncNode(asyncEndpointClientSpi, 0)
        .build();
  }

  @Test
  public void builder_whenSyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginClientFactory factory =
        RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
            .withSyncNode(syncEndpointClientSpi)
            .withPluginObservation()
            .withPluginPollingStrategy(1)
            .withReaderObservation()
            .withReaderPollingStrategy(1)
            .build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginClientFactoryAdapter.class);
  }

  @Test
  public void builder_whenAsyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginClientFactory factory =
        RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
            .withAsyncNode(asyncEndpointClientSpi, 1)
            .build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginClientFactoryAdapter.class);
  }
}
