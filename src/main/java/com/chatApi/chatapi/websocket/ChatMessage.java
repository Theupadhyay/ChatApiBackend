package com.chatApi.chatapi.websocket;

import java.time.Instant;

public class ChatMessage {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String content;
    private Instant timestamp;

    public ChatMessage() {}

    public ChatMessage(Long id, Long roomId, Long senderId, String content, Instant timestamp) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
