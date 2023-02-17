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

import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.junit.BeforeClass;
import org.junit.Test;

public class RemotePluginServerFactoryBuilderTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";

  static AsyncEndpointServerSpi asyncEndpointServerSpi;

  @BeforeClass
  public static void beforeClass() {
    asyncEndpointServerSpi = mock(AsyncEndpointServerSpi.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_1arg_whenRemotePluginNameIsNull_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder(null).withSyncNode().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_1arg_whenRemotePluginNameIsEmpty_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder("").withSyncNode().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_2args_whenRemotePluginNameIsNull_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder(null, mock(ExecutorService.class))
        .withSyncNode()
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_2args_whenRemotePluginNameIsEmpty_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder("", mock(ExecutorService.class))
        .withSyncNode()
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_2args_whenExecutorServiceIsNull_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME, null).withSyncNode().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAsyncNodeAndEndpointIsNull_shouldThrowIAE() {
    RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withAsyncNode(null).build();
  }

  @Test
  public void builder_whenSyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginServerFactory factory =
        RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME).withSyncNode().build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginServerFactoryAdapter.class);
  }

  @Test
  public void builder_whenAsyncNodeSuccess_shouldReturnANotNullInstance() {
    RemotePluginServerFactory factory =
        RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
            .withAsyncNode(asyncEndpointServerSpi)
            .build();
    assertThat(factory)
        .isInstanceOf(RemotePluginFactorySpi.class)
        .isInstanceOf(RemotePluginServerFactoryAdapter.class);
  }
}
