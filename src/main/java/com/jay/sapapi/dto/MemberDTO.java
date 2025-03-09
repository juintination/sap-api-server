package com.jay.sapapi.dto;

import com.jay.sapapi.domain.MemberRole;
import lombok.Data;
import lombok.ToString;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long userId;

    private String email;

    private String password;

    private String nickname;

    private MemberRole role;

    private LocalDateTime regDate, modDate;

}
