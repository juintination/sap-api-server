package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.dto.MemberDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {

    @Transactional(readOnly = true)
    MemberDTO get(Long userId);

    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    @Transactional(readOnly = true)
    boolean existsByNickname(String nickname);

    Long register(MemberDTO memberDTO);

    void modify(MemberDTO modifyDTO);

    void remove(Long userId);

    @Transactional(readOnly = true)
    void checkPassword(Long userId, String password);

    Member dtoToEntity(MemberDTO memberDTO);

    default MemberDTO entityToDTO(Member member) {
        return MemberDTO.builder()
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
