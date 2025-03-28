package com.jay.sapapi.dto.comment.response;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDTO {

    private Long id, userId, postId;

    private String content;

    private String commenterNickname, commenterEmail;

    private String commenterProfileImageUrl;

    private LocalDateTime createdAt, modifiedAt;

}
