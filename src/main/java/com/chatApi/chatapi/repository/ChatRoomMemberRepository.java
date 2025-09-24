package com.chatApi.chatapi.repository;

import com.chatApi.chatapi.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByUserId(Long userId);
    List<ChatRoomMember> findByRoomId(Long roomId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
