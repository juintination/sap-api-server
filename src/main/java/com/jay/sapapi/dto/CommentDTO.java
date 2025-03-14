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
public class CommentDTO {

    private Long id, commenterId, postId;

    private String content;

    private String commenterNickname, commenterEmail;

    private LocalDateTime regDate, modDate;

}
