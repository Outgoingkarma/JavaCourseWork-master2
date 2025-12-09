package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "chat")
    private FoodOrder order;
    @ManyToOne
    private BasicUser customer;
    @ManyToOne
    private Driver driver;
    @ManyToOne
    private Restaurant restaurant;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime lastMessageDate = LocalDateTime.now();

    public void addMessage(ChatMessage message) {
        if (message == null) return;
        if (messages == null) {
            messages = new ArrayList<>();
        }
        message.setChat(this);
        messages.add(message);
        this.lastMessageDate = message.getDateCreated() == null ? LocalDateTime.now() : message.getDateCreated();
    }

    public List<ChatMessage> getUnreadMessages(User user) {
        if (user == null || messages == null) {
            return List.of();
        }
        return messages.stream()
                .filter(msg -> !msg.isRead() && msg.getMessageSender() != null && msg.getMessageSender().getId() != user.getId())
                .toList();
    }

    public void markAllAsRead(User user) {
        if (user == null || messages == null) return;
        messages.stream()
                .filter(msg -> msg.getMessageSender() != null && msg.getMessageSender().getId() != user.getId())
                .forEach(ChatMessage::markAsRead);
    }

    public int getUnreadCount(User user) {
        if (user == null || messages == null) return 0;
        return (int) messages.stream()
                .filter(msg -> !msg.isRead() && msg.getMessageSender() != null && msg.getMessageSender().getId() != user.getId())
                .count();
    }

}
