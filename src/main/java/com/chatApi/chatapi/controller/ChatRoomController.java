package com.chatApi.chatapi.controller;

import com.chatApi.chatapi.dto.AddMemberDto;
import com.chatApi.chatapi.dto.ChatRoomDto;
import com.chatApi.chatapi.dto.CreateRoomDto;
import com.chatApi.chatapi.entity.ChatRoom;
import com.chatApi.chatapi.entity.User;
import com.chatApi.chatapi.service.ChatRoomService;
import com.chatApi.chatapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    public ChatRoomController(ChatRoomService chatRoomService, UserRepository userRepository) {
        this.chatRoomService = chatRoomService;
        this.userRepository = userRepository;
    }

    // Create a room
    // POST /api/rooms
    @PostMapping
    public ResponseEntity<ChatRoomDto> createRoom(@RequestBody CreateRoomDto dto, Authentication authentication) {
        User current = (User) authentication.getPrincipal();
        List<Long> members = null;
        ChatRoom room = chatRoomService.createRoom(dto.name, dto.type == null ? "group" : dto.type, current.getId(), members);
        ChatRoomDto out = new ChatRoomDto();
        out.id = room.getId();
        out.name = room.getName();
        out.type = room.getType();
        out.memberIds = room.getMembers().stream().map(m -> m.getUser().getId()).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // List rooms for authenticated user
    // GET /api/rooms
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> listRooms(Authentication authentication) {
        User current = (User) authentication.getPrincipal();
        return ResponseEntity.ok(chatRoomService.listRoomsForUser(current.getId()));
    }

    // Add member to room
    // POST /api/rooms/{roomId}/members
    @PostMapping("/{roomId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long roomId, @RequestBody AddMemberDto dto) {
        chatRoomService.addMember(roomId, dto.userId);
        return ResponseEntity.ok().build();
    }

    // Remove member
    // DELETE /api/rooms/{roomId}/members/{userId}
    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long roomId, @PathVariable Long userId) {
        chatRoomService.removeMember(roomId, userId);
        return ResponseEntity.ok().build();
    }
}
