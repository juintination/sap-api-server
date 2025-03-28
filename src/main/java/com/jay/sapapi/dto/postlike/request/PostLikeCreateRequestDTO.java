package com.jay.sapapi.dto.postlike.request;

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
public class PostLikeCreateRequestDTO {

    @NotNull(message = "게시글 ID는 필수입니다.")
    @Positive(message = "유효한 게시글 ID를 입력해주세요.")
    private Long postId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Positive(message = "유효한 사용자 ID를 입력해주세요.")
    private Long userId;

}
