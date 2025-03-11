package com.jay.sapapi.dto;

import com.jay.sapapi.dto.strategy.PostImageThumbnailStrategy;
import com.jay.sapapi.dto.strategy.ProfileImageThumbnailStrategy;
import com.jay.sapapi.dto.strategy.ThumbnailStrategy;
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
