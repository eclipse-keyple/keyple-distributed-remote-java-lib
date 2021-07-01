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

public class RemotePoolPluginClientFactoryBuilderTest {

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
    RemotePoolPluginClientFactoryBuilder.builder(null)
        .withAsyncNode(asyncEndpointClientSpi, 10)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenRemotePluginNameIsEmpty_shouldThrowIAE() {
    RemotePoolPluginClientFactoryBuilder.builder("")
        .withAsyncNode(asyncEndpointClientSpi, 10)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenSyncNodeAndEndpointIsNull_shouldThrowIAE() {
    RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withSyncNode(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAsyncNodeAndEndpointIsNull_shouldThrowIAE() {
    RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withAsyncNode(null, 1).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAsyncNodeAndTimeoutIsLessThan1_shouldThrowIAE() {
    RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
        .withAsyncNode(asyncEndpointClientSpi, 0)
        .build();
  }

  @Test
  public void builder_whenSyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginClientFactory factory =
        RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
            .withSyncNode(syncEndpointClientSpi)
            .build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginClientFactoryAdapter.class);
  }

  @Test
  public void builder_whenAsyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginClientFactory factory =
        RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
            .withAsyncNode(asyncEndpointClientSpi, 1)
            .build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginClientFactoryAdapter.class);
  }
}
