package com.jay.sapapi.dto.image.strategy;

import java.io.IOException;
import java.nio.file.Path;

public interface ThumbnailStrategy {

    void createThumbnail(Path sourcePath, Path thumbnailPath) throws IOException;

}
