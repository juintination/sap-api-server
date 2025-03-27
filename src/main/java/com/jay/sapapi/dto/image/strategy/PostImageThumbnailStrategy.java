package com.jay.sapapi.dto.image.strategy;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;
import java.nio.file.Path;

public class PostImageThumbnailStrategy implements ThumbnailStrategy {

    @Override
    public void createThumbnail(Path sourcePath, Path thumbnailPath) throws IOException {
        Thumbnails.of(sourcePath.toFile())
                .sourceRegion(Positions.CENTER, 800, 800)
                .size(800, 800)
                .toFile(thumbnailPath.toFile());
    }

}
