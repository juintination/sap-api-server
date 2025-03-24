package com.jay.sapapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Long id, userId;

    private String title, content;

    private Long viewCount;

    private int commentCount, likeCount;

    private String postImageUrl;

    private String writerNickname, writerEmail;

    private String writerProfileImageUrl;

    private LocalDateTime createdAt, modifiedAt;
}
