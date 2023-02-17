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
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.RemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.ObservableRemoteReaderSpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;
import org.eclipse.keyple.core.distributed.remote.spi.RemoteReaderSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class ObservableRemotePluginClientAdapterTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";
  static final String REMOTE_READER_NAME = "REMOTE_READER_NAME";
  static final String LOCAL_READER_NAME = "LOCAL_READER_NAME";

  static final String PLUGIN_EVENT_DATA = "PLUGIN_EVENT_DATA";
  static final MessageDto PLUGIN_EVENT_MSG =
      new MessageDto().setAction(MessageDto.Action.PLUGIN_EVENT.name()).setBody(PLUGIN_EVENT_DATA);

  static final String READER_EVENT_DATA = "READER_EVENT_DATA";
  static final MessageDto READER_EVENT_MSG =
      new MessageDto().setAction(MessageDto.Action.READER_EVENT.name()).setBody(READER_EVENT_DATA);

  static final String CMD_DATA = "CMD_DATA";
  static final MessageDto CMD_MSG =
      new MessageDto().setAction(MessageDto.Action.CMD.name()).setBody(CMD_DATA);

  static final String SESSION_ID = "SESSION_ID";
  static final String CLIENT_NODE_ID = "CLIENT_NODE_ID";
  static final String SERVER_NODE_ID = "SERVER_NODE_ID";
  static final String RESP_DATA = "RESP_DATA";
  static final MessageDto RESP_MSG =
      new MessageDto()
          .setAction(MessageDto.Action.RESP.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setServerNodeId(SERVER_NODE_ID)
          .setBody(RESP_DATA);

  ObservableRemotePluginClientAdapter syncPlugin;
  SyncEndpointClientSpi syncEndpointClientSpi;
  RemotePluginApi syncRemotePluginApi;
  ObservableRemotePluginApi syncObservableRemotePluginApi;

  ObservableRemotePluginClientAdapter asyncPlugin;
  AsyncEndpointClientSpi asyncEndpointClientSpi;
  RemotePluginApi asyncRemotePluginApi;
  ObservableRemotePluginApi asyncObservableRemotePluginApi;

  @Before
  public void setUp() {

    // SYNC
    syncEndpointClientSpi = mock(SyncEndpointClientSpi.class);
    doReturn(Collections.singletonList(RESP_MSG))
        .when(syncEndpointClientSpi)
        .sendRequest(ArgumentMatchers.<MessageDto>any());

    syncPlugin =
        (ObservableRemotePluginClientAdapter)
            ((RemotePluginFactorySpi)
                    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                        .withSyncNode(syncEndpointClientSpi)
                        .withPluginObservation()
                        .withPluginPollingStrategy(10000)
                        .withReaderObservation()
                        .withReaderPollingStrategy(10000)
                        .build())
                .getRemotePlugin();

    syncRemotePluginApi = mock(RemotePluginApi.class);

    syncObservableRemotePluginApi = mock(ObservableRemotePluginApi.class);

    syncPlugin.connect(syncRemotePluginApi);
    syncPlugin.connect(syncObservableRemotePluginApi);

    // ASYNC
    asyncEndpointClientSpi = mock(AsyncEndpointClientSpi.class);

    asyncPlugin =
        (ObservableRemotePluginClientAdapter)
            ((RemotePluginFactorySpi)
                    RemotePluginClientFactoryBuilder.builder(REMOTE_PLUGIN_NAME)
                        .withAsyncNode(asyncEndpointClientSpi, 10)
                        .build())
                .getRemotePlugin();

    asyncRemotePluginApi = mock(RemotePluginApi.class);

    asyncObservableRemotePluginApi = mock(ObservableRemotePluginApi.class);

    asyncPlugin.connect(asyncRemotePluginApi);
    asyncPlugin.connect(asyncObservableRemotePluginApi);
  }

  @Test
  public void getExecutorService_shouldReturnNull() {
    assertThat(syncPlugin.getExecutorService()).isNull();
    assertThat(asyncPlugin.getExecutorService()).isNull();
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

  @Test
  public void createObservableRemoteReader_shouldCreateAnObservableRemoteReader() {
    // Sync
    ObservableRemoteReaderSpi syncRemoteReaderSpi =
        syncPlugin.createObservableRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
    assertThat(syncRemoteReaderSpi)
        .isInstanceOf(RemoteReaderClient.class)
        .isInstanceOf(ObservableRemoteReaderClientAdapter.class);
    ObservableRemoteReaderClientAdapter syncRemoteReader =
        (ObservableRemoteReaderClientAdapter) syncRemoteReaderSpi;
    assertThat(syncRemoteReader.getName()).isEqualTo(REMOTE_READER_NAME);
    assertThat(syncRemoteReader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
    assertThat(syncRemoteReader.getNode()).isSameAs(syncPlugin.getNode());
    assertThat(syncRemoteReader.getClientNodeId()).isEqualTo(syncPlugin.getNode().getNodeId());
    assertThat(syncRemoteReader.getSessionId()).isNull();
    // Async
    RemoteReaderSpi asyncRemoteReaderSpi =
        asyncPlugin.createObservableRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
    assertThat(asyncRemoteReaderSpi)
        .isInstanceOf(RemoteReaderClient.class)
        .isInstanceOf(ObservableRemoteReaderClientAdapter.class);
    ObservableRemoteReaderClientAdapter asyncRemoteReader =
        (ObservableRemoteReaderClientAdapter) asyncRemoteReaderSpi;
    assertThat(asyncRemoteReader.getName()).isEqualTo(REMOTE_READER_NAME);
    assertThat(asyncRemoteReader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
    assertThat(asyncRemoteReader.getNode()).isSameAs(asyncPlugin.getNode());
    assertThat(asyncRemoteReader.getClientNodeId()).isEqualTo(asyncPlugin.getNode().getNodeId());
    assertThat(asyncRemoteReader.getSessionId()).isNull();
  }

  @Test
  public void onMessage_whenActionIsPluginEvent_shouldInvokeOnPluginEventOnObservableApi() {
    syncPlugin.onMessage(PLUGIN_EVENT_MSG);
    verifyZeroInteractions(syncRemotePluginApi);
    verify(syncObservableRemotePluginApi).onPluginEvent(PLUGIN_EVENT_DATA);
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(PLUGIN_EVENT_MSG);
    verifyZeroInteractions(asyncRemotePluginApi);
    verify(asyncObservableRemotePluginApi).onPluginEvent(PLUGIN_EVENT_DATA);
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void onMessage_whenActionIsReaderEvent_shouldInvokeOnReaderEventOnApi() {
    syncPlugin.onMessage(READER_EVENT_MSG);
    verify(syncRemotePluginApi).onReaderEvent(READER_EVENT_DATA);
    verifyNoMoreInteractions(syncRemotePluginApi);
    verifyZeroInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(READER_EVENT_MSG);
    verify(asyncRemotePluginApi).onReaderEvent(READER_EVENT_DATA);
    verifyNoMoreInteractions(asyncRemotePluginApi);
    verifyZeroInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void onMessage_whenActionIsOther_shouldDoNothing() {
    syncPlugin.onMessage(CMD_MSG);
    verifyZeroInteractions(syncRemotePluginApi);
    verifyZeroInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(CMD_MSG);
    verifyZeroInteractions(asyncRemotePluginApi);
    verifyZeroInteractions(asyncObservableRemotePluginApi);
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

  @Test
  public void onStartObservation_whenAsync_shouldDoNothing() {
    asyncPlugin.onStartObservation();
    verifyZeroInteractions(asyncEndpointClientSpi);
  }

  @Test
  public void onStopObservation_whenAsync_shouldDoNothing() {
    asyncPlugin.onStopObservation();
    verifyZeroInteractions(asyncEndpointClientSpi);
  }
}
