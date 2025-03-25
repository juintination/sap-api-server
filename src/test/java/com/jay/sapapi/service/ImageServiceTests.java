package com.jay.sapapi.service;

import com.jay.sapapi.dto.ImageDTO;
import com.jay.sapapi.dto.ImageType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ImageServiceTests")
public class ImageServiceTests {

    @Autowired
    private ImageService imageService;

    private String fileName;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(imageService, "ImageService should not be null");
        log.info(imageService.getClass().getName());
    }

    @Test
    @BeforeEach
    @DisplayName("이미지 추가")
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
        fileName = imageService.registerImage(imageDTO);

        Assertions.assertNotNull(fileName, "Registered file name should not be null");
        Assertions.assertFalse(fileName.isEmpty(), "Registered file name should not be empty");

    }

    @AfterAll
    public void cleanup() throws IOException {
        Path directory = Paths.get("upload/test");
        if (Files.exists(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    Files.delete(entry);
                }
            }
            Files.delete(directory);
        }
    }

    @Test
    @DisplayName("조회 테스트")
    public void testGet() throws IOException {
        Map<String, String> result = imageService.viewImage(fileName);
        Assertions.assertNotNull(result, "Result map from viewImage should not be null");
        Assertions.assertTrue(result.containsKey("fileContent"), "Result should contain the fileContent");
    }

    @Test
    @DisplayName("삭제 테스트")
    public void testRemove() {
        imageService.removeImage(fileName);
        Assertions.assertThrows(IOException.class, () -> imageService.viewImage(fileName));
    }

}
