package de.caritas.cob.liveservice.api.controller;

import static de.caritas.cob.liveservice.StompClientIntegrationTest.buildLiveEventMessage;
import static de.caritas.cob.liveservice.api.model.EventType.DIRECTMESSAGE;
import static de.caritas.cob.liveservice.api.model.EventType.VIDEOCALLDENY;
import static de.caritas.cob.liveservice.api.model.EventType.VIDEOCALLREQUEST;
import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.liveservice.LiveServiceApplication;
import de.caritas.cob.liveservice.api.model.VideoCallRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = LiveServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class LiveControllerIT {

  public static final String LIVEEVENT_SEND = "/liveevent/send";
  public static final String USER_IDS_PARAM = "userIds";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void sendLiveEvent_Should_returnStatusOk_When_calledWithValidDirectMessageParams()
      throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .contentType(APPLICATION_JSON)
        .content(buildLiveEventMessage(DIRECTMESSAGE, asList("1", "2"), null))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void sendLiveEvent_Should_returnStatusOk_When_calledWithValidVideoCallMessageParams()
      throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .contentType(APPLICATION_JSON)
        .content(
            buildLiveEventMessage(VIDEOCALLREQUEST, asList("1", "2"), new VideoCallRequestDTO()))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void sendLiveEvent_Should_returnStatusOk_When_calledWithValidVideoDenyMessageParams()
      throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .contentType(APPLICATION_JSON)
        .content(buildLiveEventMessage(VIDEOCALLDENY, asList("1", "2"), null))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  public void sendLiveEvent_Should_returnBadRequest_When_userIdsAreMissing() throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .content(buildLiveEventMessage(DIRECTMESSAGE, null, null))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void sendLiveEvent_Should_returnBadRequest_When_eventTypeIsMissing() throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .param(USER_IDS_PARAM, "1", "2").contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void sendLiveEvent_Should_returnBadRequest_When_calledWithInvalidEventType()
      throws Exception {
    mockMvc.perform(post(LIVEEVENT_SEND)
        .param(USER_IDS_PARAM, "1", "2").contentType(APPLICATION_JSON)
        .content("InvalidEventType").contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

}
