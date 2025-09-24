package com.chatApi.chatapi.dto;

import java.time.Instant;

public class MessageDto {
    public Long id;
    public Long roomId;
    public Long senderId;
    public String content;
    public Instant timestamp;
}
