package com.timur.deepface.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос серверу с нейронной сетью")
public class ImageRequest {
    private byte[] data;
    private String contentType;
    private Long userId;
}
