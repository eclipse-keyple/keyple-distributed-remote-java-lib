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
import static org.eclipse.keyple.distributed.MessageDto.API_LEVEL;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class RemotePoolPluginClientAdapterTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";
  static final String REMOTE_READER_NAME = "REMOTE_READER_NAME";
  static final String LOCAL_READER_NAME = "LOCAL_READER_NAME";

  static final String READER_EVENT_DATA = "READER_EVENT_DATA";
  static final MessageDto READER_EVENT_MSG =
      new MessageDto()
          .setApiLevel(API_LEVEL)
          .setAction(MessageDto.Action.READER_EVENT.name())
          .setBody(READER_EVENT_DATA);

  static final String CMD_DATA = "CMD_DATA";

  static final String SESSION_ID = "SESSION_ID";
  static final String CLIENT_NODE_ID = "CLIENT_NODE_ID";
  static final String SERVER_NODE_ID = "SERVER_NODE_ID";
  static final String RESP_DATA = "RESP_DATA";
  static final MessageDto RESP_MSG =
      new MessageDto()
          .setApiLevel(API_LEVEL)
          .setAction(MessageDto.Action.RESP.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setServerNodeId(SERVER_NODE_ID)
          .setBody(RESP_DATA);

  RemotePoolPluginClientAdapter syncPlugin;
  SyncEndpointClientSpi syncEndpointClientSpi;

  RemotePoolPluginClientAdapter asyncPlugin;
  AsyncEndpointClientSpi asyncEndpointClientSpi;

  @Before
  public void setUp() {

    // SYNC
    syncEndpointClientSpi = mock(SyncEndpointClientSpi.class);
    doReturn(Collections.singletonList(RESP_MSG))
        .when(syncEndpointClientSpi)
        .sendRequest(ArgumentMatchers.<MessageDto>any());

    syncPlugin =
        (RemotePoolPluginClientAdapter)
            ((RemotePluginFactorySpi)
                    RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                        .withSyncNode(syncEndpointClientSpi)
                        .build())
                .getRemotePlugin();

    // ASYNC
    asyncEndpointClientSpi = mock(AsyncEndpointClientSpi.class);

    asyncPlugin =
        (RemotePoolPluginClientAdapter)
            ((RemotePluginFactorySpi)
                    RemotePoolPluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                        .withAsyncNode(asyncEndpointClientSpi, 10)
                        .build())
                .getRemotePlugin();
  }

  @Test
  public void createRemoteReader_shouldCreateARemoteReader() {
    // Sync
    RemoteReaderSpi syncRemoteReaderSpi =
        syncPlugin.createRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
    assertThat(syncRemoteReaderSpi)
        .isInstanceOf(RemoteReaderClient.class)
        .isInstanceOf(RemoteReaderClientAdapter.class);
    RemoteReaderClientAdapter syncRemoteReader = (RemoteReaderClientAdapter) syncRemoteReaderSpi;
    assertThat(syncRemoteReader.getName()).isEqualTo(REMOTE_READER_NAME);
    assertThat(syncRemoteReader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
    assertThat(syncRemoteReader.getNode()).isSameAs(syncPlugin.getNode());
    assertThat(syncRemoteReader.getClientNodeId()).isEqualTo(syncPlugin.getNode().getNodeId());
    assertThat(syncRemoteReader.getSessionId()).isNull();
    // Async
    RemoteReaderSpi asyncRemoteReaderSpi =
        asyncPlugin.createRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
    assertThat(asyncRemoteReaderSpi)
        .isInstanceOf(RemoteReaderClient.class)
        .isInstanceOf(RemoteReaderClientAdapter.class);
    RemoteReaderClientAdapter asyncRemoteReader = (RemoteReaderClientAdapter) asyncRemoteReaderSpi;
    assertThat(asyncRemoteReader.getName()).isEqualTo(REMOTE_READER_NAME);
    assertThat(asyncRemoteReader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
    assertThat(asyncRemoteReader.getNode()).isSameAs(asyncPlugin.getNode());
    assertThat(asyncRemoteReader.getClientNodeId()).isEqualTo(asyncPlugin.getNode().getNodeId());
    assertThat(asyncRemoteReader.getSessionId()).isNull();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void onMessage_whenSync_shouldThrowUOE() {
    syncPlugin.onMessage(READER_EVENT_MSG);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void onMessage_whenAsync_shouldThrowUOE() {
    asyncPlugin.onMessage(READER_EVENT_MSG);
  }

  @Test(expected = IllegalStateException.class)
  public void getAsyncNode_whenSync_shouldThrowISE() {
    syncPlugin.getAsyncNode();
  }

  @Test
  public void getAsyncNode_whenAsync_shouldReturnANotNullInstance() {
    AsyncNodeClient node = asyncPlugin.getAsyncNode();
    assertThat(node).isInstanceOf(AsyncNodeClientAdapter.class);
  }

  @Test
  public void executeRemotely_1Arg_whenSync_shouldInvokeSendRequestOnEndpoint() {
    String response = syncPlugin.executeRemotely(CMD_DATA);
    assertThat(response).isEqualTo(RESP_DATA);
    verify(syncEndpointClientSpi).sendRequest(ArgumentMatchers.<MessageDto>any());
    verifyNoMoreInteractions(syncEndpointClientSpi);
  }

  @Test
  public void executeRemotely_2Args_whenSync_shouldInvokeSendRequestOnEndpoint() {
    String response = syncPlugin.executeRemotely(CMD_DATA, SESSION_ID);
    assertThat(response).isEqualTo(RESP_DATA);
    verify(syncEndpointClientSpi).sendRequest(ArgumentMatchers.<MessageDto>any());
    verifyNoMoreInteractions(syncEndpointClientSpi);
  }

  @Test
  public void getName_shouldReturnTheProvidedName() {
    assertThat(syncPlugin.getName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(asyncPlugin.getName()).isEqualTo(REMOTE_PLUGIN_NAME);
  }

  @Test
  public void getNode_shouldReturnANotNullNode() {
    assertThat(syncPlugin.getNode()).isInstanceOf(SyncNodeClientAdapter.class);
    assertThat(asyncPlugin.getNode())
        .isSameAs(asyncPlugin.getAsyncNode())
        .isInstanceOf(AsyncNodeClientAdapter.class);
  }

  @Test
  public void isBoundToSyncNode_whenSync_shouldReturnTrue() {
    assertThat(syncPlugin.isBoundToSyncNode()).isTrue();
  }

  @Test
  public void isBoundToSyncNode_whenAsync_shouldReturnFalse() {
    assertThat(asyncPlugin.isBoundToSyncNode()).isFalse();
  }
}
