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

import com.google.gson.JsonObject;
import java.util.concurrent.ExecutorService;
import org.eclipse.keyple.core.distributed.remote.ObservableRemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.RemotePluginApi;
import org.eclipse.keyple.core.distributed.remote.spi.RemotePluginFactorySpi;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

public class ObservableRemotePluginServerAdapterTest {

  static final String REMOTE_PLUGIN_NAME = "REMOTE_PLUGIN_NAME";
  static final String REMOTE_READER_NAME = "REMOTE_READER_NAME";

  static final String SESSION_ID = "SESSION_ID";
  static final String CLIENT_NODE_ID = "CLIENT_NODE_ID";
  static final String LOCAL_READER_NAME = "LOCAL_READER_NAME";

  static final String UNKNOWN = "UNKNOWN";

  static final String CONTENT = "CONTENT";
  static final String DATA_IN = "DATA_IN";
  static final String DATA_OUT = "DATA_OUT";

  static final String CMD_DATA = "CMD_DATA";
  static final MessageDto CMD_MSG =
      new MessageDto().setAction(MessageDto.Action.CMD.name()).setBody(CMD_DATA);

  static final ExecutorService executorService = mock(ExecutorService.class);

  ObservableRemotePluginServerAdapter syncPlugin;
  RemotePluginApi syncRemotePluginApi;
  ObservableRemotePluginApi syncObservableRemotePluginApi;

  ObservableRemotePluginServerAdapter asyncPlugin;
  RemotePluginApi asyncRemotePluginApi;
  ObservableRemotePluginApi asyncObservableRemotePluginApi;
  AsyncEndpointServerSpi asyncEndpointServerSpi;

  static final int CLIENT_CORE_API_LEVEL = 2;
  static final String SERVICE_ID = "SERVICE_ID";
  static final CardContent CARD_CONTENT = new CardContent(CONTENT);
  static final InputData INPUT_DATA = new InputData(DATA_IN);

  static class CardContent {

    private final String content;

    public CardContent(String content) {
      this.content = content;
    }
  }

  static class InputData {

    private final String data;

    public InputData(String data) {
      this.data = data;
    }
  }

  /**
   * Builds a message associated to the {@link MessageDto.Action#EXECUTE_REMOTE_SERVICE} action.
   *
   * @param initialCardContent The initial card content if needed.
   * @param inputData The additional information if needed.
   * @param isLegacyMode True if the communication is in legacy mode.
   * @return A not null reference.
   */
  private MessageDto buildMessage(
      Object initialCardContent, Object inputData, boolean isLegacyMode) {

    JsonObject body = new JsonObject();

    // Client Core API level
    if (!isLegacyMode) {
      body.addProperty(MessageDto.JsonProperty.CORE_API_LEVEL.getKey(), CLIENT_CORE_API_LEVEL);
    }

    // Service ID
    if (isLegacyMode) {
      body.addProperty(MessageDto.JsonProperty.SERVICE_ID.name(), SERVICE_ID);
    } else {
      body.addProperty(MessageDto.JsonProperty.SERVICE_ID.getKey(), SERVICE_ID);
    }

    // Is reader contactless?
    if (!isLegacyMode) {
      body.addProperty(MessageDto.JsonProperty.IS_READER_CONTACTLESS.getKey(), true);
    }

    // Initial card content
    if (initialCardContent != null) {
      if (isLegacyMode) {
        body.addProperty(
            MessageDto.JsonProperty.INITIAL_CARD_CONTENT.name(),
            JsonUtil.toJson(initialCardContent));
        body.addProperty(
            MessageDto.JsonProperty.INITIAL_CARD_CONTENT_CLASS_NAME.name(),
            initialCardContent.getClass().getName());
      } else {
        body.add(
            MessageDto.JsonProperty.INITIAL_CARD_CONTENT.getKey(),
            JsonUtil.getParser().toJsonTree(initialCardContent));
        body.addProperty(
            MessageDto.JsonProperty.INITIAL_CARD_CONTENT_CLASS_NAME.getKey(),
            initialCardContent.getClass().getName());
      }
    }

    // Input data
    if (inputData != null) {
      if (isLegacyMode) {
        body.addProperty(MessageDto.JsonProperty.INPUT_DATA.name(), JsonUtil.toJson(inputData));
      } else {
        body.add(
            MessageDto.JsonProperty.INPUT_DATA.getKey(),
            JsonUtil.getParser().toJsonTree(inputData));
      }
    }

    return new MessageDto()
        .setApiLevel(isLegacyMode ? 0 : MessageDto.API_LEVEL)
        .setAction(MessageDto.Action.EXECUTE_REMOTE_SERVICE.name())
        .setSessionId(SESSION_ID)
        .setClientNodeId(CLIENT_NODE_ID)
        .setLocalReaderName(LOCAL_READER_NAME)
        .setBody(body.toString());
  }

