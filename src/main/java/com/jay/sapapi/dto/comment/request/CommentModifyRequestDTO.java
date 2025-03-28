package com.jay.sapapi.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentModifyRequestDTO {

    @NotNull(message = "작성자 ID는 필수입니다.")
    @Positive(message = "유효한 작성자 ID를 입력해주세요.")
    private Long userId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

}
