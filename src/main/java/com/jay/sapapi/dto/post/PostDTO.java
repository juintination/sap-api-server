package com.jay.sapapi.dto.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
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
