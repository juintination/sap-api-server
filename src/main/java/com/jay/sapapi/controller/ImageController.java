package com.jay.sapapi.controller;

import com.jay.sapapi.dto.ImageDTO;
import com.jay.sapapi.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{fileName}")
    public Map<String, String> viewFileGET(@PathVariable("fileName") String fileName) throws IOException {
        return imageService.viewImage(fileName);
    }

    @GetMapping("/thumbnail/{fileName}")
    public Map<String, String> viewThumbnailGET(@PathVariable("fileName") String fileName) throws IOException {
        return imageService.viewImageThumbnail(fileName);
    }

    @PostMapping("/")
    public ResponseEntity<?> register(ImageDTO imageDTO) {
        String fileName = imageService.registerImage(imageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "registerSuccess", "data", Map.of("fileName", fileName)));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> remove(@PathVariable("fileName") String fileName) {
        imageService.removeImage(fileName);
        return ResponseEntity.ok(Map.of("message", "imageDeleted"));
    }

}
