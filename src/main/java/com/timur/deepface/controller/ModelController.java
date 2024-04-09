package com.timur.deepface.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timur.deepface.dto.ImagePredictionResponse;
import com.timur.deepface.dto.ImageRequest;
import com.timur.deepface.entity.ContentType;
import com.timur.deepface.entity.Response;
import com.timur.deepface.repository.ImageRepository;
import com.timur.deepface.repository.ResponseRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class ModelController {
    private final ImageRepository imageRepository;
    private final ResponseRepository responseRepository;
    @Autowired
    private RestTemplate restTemplate;
    @PostMapping("/upload")
    public ResponseEntity<ImagePredictionResponse> uploadImageAndSendToNeuralNetwork(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        try {
            // Сохраняем изображение в базу данных или хранилище
            byte[] imageData = file.getBytes();

            // Создаем объект ImageRequest для отправки на сервер с нейронной сетью
            ImageRequest imageRequest = new ImageRequest();
            imageRequest.setUserId(userId);
            imageRequest.setData(Base64.getEncoder().encodeToString(imageData).getBytes());

            // Отправляем запрос на сервер с нейронной сетью
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("image_file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> predictionResponse = restTemplate.exchange("http://localhost:8000/predict/", HttpMethod.POST, requestEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(predictionResponse.getBody());

            // Извлечение массива предсказаний
            JsonNode predictionNode = responseJson.get("Prediction");
            float[] prediction = objectMapper.convertValue(predictionNode.get(0), float[].class);

            // Создаем запись в таблице Response
            Response response = new Response();
            response.setUserId(userId);
            response.setContentType(ContentType.IMAGE_JPEG); // Предположим, что тип контента - JPEG
            response.setData(imageData);
            response.setPrediction(prediction);


            // Сохраняем запись в таблице Response
            responseRepository.save(response);

            ImagePredictionResponse imagePredictionResponse = new ImagePredictionResponse();
            imagePredictionResponse.setImage(imageData);
            imagePredictionResponse.setPrediction(prediction);

            return ResponseEntity.ok().body(imagePredictionResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
