package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.MemberDTO;
import com.jay.sapapi.repository.MemberRepository;
import com.jay.sapapi.util.exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO get(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("userNotFound"));
        return entityToDTO(member);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public Long register(MemberDTO memberDTO) {

        if (memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new CustomValidationException("emailAlreadyExists");
        }

        if (memberRepository.existsByNickname(memberDTO.getNickname())) {
            throw new CustomValidationException("nicknameAlreadyExists");
        }

        Member member = memberRepository.save(dtoToEntity(memberDTO));
        return member.getId();
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("userNotFound"));

        if (memberDTO.getEmail() != null && !memberDTO.getEmail().isEmpty()) {
            if (!member.getEmail().equals(memberDTO.getEmail()) &&
                    memberRepository.existsByEmail(memberDTO.getEmail())) {
                throw new CustomValidationException("emailAlreadyExists");
            }
            try {
                member.changeEmail(memberDTO.getEmail());
            } catch (IllegalArgumentException e) {
                throw new CustomValidationException("invalidEmail");
            }
        }

        if (memberDTO.getNickname() != null && !memberDTO.getNickname().isEmpty()) {
            if (!member.getNickname().equals(memberDTO.getNickname()) &&
                    memberRepository.existsByNickname(memberDTO.getNickname())) {
                throw new CustomValidationException("nicknameAlreadyExists");
            }
            member.changeNickname(memberDTO.getNickname());
        }

        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            member.changePassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        if (memberDTO.getProfileImageUrl() != null && !memberDTO.getProfileImageUrl().isEmpty()) {
            member.changeProfileImageUrl(memberDTO.getProfileImageUrl());
        }

        memberRepository.save(member);
    }

    @Override
    public void remove(Long userId) {
        if (!memberRepository.existsById(userId)) {
            throw new NoSuchElementException("userNotFound");
        }
        memberRepository.deleteById(userId);
    }

    @Override
    public void checkPassword(Long userId, String password) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("userNotFound"));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomValidationException("invalidPassword");
        }
    }

    @Override
    public Member dtoToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .id(memberDTO.getId())
                .email(memberDTO.getEmail())
                .password(memberDTO.getPassword() != null ? passwordEncoder.encode(memberDTO.getPassword()) : null)
                .nickname(memberDTO.getNickname())
                .profileImageUrl(memberDTO.getProfileImageUrl())
                .memberRole(memberDTO.getRole() != null ? memberDTO.getRole() : MemberRole.USER)
                .build();
    }

}
