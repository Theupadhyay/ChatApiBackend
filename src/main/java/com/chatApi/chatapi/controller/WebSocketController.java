package com.chatApi.chatapi.controller;

import com.chatApi.chatapi.entity.Message;
import com.chatApi.chatapi.service.MessageService;
import com.chatApi.chatapi.websocket.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // Client sends to /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage incoming) {
        // Persist message
        Message saved = messageService.sendMessage(incoming.getRoomId(), incoming.getSenderId(), incoming.getContent());
        // Broadcast to subscribers of /topic/rooms.{roomId}
        simpMessagingTemplate.convertAndSend("/topic/rooms." + incoming.getRoomId(),
                new ChatMessage(saved.getId(), saved.getRoom().getId(), saved.getSender().getId(), saved.getContent(), saved.getTimestamp()));
    }
}
