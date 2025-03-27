package com.jay.sapapi.dto.comment;

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
public class CommentDTO {

    private Long id, userId, postId;

    private String content;

    private String commenterNickname, commenterEmail;

    private String commenterProfileImageUrl;

    private LocalDateTime createdAt, modifiedAt;

}