  private ArgumentMatcher<RemoteReaderServerAdapter> getRemoteReaderServerAdapterMatcher(
      final AbstractNodeAdapter node,
      final boolean withInitialCardContent,
      final boolean withInputData) {
    return new ArgumentMatcher<RemoteReaderServerAdapter>() {
      @Override
      public boolean matches(RemoteReaderServerAdapter argument) {
        return SESSION_ID.equals(argument.getSessionId())
            && CLIENT_NODE_ID.equals(argument.getClientNodeId())
            && LOCAL_READER_NAME.equals(argument.getLocalReaderName())
            && node == argument.getNode()
            && SERVICE_ID.equals(argument.getServiceId())
            && (argument.isContactless() == null || argument.isContactless())
            && (withInitialCardContent
                ? (argument.getInitialCardContent() instanceof CardContent
                    && ((CardContent) argument.getInitialCardContent()).content.equals(CONTENT))
                : (argument.getInitialCardContent() == null))
            && (withInputData
                ? (argument.getInputData(InputData.class) != null
                    && argument.getInputData(InputData.class).data.equals(DATA_IN))
                : (argument.getInputData(InputData.class) == null));
      }
    };
  }

  @Before
  public void setUp() {

    // SYNC
    syncPlugin =
        (ObservableRemotePluginServerAdapter)
            ((RemotePluginFactorySpi)
                    RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME, executorService)
                        .withSyncNode()
                        .build())
                .getRemotePlugin();

    syncRemotePluginApi = mock(RemotePluginApi.class);

    syncObservableRemotePluginApi = mock(ObservableRemotePluginApi.class);

    syncPlugin.connect(syncRemotePluginApi);
    syncPlugin.connect(syncObservableRemotePluginApi);

    // ASYNC
    asyncEndpointServerSpi = mock(AsyncEndpointServerSpi.class);

    asyncPlugin =
        (ObservableRemotePluginServerAdapter)
            ((RemotePluginFactorySpi)
                    RemotePluginServerFactoryBuilder.builder(REMOTE_PLUGIN_NAME, executorService)
                        .withAsyncNode(asyncEndpointServerSpi)
                        .build())
                .getRemotePlugin();

    asyncRemotePluginApi = mock(RemotePluginApi.class);

    asyncObservableRemotePluginApi = mock(ObservableRemotePluginApi.class);

