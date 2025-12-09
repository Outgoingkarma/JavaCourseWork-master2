package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(unique = true)
    protected String login;
    protected String password;
    protected String name;
    protected String surname;
    protected String phone_number;
    protected String email;
    protected LocalDateTime dateCreated;
    protected LocalDateTime dateModified;
    protected boolean isAdmin;

    public User(String login, String password, String name, String surname, String phone_number, String email, boolean isAdmin) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone_number = phone_number;
        this.email = email;
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
        this.isAdmin = isAdmin;
    }

}
