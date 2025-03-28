package com.jay.sapapi.dto.postlike.response;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostLikeResponseDTO {

    private Long id, postId, userId;

    private LocalDateTime createdAt;

}
