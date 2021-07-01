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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.junit.BeforeClass;
import org.junit.Test;

public class RemotePluginClientFactoryAdapterTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";

  static SyncEndpointClientSpi syncEndpointClientSpi;
  static RemotePluginClientFactoryAdapter syncFactory;
  static RemotePluginClientFactoryAdapter syncPoolFactory;

  static AsyncEndpointClientSpi asyncEndpointClientSpi;
  static RemotePluginClientFactoryAdapter asyncFactory;
  static RemotePluginClientFactoryAdapter asyncPoolFactory;

  @BeforeClass
  public static void beforeClass() {

    // SYNC
    syncEndpointClientSpi = mock(SyncEndpointClientSpi.class);

    syncFactory =
        (RemotePluginClientFactoryAdapter)
            RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                .withSyncNode(syncEndpointClientSpi)
                .withPluginObservation()
                .withPluginPollingStrategy(10000)
                .withReaderObservation()
                .withReaderPollingStrategy(10000)
                .build();

    syncPoolFactory =
        (RemotePluginClientFactoryAdapter)
            RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                .withSyncNode(syncEndpointClientSpi)
                .build();

    // ASYNC
    asyncEndpointClientSpi = mock(AsyncEndpointClientSpi.class);

    asyncFactory =
        (RemotePluginClientFactoryAdapter)
            RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                .withAsyncNode(asyncEndpointClientSpi, 10)
                .build();

    asyncPoolFactory =
        (RemotePluginClientFactoryAdapter)
            RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                .withAsyncNode(asyncEndpointClientSpi, 10)
                .build();
  }

  @Test
  public void getDistributedRemoteApiVersion_shouldReturnANotEmptyValue() {
    assertThat(syncFactory.getDistributedRemoteApiVersion()).isNotEmpty();
    assertThat(syncPoolFactory.getDistributedRemoteApiVersion()).isNotEmpty();
    assertThat(asyncFactory.getDistributedRemoteApiVersion()).isNotEmpty();
    assertThat(asyncPoolFactory.getDistributedRemoteApiVersion()).isNotEmpty();
  }

  @Test
  public void getCommonApiVersion_shouldReturnANotEmptyValue() {
    assertThat(syncFactory.getCommonApiVersion()).isNotEmpty();
    assertThat(syncPoolFactory.getCommonApiVersion()).isNotEmpty();
    assertThat(asyncFactory.getCommonApiVersion()).isNotEmpty();
    assertThat(asyncPoolFactory.getCommonApiVersion()).isNotEmpty();
  }

  @Test
  public void getRemotePluginName_shouldReturnTheProvidedName() {
    assertThat(syncFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(syncPoolFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(asyncFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(asyncPoolFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
  }

  @Test
  public void getRemotePlugin_shouldReturnANotNullInstance() {
    assertThat(syncFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginClient.class)
        .isInstanceOf(ObservableRemotePluginClientAdapter.class);
    assertThat(syncPoolFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginClient.class)
        .isInstanceOf(RemotePoolPluginClientAdapter.class);
    assertThat(asyncFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginClient.class)
        .isInstanceOf(ObservableRemotePluginClientAdapter.class);
    assertThat(asyncPoolFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginClient.class)
        .isInstanceOf(RemotePoolPluginClientAdapter.class);
  }
}
