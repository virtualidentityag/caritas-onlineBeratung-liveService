package de.caritas.cob.liveservice.websocket.stomphandler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.caritas.cob.liveservice.websocket.registry.LiveEventMessageQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class StompAcknowledgeHandlerTest {

  private MockedStatic<MessageHeaderAccessor> mockedMessageHeaderAccessor;

  @InjectMocks
  private StompAcknowledgeHandler stompAcknowledgeHandler;

  @Mock
  private LiveEventMessageQueue liveEventMessageQueue;

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
  public void supportedStompCommand_Should_returnAck() {
    var command = this.stompAcknowledgeHandler.supportedStompCommand();

    assertThat(command, is(StompCommand.ACK));
  }

  @Test
  public void handle_Should_useNoServices_When_messageIsNull() {
    this.stompAcknowledgeHandler.handle(null);

    verifyNoInteractions(this.liveEventMessageQueue);
  }

  @Test
  public void handle_Should_removeQueuedMessageWithId_When_messageHasMessageId() {
    when(this.stompHeaderAccessor.getFirstNativeHeader(anyString())).thenReturn("id");

    this.stompAcknowledgeHandler.handle(this.message);

    verify(this.liveEventMessageQueue, times(1)).removeIdentifiedMessageWithId("id");
  }

}
