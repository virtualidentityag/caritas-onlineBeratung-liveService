package de.caritas.cob.liveservice.websocket.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.liveservice.websocket.stomphandler.StompHandlerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class ClientInboundChannelInterceptorTest {

  private MockedStatic<MessageHeaderAccessor> mockedMessageHeaderAccessor;

  @InjectMocks
  private ClientInboundChannelInterceptor clientInboundChannelInterceptor;

  @Mock
  private StompHandlerRegistry stompHandlerRegistry;

  @Mock
  private MessageHeaders messageHeaders;

  @Mock
  private StompHeaderAccessor stompHeaderAccessor;

  @Mock
  private Message<?> message;

  @BeforeEach
  public void initMocks() {
    when(messageHeaders.get(anyString())).thenReturn("header");
    when(message.getHeaders()).thenReturn(messageHeaders);
    mockedMessageHeaderAccessor.when(() -> MessageHeaderAccessor.getAccessor(any(Message.class), eq(StompHeaderAccessor.class)))
        .thenReturn(stompHeaderAccessor);
  }

  @BeforeEach
  void setUpStaticMocks() {
    mockedMessageHeaderAccessor = mockStatic(MessageHeaderAccessor.class);
  }

  @AfterEach
  void tearDownStaticMocks() {
    mockedMessageHeaderAccessor.closeOnDemand();
  }

  @Test
  public void preSend_Should_returnUntouchedMessage_When_accessorIsNull() {
    mockedMessageHeaderAccessor.when(() -> MessageHeaderAccessor.getAccessor(any(Message.class), eq(StompHeaderAccessor.class)))
        .thenReturn(null);

    var resultMessage = clientInboundChannelInterceptor
        .preSend(message, mock(MessageChannel.class));

    assertThat(resultMessage, is(message));
  }

  @Test
  public void preSend_Should_callRegistryWithExpectedCommand_When_accessorCommandIsConnect() {
    when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.CONNECT);

    clientInboundChannelInterceptor.preSend(message, mock(MessageChannel.class));

    verify(stompHandlerRegistry, times(1)).retrieveStompHandler(StompCommand.CONNECT);
  }

  @Test
  public void preSend_Should_callRegistryWithExpectedCommand_When_accessorCommandIsSubscribe() {
    when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.SUBSCRIBE);

    clientInboundChannelInterceptor.preSend(message, mock(MessageChannel.class));

    verify(stompHandlerRegistry, times(1)).retrieveStompHandler(StompCommand.SUBSCRIBE);
  }

  @Test
  public void preSend_Should_callRegistryWithExpectedCommand_When_accessorCommandIsDisconnect() {
    when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.DISCONNECT);

    clientInboundChannelInterceptor.preSend(message, mock(MessageChannel.class));

    verify(stompHandlerRegistry, times(1)).retrieveStompHandler(StompCommand.DISCONNECT);
  }

  @Test
  public void preSend_Should_callRegistryWithExpectedCommand_When_accessorCommandIsError() {
    when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.ERROR);

    clientInboundChannelInterceptor.preSend(message, mock(MessageChannel.class));

    verify(stompHandlerRegistry, times(1)).retrieveStompHandler(StompCommand.ERROR);
  }

  @Test
  public void preSend_Should_callRegistryWithExpectedCommand_When_accessorCommandIsAck() {
    when(stompHeaderAccessor.getCommand()).thenReturn(StompCommand.ACK);

    clientInboundChannelInterceptor.preSend(message, mock(MessageChannel.class));

    verify(stompHandlerRegistry, times(1)).retrieveStompHandler(StompCommand.ACK);
  }

}
