package com.jay.sapapi.dto.post.response;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostResponseDTO {

    private Long id, userId;

    private String title, content;

    private Long viewCount;

    private Long commentCount, likeCount;

    private String postImageUrl;

    private String writerNickname, writerEmail;

    private String writerProfileImageUrl;

    private LocalDateTime createdAt, modifiedAt;

}
