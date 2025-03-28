package com.jay.sapapi.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class PostModifyRequestDTO {

    @NotNull(message = "작성자 ID는 필수입니다.")
    @Positive(message = "유효한 작성자 ID를 입력해주세요.")
    private Long userId;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 26, message = "제목은 최대 26자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    private String postImageUrl;

}
