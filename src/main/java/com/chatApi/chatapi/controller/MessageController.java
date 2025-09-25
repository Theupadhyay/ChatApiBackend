package com.chatApi.chatapi.controller;

import com.chatApi.chatapi.dto.MessageDto;
import com.chatApi.chatapi.dto.SendMessageRequest;
import com.chatApi.chatapi.entity.Message;
import com.chatApi.chatapi.entity.User;
import com.chatApi.chatapi.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    public MessageController(MessageService messageService) { this.messageService = messageService; }

    // POST /api/messages/send
    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody SendMessageRequest request,
                                                  Authentication authentication) {
        // Get current user from JWT (adjust if your principal isn’t User directly)
        User current = (User) authentication.getPrincipal();

        Message msg = messageService.sendMessage(request.roomId, current.getId(), request.content);

        MessageDto dto = new MessageDto();
        dto.id = msg.getId();
        dto.roomId = msg.getRoom().getId();
        dto.senderId = msg.getSender().getId();
        dto.content = msg.getContent();
        dto.timestamp = msg.getTimestamp();

        return ResponseEntity.ok(dto);
    }


    // GET /api/messages/history/{roomId}
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<MessageDto>> getHistory(@PathVariable Long roomId) {
        List<Message> history = messageService.getHistory(roomId);
        List<MessageDto> result = history.stream().map(m -> {
            MessageDto dto = new MessageDto();
            dto.id = m.getId();
            dto.roomId = m.getRoom().getId();
            dto.senderId = m.getSender().getId();
            dto.content = m.getContent();
            dto.timestamp = m.getTimestamp();
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
