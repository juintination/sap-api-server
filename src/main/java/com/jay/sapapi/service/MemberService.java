package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.dto.MemberDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {

    MemberDTO get(Long userId);

    Long register(MemberDTO memberDTO);

    void modify(MemberDTO modifyDTO);

    void remove(Long userId);

    void checkPassword(Long userId, String password);

    Member dtoToEntity(MemberDTO memberDTO);

    default MemberDTO entityToDTO(Member member) {
        return MemberDTO.builder()
                .userId(member.getUserId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getMemberRole())
                .regDate(member.getRegDate())
                .modDate(member.getModDate())
                .build();
    }

}
