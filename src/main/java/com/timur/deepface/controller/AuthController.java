package com.timur.deepface.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.timur.deepface.dto.JwtAuthenticationResponse;
import com.timur.deepface.dto.SignInRequest;
import com.timur.deepface.dto.SignUpRequest;
import com.timur.deepface.dto.SingInProviderRequest;
import com.timur.deepface.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

//    @PostMapping("/sign-in/provider")
//    public JwtAuthenticationResponse signInProvider(@RequestBody SingInProviderRequest request) throws JsonProcessingException, ParseException, JOSEException {
//        RestTemplate restTemplate = new RestTemplate();
//        String jwksUrl = "https://www.googleapis.com/oauth2/v3/certs";
//        String jwksJson = restTemplate.getForObject(jwksUrl, String.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jwksNode = objectMapper.readTree(jwksJson);
//        RSAKey rsaKey = RSAKey.parse(jwksNode);
//        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
//
//        // Создание JWT парсера
//        SignedJWT signedJWT = SignedJWT.parse(request.getIdToken());
//
//        // Создание JWT верификатора с публичным ключом
//        JWSVerifier verifier = new RSASSAVerifier(publicKey);
//
//        // Проверка подписи токена
//        boolean isSignatureValid = signedJWT.verify(verifier);
//        if (isSignatureValid) {
//            SignUpRequest signUp = new SignUpRequest();
//            signUp.setEmail(request.getEmail());
//            signUp.setUsername(request.getName());
//            signUp.setPassword("123");
//            return authenticationService.signUp(signUp);
//        }
//
//        return null;
//    }

}
