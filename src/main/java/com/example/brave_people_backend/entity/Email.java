package com.example.brave_people_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long emailId;

    @Column(length = 30, nullable = false)
    String emailAddress;

    int authCode;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    boolean authStatus;
}
