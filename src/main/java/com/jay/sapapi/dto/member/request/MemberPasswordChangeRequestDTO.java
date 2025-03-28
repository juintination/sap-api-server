package com.jay.sapapi.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class MemberPasswordChangeRequestDTO {

    @NotBlank(message = "현재 비밀번호는 필수 입력 값입니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

}
