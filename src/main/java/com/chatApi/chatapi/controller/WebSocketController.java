package com.chatApi.chatapi.controller;

import com.chatApi.chatapi.websocket.ChatMessage;
import com.chatApi.chatapi.service.MessageService;
import com.chatApi.chatapi.entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Handles incoming STOMP messages at /app/chat.sendMessage
 * and broadcasts to /topic/rooms.{roomId}
 */
@Controller
public class WebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage incoming) {
        // Persist message (MessageService returns the saved JPA entity)
        Message saved = messageService.sendMessage(incoming.getRoomId(), incoming.getSenderId(), incoming.getContent());

        // build outgoing ChatMessage (you already have ChatMessage class in websocket package - ensure getters/setters)
        ChatMessage outgoing = new ChatMessage();
        outgoing.setId(saved.getId());
        outgoing.setRoomId(saved.getRoom().getId());
        outgoing.setSenderId(saved.getSender().getId());
        outgoing.setContent(saved.getContent());
        outgoing.setTimestamp(saved.getTimestamp());

        // Broadcast to subscribers of the room topic
        simpMessagingTemplate.convertAndSend("/topic/rooms." + saved.getRoom().getId(), outgoing);
    }
}
