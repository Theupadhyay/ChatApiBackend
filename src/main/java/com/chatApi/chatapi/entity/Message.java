package com.chatApi.chatapi.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="room_id")
    private ChatRoom room;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant timestamp;

    private String status; // e.g., SENT, DELIVERED, READ

    public Message() {}

    public Message(ChatRoom room, User sender, String content, Instant timestamp) {
        this.room = room; this.sender = sender; this.content = content; this.timestamp = timestamp;
        this.status = "SENT";
    }

    // getters/setters
    public Long getId() { return id; }
    public ChatRoom getRoom() { return room; }
    public void setRoom(ChatRoom room) { this.room = room; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
