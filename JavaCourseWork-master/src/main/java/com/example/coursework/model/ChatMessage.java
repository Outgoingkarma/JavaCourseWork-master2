package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private String messageText;
    @ManyToOne
    private User messageSender;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private boolean isRead = false;
    @ManyToOne
    private Chat chat;

    public void markAsRead() {
        this.isRead = true;
    }


}
