package com.jay.sapapi.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.jay.sapapi.domain.MemberRole;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;

    private MemberRole role;

    private LocalDateTime createdAt, modifiedAt;

}
