package com.jay.sapapi.service;

import com.jay.sapapi.dto.ImageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Transactional
public interface ImageService {

    Map<String, String> viewImage(String fileName) throws IOException;

    Map<String, String> viewImageThumbnail(String fileName) throws IOException;

    String registerImage(ImageDTO imageDTO);

    void removeImage(String fileName);

}
