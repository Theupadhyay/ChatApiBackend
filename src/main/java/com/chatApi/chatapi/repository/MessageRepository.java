package com.chatApi.chatapi.repository;

import com.chatApi.chatapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomIdOrderByTimestampAsc(Long roomId);
}
