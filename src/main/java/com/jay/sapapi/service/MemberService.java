package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.dto.member.request.MemberModifyRequestDTO;
import com.jay.sapapi.dto.member.request.MemberSignupRequestDTO;
import com.jay.sapapi.dto.member.response.MemberResponseDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {

    @Transactional(readOnly = true)
    MemberResponseDTO get(Long userId);

    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    boolean existsByNickname(String nickname);

    Long register(MemberSignupRequestDTO memberSignupRequestDTO);

    void modify(Long userId, MemberModifyRequestDTO memberModifyRequestDTO);

    void remove(Long userId);

    @Transactional(readOnly = true)
    void checkPassword(Long userId, String password);

    void changePassword(Long userId, String newPassword);

    Member dtoToEntity(MemberSignupRequestDTO dto);

    default MemberResponseDTO entityToDTO(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .role(member.getMemberRole())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }

}
