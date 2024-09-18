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
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.eclipse.keyple.distributed.MessageDto.API_LEVEL;
import static org.mockito.Mockito.*;

import org.eclipse.keyple.core.util.json.BodyError;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

public class RemoteReaderClientAdapterTest {

  static final int CLIENT_CORE_API_LEVEL = 2;
  private static final String REMOTE_READER_NAME = "REMOTE_READER_NAME";
  private static final String LOCAL_READER_NAME = "LOCAL_READER_NAME";
  private static final String SESSION_ID = "SESSION_ID";
  private static final String CLIENT_NODE_ID = "CLIENT_NODE_ID";
  private static final String SERVER_NODE_ID = "SERVER_NODE_ID";

  static final String CMD_DATA = "CMD_DATA";
  static final MessageDto CMD_MSG =
      new MessageDto()
          .setApiLevel(API_LEVEL)
          .setAction(MessageDto.Action.CMD.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setLocalReaderName(LOCAL_READER_NAME)
          .setRemoteReaderName(REMOTE_READER_NAME)
          .setBody(CMD_DATA);

  static final String BAD_CMD_DATA = "BAD_CMD_DATA";
  static final MessageDto BAD_CMD_MSG =
      new MessageDto()
          .setApiLevel(API_LEVEL)
          .setAction(MessageDto.Action.CMD.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setLocalReaderName(LOCAL_READER_NAME)
          .setRemoteReaderName(REMOTE_READER_NAME)
          .setBody(BAD_CMD_DATA);

  static final String RESP_DATA = "RESP_DATA";
  static final MessageDto RESP_MSG =
      new MessageDto()
          .setApiLevel(API_LEVEL)
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
          .setApiLevel(API_LEVEL)
          .setAction(MessageDto.Action.ERROR.name())
          .setSessionId(SESSION_ID)
          .setClientNodeId(CLIENT_NODE_ID)
          .setServerNodeId(SERVER_NODE_ID)
          .setBody(ERROR_DATA);

  AbstractNodeAdapter node;
  RemoteReaderClientAdapter reader;

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

    reader =
        new RemoteReaderClientAdapter(
            CLIENT_CORE_API_LEVEL,
            REMOTE_READER_NAME,
            LOCAL_READER_NAME,
            SESSION_ID,
            CLIENT_NODE_ID,
            node);
  }

  @Test
  public void getLocalReaderName_shouldReturnTheProvidedName() {
    assertThat(reader.getLocalReaderName()).isEqualTo(LOCAL_READER_NAME);
  }

  @Test
  public void getSessionId_shouldReturnTheProvidedSessionId() {
    assertThat(reader.getSessionId()).isEqualTo(SESSION_ID);
  }

  @Test
  public void getClientNodeId_shouldReturnTheProvidedClientNodeId() {
    assertThat(reader.getClientNodeId()).isEqualTo(CLIENT_NODE_ID);
  }

  @Test
  public void getNode_shouldReturnTheProvidedNode() {
    assertThat(reader.getNode()).isSameAs(node);
  }

  @Test
  public void getName_shouldReturnTheProvidedRemoteReaderName() {
    assertThat(reader.getName()).isEqualTo(REMOTE_READER_NAME);
  }

  @Test
  public void executeRemotely_shouldInvokeSendRequestOnNode() {
    String response = reader.executeRemotely(CMD_DATA);
    assertThat(response).isEqualTo(RESP_DATA);
  }

  @Test
  public void executeRemotely_whenBadCommand_shouldInvokeSendRequestOnNodeAndThrowRE() {
    try {
      reader.executeRemotely(BAD_CMD_DATA);
      shouldHaveThrown(RuntimeException.class);
    } catch (RuntimeException e) {
      assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
      assertThat(e.getCause().getMessage()).isEqualTo(ERROR_DETAIL_MESSAGE);
    }
  }
}
