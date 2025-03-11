package com.jay.sapapi.service;

import com.jay.sapapi.dto.ImageDTO;
import com.jay.sapapi.dto.ImageType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageServiceTests {

    @Autowired
    private ImageService imageService;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(imageService, "ImageService should not be null");
        log.info(imageService.getClass().getName());
    }

    @Test
    public void testRegister() throws IOException {

        Path path = Paths.get("upload/default.png");
        byte[] fileContent = Files.readAllBytes(path);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "default.png",
                "image/png",
                fileContent
        );

        ImageDTO imageDTO = ImageDTO.builder()
                .fileName(mockFile.getOriginalFilename())
                .file(mockFile)
                .imageType(ImageType.PROFILE_IMAGE)
                .build();
        String fileName = imageService.registerImage(imageDTO);
        log.info(fileName);

    }

    @Test
    public void testGet() throws IOException {
        Map<String, String> result = imageService.viewImage("../default.png");
        Assertions.assertNotNull(result);
        log.info(result);
    }

}
