package com.timur.deepface.dto;

import lombok.Data;

@Data
public class SingInProviderRequest {
    private String idToken;
    private String name;
    private String email;
    private String provider;
}
