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

import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.junit.BeforeClass;
import org.junit.Test;

public class RemotePluginServerFactoryAdapterTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";

  static RemotePluginServerFactoryAdapter syncFactory;

  static RemotePluginServerFactoryAdapter asyncFactory;
  static AsyncEndpointServerSpi asyncEndpointServerSpi;

  @BeforeClass
  public static void beforeClass() {

    // SYNC
    syncFactory =
        (RemotePluginServerFactoryAdapter)
            RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withSyncNode().build();

    // ASYNC
    asyncEndpointServerSpi = mock(AsyncEndpointServerSpi.class);

    asyncFactory =
        (RemotePluginServerFactoryAdapter)
            RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                .withAsyncNode(asyncEndpointServerSpi)
                .build();
  }

  @Test
  public void getDistributedRemoteApiVersion_shouldReturnANotEmptyValue() {
    assertThat(syncFactory.getDistributedRemoteApiVersion()).isNotEmpty();
    assertThat(asyncFactory.getDistributedRemoteApiVersion()).isNotEmpty();
  }

  @Test
  public void getCommonApiVersion_shouldReturnANotEmptyValue() {
    assertThat(syncFactory.getCommonApiVersion()).isNotEmpty();
    assertThat(asyncFactory.getCommonApiVersion()).isNotEmpty();
  }

  @Test
  public void getRemotePluginName_shouldReturnTheProvidedName() {
    assertThat(syncFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(asyncFactory.getRemotePluginName()).isEqualTo(REMOTE_PLUGIN_NAME);
  }

  @Test
  public void getRemotePlugin_shouldReturnANotNullInstance() {
    assertThat(syncFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginServer.class)
        .isInstanceOf(ObservableRemotePluginServerAdapter.class);
    assertThat(asyncFactory.getRemotePlugin())
        .isInstanceOf(RemotePluginServer.class)
        .isInstanceOf(ObservableRemotePluginServerAdapter.class);
  }
}
