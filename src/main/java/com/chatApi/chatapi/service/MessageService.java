package com.chatApi.chatapi.service;

import com.chatApi.chatapi.entity.ChatRoom;
import com.chatApi.chatapi.entity.Message;
import com.chatApi.chatapi.entity.User;
import com.chatApi.chatapi.repository.ChatRoomRepository;
import com.chatApi.chatapi.repository.MessageRepository;
import com.chatApi.chatapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          ChatRoomRepository chatRoomRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Message message = new Message(room, sender, content, Instant.now());
        return messageRepository.save(message);
    }

    public List<Message> getHistory(Long roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
