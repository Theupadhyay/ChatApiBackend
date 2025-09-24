package com.chatApi.chatapi.service;

import com.chatApi.chatapi.dto.ChatRoomDto;
import com.chatApi.chatapi.entity.ChatRoom;
import com.chatApi.chatapi.entity.ChatRoomMember;
import com.chatApi.chatapi.entity.User;
import com.chatApi.chatapi.repository.ChatRoomMemberRepository;
import com.chatApi.chatapi.repository.ChatRoomRepository;
import com.chatApi.chatapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatRoomMemberRepository chatRoomMemberRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatRoom createRoom(String name, String type, Long creatorId, List<Long> memberIds) {
        ChatRoom room = new ChatRoom(name, type);
        chatRoomRepository.save(room);

        // add creator
        User creator = userRepository.findById(creatorId).orElseThrow(() -> new RuntimeException("Creator not found"));
        ChatRoomMember creatorMember = new ChatRoomMember(room, creator);
        room.getMembers().add(creatorMember);
        chatRoomMemberRepository.save(creatorMember);

        if (memberIds != null) {
            for (Long uid : memberIds) {
                userRepository.findById(uid).ifPresent(u -> {
                    if (!chatRoomMemberRepository.existsByRoomIdAndUserId(room.getId(), u.getId())) {
                        ChatRoomMember m = new ChatRoomMember(room, u);
                        room.getMembers().add(m);
                        chatRoomMemberRepository.save(m);
                    }
                });
            }
        }
        return room;
    }

    public List<ChatRoomDto> listRoomsForUser(Long userId) {
        List<ChatRoomMember> memberships = chatRoomMemberRepository.findByUserId(userId);
        return memberships.stream().map(m -> {
            ChatRoom r = m.getRoom();
            ChatRoomDto dto = new ChatRoomDto();
            dto.id = r.getId();
            dto.name = r.getName();
            dto.type = r.getType();
            dto.memberIds = r.getMembers().stream().map(cm -> cm.getUser().getId()).collect(Collectors.toList());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addMember(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
            ChatRoomMember m = new ChatRoomMember(room, user);
            chatRoomMemberRepository.save(m);
            room.getMembers().add(m);
            chatRoomRepository.save(room);
        }
    }

    @Transactional
    public void removeMember(Long roomId, Long userId) {
        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoomId(roomId);
        for (ChatRoomMember m : members) {
            if (m.getUser().getId().equals(userId)) {
                chatRoomMemberRepository.delete(m);
                break;
            }
        }
    }

    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }
}
