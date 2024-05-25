package com.timur.deepface.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timur.deepface.dto.ImagePredictionResponse;
import com.timur.deepface.dto.ImageRequest;
import com.timur.deepface.entity.ContentType;
import com.timur.deepface.entity.Response;
import com.timur.deepface.entity.User;
import com.timur.deepface.repository.ImageRepository;
import com.timur.deepface.repository.ResponseRepository;
import com.timur.deepface.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class ModelController {
    private final ImageRepository imageRepository;
    private final ResponseRepository responseRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);


    @Autowired
    private RestTemplate restTemplate;
    @PostMapping("/upload/bytes")
    public ResponseEntity<ImagePredictionResponse> uploadImageAndSendToNeuralNetworkByBytes(@RequestParam("image") byte[] imageData, @RequestParam("userId") Long userId) {
        try {
            // Создаем объект ImageRequest для отправки на сервер с нейронной сетью
            ImageRequest imageRequest = new ImageRequest();
            imageRequest.setUserId(userId);
            imageRequest.setData(Base64.getEncoder().encodeToString(imageData).getBytes());

            // Отправляем запрос на сервер с нейронной сетью
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("image_file", new ByteArrayResource(imageData));

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
            logger.error("An error occurred during uploadImageAndSendToNeuralNetwork method:", e);

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ImagePredictionResponse> uploadImageAndSendToNeuralNetwork(@RequestParam() MultipartFile file, @RequestParam("userName") String userName) {
        try {
            // Сохраняем изображение в базу данных или хранилище
            byte[] imageData = file.getBytes();
            Optional<User> user = userRepository.findByUsername(userName);
            User myUser = user.get();
            // Создаем объект ImageRequest для отправки на сервер с нейронной сетью
            ImageRequest imageRequest = new ImageRequest();
            imageRequest.setUserId(myUser.getId());
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
            response.setUserId(myUser.getId());
            response.setContentType(ContentType.IMAGE_JPEG); // Предположим, что тип контента - JPEG
            response.setData(imageData);
            response.setPrediction(prediction);
            response.setResponseDate(LocalDate.now());

            // Сохраняем запись в таблице Response
            responseRepository.save(response);

            ImagePredictionResponse imagePredictionResponse = new ImagePredictionResponse();
            imagePredictionResponse.setImage(imageData);
            imagePredictionResponse.setPrediction(prediction);

            return ResponseEntity.ok().body(imagePredictionResponse);
        } catch (IOException e) {
            logger.error("An error occurred during uploadImageAndSendToNeuralNetwork method:", e);

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
        }

    }

    @GetMapping("/history")
    public List<Response> getHistoryByUserId(@RequestParam String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        User myUser = user.get();
        return responseRepository.findByUserId(myUser.getId());
    }
}
