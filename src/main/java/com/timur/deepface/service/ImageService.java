package com.timur.deepface.service;

import com.timur.deepface.entity.ContentType;
import com.timur.deepface.entity.Image;
import com.timur.deepface.entity.User;
import com.timur.deepface.repository.ImageRepository;
import com.timur.deepface.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }
    @Transactional
    public Image uploadImage(MultipartFile file, Long userId, ContentType contentType) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Сохраняем изображение в базе данных
        Image image = new Image();
        image.setUser(user);
        image.setContentType(contentType);
        image.setData(file.getBytes());

        return imageRepository.save(image);
    }

    public Optional<Image> getImageById(Long imageId) {
        return imageRepository.findById(imageId);
    }

    public List<Image> getAllImagesByUserId(Long userId) {
        return imageRepository.findAllByUserId(userId);
    }

}
