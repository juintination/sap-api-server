package com.jay.sapapi.util;

import com.jay.sapapi.dto.image.ImageType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${image.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);

        if (!tempFolder.exists()) tempFolder.mkdir();

        uploadPath = tempFolder.getAbsolutePath();
        log.info("UploadPath: " + uploadPath);
    }

    public String saveFile(MultipartFile file, ImageType imageType) {

        if (file == null) {
            throw new NullPointerException();
        }

        String savedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path savePath = Paths.get(uploadPath, savedName);
        try {
            Files.copy(file.getInputStream(), savePath);
            log.info("File saved at: " + savePath);
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image")) {
                Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                imageType.getThumbnailStrategy().createThumbnail(savePath, thumbnailPath);
                log.info("Thumbnail created at: " + thumbnailPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return savedName;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.exists()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.png");
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFile(String fileName) {

        if (fileName == null) {
            throw new NullPointerException();
        }

        Path filePath = Paths.get(uploadPath, fileName);
        try {
            Files.deleteIfExists(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType != null && contentType.startsWith("image")) {
                String thumbnailFileName = "s_" + fileName;
                Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
                Files.deleteIfExists(thumbnailPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] getFileContent(Resource resource) throws IOException {
        Path path = resource.getFile().toPath();
        return Files.readAllBytes(path);
    }

}
