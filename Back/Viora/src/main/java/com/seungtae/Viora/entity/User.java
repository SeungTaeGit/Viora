package com.seungtae.Viora.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String nickname;
}
