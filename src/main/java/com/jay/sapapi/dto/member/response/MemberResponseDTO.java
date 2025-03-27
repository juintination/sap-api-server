package com.jay.sapapi.dto.member.response;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import com.jay.sapapi.domain.MemberRole;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponseDTO {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;

    private MemberRole role;

    private LocalDateTime createdAt, modifiedAt;

}
