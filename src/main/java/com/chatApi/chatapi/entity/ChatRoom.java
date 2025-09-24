package com.chatApi.chatapi.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // private / group

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatRoomMember> members = new HashSet<>();

    // constructors, getters, setters
    public ChatRoom() {}
    public ChatRoom(String name, String type) { this.name = name; this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() {return type;}
    public void setType(String type){this.type = type;}

    public Set<ChatRoomMember> getMembers() { return members; }
    public void setMembers(Set<ChatRoomMember> members) { this.members = members; }
}
