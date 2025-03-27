package com.jay.sapapi.service;

import com.jay.sapapi.dto.image.ImageDTO;
import com.jay.sapapi.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final CustomFileUtil fileUtil;

    @Override
    public Map<String, String> viewImage(String fileName) throws IOException {
        Resource fileResource = fileUtil.getFile(fileName).getBody();
        assert fileResource != null;
        byte[] fileContent = fileUtil.getFileContent(fileResource);
        String base64FileContent = Base64.getEncoder().encodeToString(fileContent);
        return Map.of("fileContent", base64FileContent);
    }

    @Override
    public Map<String, String> viewImageThumbnail(String fileName) throws IOException {
        String thumbnailName = "s_" + fileName;
        Resource fileResource = fileUtil.getFile(thumbnailName).getBody();
        assert fileResource != null;
        byte[] fileContent = fileUtil.getFileContent(fileResource);
        String base64FileContent = Base64.getEncoder().encodeToString(fileContent);
        return Map.of("fileContent", base64FileContent);
    }

    @Override
    public String registerImage(ImageDTO imageDTO) {
        imageDTO.setFileName(saveFileAndGetFileName(imageDTO));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return imageDTO.getFileName();
    }

    @Override
    public void removeImage(String fileName) {
        fileUtil.deleteFile(fileName);
    }

    private String saveFileAndGetFileName(ImageDTO imageDTO) {
        MultipartFile file = imageDTO.getFile();
        return fileUtil.saveFile(file, imageDTO.getImageType());
    }

}
