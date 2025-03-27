package com.jay.sapapi.service;

import com.jay.sapapi.dto.image.ImageDTO;

import java.io.IOException;
import java.util.Map;

public interface ImageService {

    Map<String, String> viewImage(String fileName) throws IOException;

    Map<String, String> viewImageThumbnail(String fileName) throws IOException;

    String registerImage(ImageDTO imageDTO);

    void removeImage(String fileName);

}
