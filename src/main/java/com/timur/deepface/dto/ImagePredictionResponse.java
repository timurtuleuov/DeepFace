package com.timur.deepface.dto;

import lombok.Data;

@Data
public class ImagePredictionResponse {
    private byte[] image;
    private float[] prediction;
}
