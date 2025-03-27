package com.jay.sapapi.dto.member.request;

import com.jay.sapapi.domain.MemberRole;
import jakarta.validation.constraints.Email;
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
public class MemberModifyRequestDTO {

    @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    @Pattern(regexp = "^\\S+$", message = "닉네임은 공백을 포함할 수 없습니다.")
    private String nickname;

    private String profileImageUrl;

    private MemberRole role;

}