    asyncPlugin.connect(asyncRemotePluginApi);
    asyncPlugin.connect(asyncObservableRemotePluginApi);
  }

  @Test
  public void getName_shouldReturnTheProvidedName() {
    assertThat(syncPlugin.getName()).isEqualTo(REMOTE_PLUGIN_NAME);
    assertThat(asyncPlugin.getName()).isEqualTo(REMOTE_PLUGIN_NAME);
  }

  @Test
  public void getExecutorService_shouldReturnTheProvidedExecutorService() {
    assertThat(syncPlugin.getExecutorService()).isEqualTo(executorService);
    assertThat(asyncPlugin.getExecutorService()).isEqualTo(executorService);
  }

  @Test
  public void executeRemotely_whenSync_shouldReturnNull() {
    assertThat(syncPlugin.executeRemotely("")).isNull();
  }

  @Test
  public void getNode_shouldReturnANotNullNode() {
    assertThat(syncPlugin.getNode()).isInstanceOf(SyncNodeServerAdapter.class);
    assertThat(asyncPlugin.getNode())
        .isSameAs(asyncPlugin.getAsyncNode())
        .isInstanceOf(AsyncNodeServerAdapter.class);
  }

  @Test
  public void isBoundToSyncNode_whenSync_shouldReturnTrue() {
    assertThat(syncPlugin.isBoundToSyncNode()).isTrue();
  }

  @Test
  public void isBoundToSyncNode_whenAsync_shouldReturnFalse() {
    assertThat(asyncPlugin.isBoundToSyncNode()).isFalse();
  }

  @Test(expected = IllegalStateException.class)
  public void getSyncNode_whenAsync_shouldThrowISE() {
    asyncPlugin.getSyncNode();
  }

  @Test
  public void getSyncNode_whenSync_shouldReturnANotNullInstance() {
    SyncNodeServer node = syncPlugin.getSyncNode();
    assertThat(node).isInstanceOf(SyncNodeServerAdapter.class);
  }

  @Test(expected = IllegalStateException.class)
  public void getAsyncNode_whenSync_shouldThrowISE() {
    syncPlugin.getAsyncNode();
  }

  @Test
  public void getAsyncNode_whenAsync_shouldReturnANotNullInstance() {
    AsyncNodeServer node = asyncPlugin.getAsyncNode();
    assertThat(node).isInstanceOf(AsyncNodeServerAdapter.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenSyncAndReaderNameIsNull_shouldThrowIAE() {
    syncPlugin.endRemoteService(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenAsyncAndReaderNameIsNull_shouldThrowIAE() {
    asyncPlugin.endRemoteService(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenSyncAndReaderNameIsEmpty_shouldThrowIAE() {
    syncPlugin.endRemoteService("", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenAsyncAndReaderNameIsEmpty_shouldThrowIAE() {
    asyncPlugin.endRemoteService("", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenSyncAndReaderNameIsUnknown_shouldThrowIAE() {
    syncPlugin.endRemoteService(UNKNOWN, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void endRemoteService_whenAsyncAndReaderNameIsUnknown_shouldThrowIAE() {
    asyncPlugin.endRemoteService(UNKNOWN, null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void createRemoteReader_whenSync_ShouldThrowUOE() {
    syncPlugin.createRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void createRemoteReader_whenAsync_ShouldThrowUOE() {
    asyncPlugin.createRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void createObservableRemoteReader_whenSync_ShouldThrowUOE() {
    syncPlugin.createObservableRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void createObservableRemoteReader_whenAsync_ShouldThrowUOE() {
    asyncPlugin.createObservableRemoteReader(REMOTE_READER_NAME, LOCAL_READER_NAME);
  }

  @Test(expected = IllegalStateException.class)
  public void onMessage_whenActionIsNotExecuteRemoteService_shouldThrowISE() {
    syncPlugin.onMessage(CMD_MSG);
  }

  @Test
  public void
      onMessage_whenNoCardContentAndInputDataAreProvided_shouldCreateARemoteReaderAndInvokeTheApi() {
    syncPlugin.onMessage(buildMessage(null, null, false));
    verify(syncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(syncPlugin.getNode(), false, false)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(buildMessage(null, null, false));
    verify(asyncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(asyncPlugin.getNode(), false, false)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void onMessage_whenNoCardContentIsProvided_shouldCreateARemoteReaderAndInvokeTheApi() {
    syncPlugin.onMessage(buildMessage(null, INPUT_DATA, false));
    verify(syncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(syncPlugin.getNode(), false, true)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(buildMessage(null, INPUT_DATA, false));
    verify(asyncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(asyncPlugin.getNode(), false, true)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void onMessage_whenNoInputDataIsProvided_shouldCreateARemoteReaderAndInvokeTheApi() {
    syncPlugin.onMessage(buildMessage(CARD_CONTENT, null, false));
    verify(syncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(syncPlugin.getNode(), true, false)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(buildMessage(CARD_CONTENT, null, false));
    verify(asyncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(asyncPlugin.getNode(), true, false)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void onMessage_whenAllInfoAreProvided_shouldCreateARemoteReaderAndInvokeTheApi() {
    syncPlugin.onMessage(buildMessage(CARD_CONTENT, INPUT_DATA, false));
    verify(syncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(syncPlugin.getNode(), true, true)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(buildMessage(CARD_CONTENT, INPUT_DATA, false));
    verify(asyncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(asyncPlugin.getNode(), true, true)),
            eq(CLIENT_CORE_API_LEVEL));
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }

  @Test
  public void
      onMessage_whenAllInfoAreProvidedAndLegacyMode_shouldCreateARemoteReaderAndInvokeTheApi() {
    syncPlugin.onMessage(buildMessage(CARD_CONTENT, INPUT_DATA, true));
    verify(syncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(syncPlugin.getNode(), true, true)),
            eq(-1));
    verifyNoMoreInteractions(syncObservableRemotePluginApi);
    asyncPlugin.onMessage(buildMessage(CARD_CONTENT, INPUT_DATA, true));
    verify(asyncObservableRemotePluginApi)
        .addRemoteReader(
            ArgumentMatchers.argThat(
                getRemoteReaderServerAdapterMatcher(asyncPlugin.getNode(), true, true)),
            eq(-1));
    verifyNoMoreInteractions(asyncObservableRemotePluginApi);
  }
}
