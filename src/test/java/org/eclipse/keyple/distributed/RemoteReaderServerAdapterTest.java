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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.eclipse.keyple.core.util.json.BodyError;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

public class RemoteReaderServerAdapterTest {

  static final String REMOTE_READER_NAME = "REMOTE_READER_NAME";
  static final String LOCAL_READER_NAME = "LOCAL_READER_NAME";
  static final String SESSION_ID = "SESSION_ID";
  static final String CLIENT_NODE_ID = "CLIENT_NODE_ID";
  static final String SERVER_NODE_ID = "SERVER_NODE_ID";
  static final String SERVICE_ID = "SERVICE_ID";

  static final String CONTENT = "CONTENT";
  static final String DATA_IN = "DATA_IN";

  static final CardContent CARD_CONTENT = new CardContent(CONTENT);
  static final InputData INPUT_DATA = new InputData(DATA_IN);

  static final String INITIAL_CARD_CONTENT_JSON = JsonUtil.toJson(CARD_CONTENT);
  static final String INITIAL_CARD_CONTENT_CLASS_NAME = CARD_CONTENT.getClass().getName();

  static final String INPUT_DATA_JSON = JsonUtil.toJson(INPUT_DATA);

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

  static final String CMD_DATA = "CMD_DATA";
  static final MessageDto CMD_MSG =
      new MessageDto()
          .setAction(MessageDto.Action.CMD.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setLocalReaderName(LOCAL_READER_NAME)
          .setRemoteReaderName(REMOTE_READER_NAME)
          .setBody(CMD_DATA);

  static final String BAD_CMD_DATA = "BAD_CMD_DATA";
  static final MessageDto BAD_CMD_MSG =
      new MessageDto()
          .setAction(MessageDto.Action.CMD.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setLocalReaderName(LOCAL_READER_NAME)
          .setRemoteReaderName(REMOTE_READER_NAME)
          .setBody(BAD_CMD_DATA);

  static final String RESP_DATA = "RESP_DATA";
  static final MessageDto RESP_MSG =
      new MessageDto()
          .setAction(MessageDto.Action.RESP.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setServerNodeId(SERVER_NODE_ID)
          .setBody(RESP_DATA);

  static final String ERROR_DETAIL_MESSAGE = "ERROR_DETAIL";
  static final String ERROR_DATA =
      JsonUtil.toJson(new BodyError(new IllegalArgumentException(ERROR_DETAIL_MESSAGE)));
  static final MessageDto ERROR_MSG =
      new MessageDto()
          .setAction(MessageDto.Action.ERROR.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setServerNodeId(SERVER_NODE_ID)
          .setBody(ERROR_DATA);

  AbstractNodeAdapter node;
  RemoteReaderServerAdapter reader;

  private void initReader(
      String initialCardContentJson, String initialCardContentClassName, String inputDataJson) {
    reader =
        new RemoteReaderServerAdapter(
            REMOTE_READER_NAME,
            LOCAL_READER_NAME,
            SESSION_ID,
            CLIENT_NODE_ID,
            node,
            SERVICE_ID,
            initialCardContentJson,
            initialCardContentClassName,
            inputDataJson,
            false);
  }

  private void initSimpleReader() {
    initReader(null, null, null);
  }

  private ArgumentMatcher<MessageDto> getMessageDtoMatcher(final String cmdData) {
    return new ArgumentMatcher<MessageDto>() {
      @Override
      public boolean matches(MessageDto argument) {
        return MessageDto.Action.CMD.name().equals(argument.getAction())
            && SESSION_ID.equals(argument.getSessionId())
            && CLIENT_NODE_ID.equals(argument.getClientNodeId())
            && LOCAL_READER_NAME.equals(argument.getLocalReaderName())
            && REMOTE_READER_NAME.equals(argument.getRemoteReaderName())
            && cmdData.equals(argument.getBody());
      }
    };
  }

  @Before
  public void setUp() {
    node = mock(AbstractNodeAdapter.class);
    doReturn(RESP_MSG)
        .when(node)
        .sendRequest(ArgumentMatchers.argThat(getMessageDtoMatcher(CMD_DATA)));
    doReturn(ERROR_MSG)
        .when(node)
        .sendRequest(ArgumentMatchers.argThat(getMessageDtoMatcher(BAD_CMD_DATA)));
  }

  @Test
  public void getLocalReaderName_shouldReturnTheProvidedName() {
    initSimpleReader();
    assertThat(reader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
  }

  @Test
  public void getSessionId_shouldReturnTheProvidedSessionId() {
    initSimpleReader();
    assertThat(reader.getSessionId()).isEqualTo(SESSION_ID);
  }

  @Test
  public void getClientNodeId_shouldReturnTheProvidedClientNodeId() {
    initSimpleReader();
    assertThat(reader.getClientNodeId()).isEqualTo(CLIENT_NODE_ID);
  }

  @Test
  public void getNode_shouldReturnTheProvidedNode() {
    initSimpleReader();
    assertThat(reader.getNode()).isSameAs(node);
  }

  @Test
  public void getName_shouldReturnTheProvidedRemoteReaderName() {
    initSimpleReader();
    assertThat(reader.getName()).isEqualTo(REMOTE_READER_NAME);
  }

  @Test
  public void executeRemotely_shouldInvokeSendRequestOnNode() {
    initSimpleReader();
    String response = reader.executeRemotely(CMD_DATA);
    assertThat(response).isEqualTo(RESP_DATA);
  }

  @Test
  public void executeRemotely_whenBadCommand_shouldInvokeSendRequestOnNodeAndThrowRE() {
    initSimpleReader();
    try {
      reader.executeRemotely(BAD_CMD_DATA);
      shouldHaveThrown(RuntimeException.class);
    } catch (RuntimeException e) {
      assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
      assertThat(e.getCause().getMessage()).isEqualTo(ERROR_DETAIL_MESSAGE);
    }
  }

  @Test
  public void getServiceId_shouldReturnTheProvidedServiceId() {
    initSimpleReader();
    assertThat(reader.getServiceId()).isEqualTo(SERVICE_ID);
  }

  @Test
  public void getInitialCardContent_whenCardContentIsNotProvided_shouldReturnNull() {
    initReader(null, null, null);
    assertThat(reader.getInitialCardContent()).isNull();
  }

  @Test
  public void getInitialCardContent_whenCardContentIsProvided_shouldReturnANotNullInstance() {
    initReader(INITIAL_CARD_CONTENT_JSON, INITIAL_CARD_CONTENT_CLASS_NAME, null);
    assertThat(reader.getInitialCardContent())
        .isInstanceOf(CardContent.class)
        .isEqualToComparingFieldByField(CARD_CONTENT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getInputData_whenClassIsNull_shouldThrowIAE() {
    initReader(null, null, null);
    reader.getInputData(null);
  }

  @Test
  public void getInputData_whenInputDataIsNotProvided_shouldReturnNull() {
    initReader(null, null, null);
    assertThat(reader.getInputData(InputData.class)).isNull();
  }

  @Test
  public void getInputData_whenInputDataIsProvidedButWrongType_shouldReturnAMalformedInstance() {
    initReader(null, null, INPUT_DATA_JSON);
    CardContent inputData = reader.getInputData(CardContent.class);
    assertThat(inputData).isNotNull();
    assertThat(inputData.content).isNull();
  }

  @Test
  public void getInputData_whenInputDataIsProvidedAndGoodType_shouldReturnANotNullInstance() {
    initReader(null, null, INPUT_DATA_JSON);
    assertThat(reader.getInputData(InputData.class)).isEqualToComparingFieldByField(INPUT_DATA);
  }
}
