package com.jay.sapapi.dto.image;

import com.jay.sapapi.dto.image.strategy.PostImageThumbnailStrategy;
import com.jay.sapapi.dto.image.strategy.ProfileImageThumbnailStrategy;
import com.jay.sapapi.dto.image.strategy.ThumbnailStrategy;
import lombok.Getter;

@Getter
public enum ImageType {

    POST_IMAGE(new PostImageThumbnailStrategy()),
    PROFILE_IMAGE(new ProfileImageThumbnailStrategy());

    private final ThumbnailStrategy thumbnailStrategy;

    ImageType(ThumbnailStrategy thumbnailStrategy) {
        this.thumbnailStrategy = thumbnailStrategy;
    }

}
