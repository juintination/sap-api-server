package com.jay.sapapi.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordVerifyRequestDTO {

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

}
