package com.timur.deepface.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "prediction")
    private float[] prediction;

}
