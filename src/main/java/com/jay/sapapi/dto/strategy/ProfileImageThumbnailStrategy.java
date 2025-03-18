package com.jay.sapapi.dto.strategy;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class ProfileImageThumbnailStrategy implements ThumbnailStrategy {

    @Override
    public void createThumbnail(Path sourcePath, Path thumbnailPath) throws IOException {
        BufferedImage originalImage = ImageIO.read(sourcePath.toFile());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int resizedWidth, resizedHeight;
        if (width < height) {
            resizedWidth = 200;
            resizedHeight = (int) ((double) height / width * 200);
        } else {
            resizedWidth = (int) ((double) width / height * 200);
            resizedHeight = 200;
        }

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(resizedWidth, resizedHeight)
                .asBufferedImage();

        Thumbnails.of(resizedImage)
                .sourceRegion(Positions.CENTER, 200, 200)
                .size(200, 200)
                .toFile(thumbnailPath.toFile());
    }

}
