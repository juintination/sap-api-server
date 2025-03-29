package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.member.request.MemberModifyRequestDTO;
import com.jay.sapapi.dto.member.request.MemberSignupRequestDTO;
import com.jay.sapapi.dto.member.response.MemberResponseDTO;
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

    private final PostLikeService postLikeService;

    @Override
    public MemberResponseDTO get(Long userId) {
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
    public Long register(MemberSignupRequestDTO memberSignupRequestDTO) {

        if (memberRepository.existsByEmail(memberSignupRequestDTO.getEmail())) {
            throw new CustomValidationException("emailAlreadyExists");
        }

        if (memberRepository.existsByNickname(memberSignupRequestDTO.getNickname())) {
            throw new CustomValidationException("nicknameAlreadyExists");
        }

        Member member = memberRepository.save(dtoToEntity(memberSignupRequestDTO));
        return member.getId();
    }

    @Override
    public void modify(Long userId, MemberModifyRequestDTO memberModifyRequestDTO) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("userNotFound"));

        if (!member.getEmail().equals(memberModifyRequestDTO.getEmail()) &&
                memberRepository.existsByEmail(memberModifyRequestDTO.getEmail())) {
            throw new CustomValidationException("emailAlreadyExists");
        }
        member.changeEmail(memberModifyRequestDTO.getEmail());

        if (!member.getNickname().equals(memberModifyRequestDTO.getNickname()) &&
                memberRepository.existsByNickname(memberModifyRequestDTO.getNickname())) {
            throw new CustomValidationException("nicknameAlreadyExists");
        }
        member.changeNickname(memberModifyRequestDTO.getNickname());

        if (memberModifyRequestDTO.getProfileImageUrl() != null && !memberModifyRequestDTO.getProfileImageUrl().isEmpty()) {
            member.changeProfileImageUrl(memberModifyRequestDTO.getProfileImageUrl());
        }

        memberRepository.save(member);
    }

    @Override
    public void remove(Long userId) {
        if (!memberRepository.existsById(userId)) {
            throw new NoSuchElementException("userNotFound");
        }
        postLikeService.getHeartsByMember(userId).forEach(heart -> postLikeService.remove(heart.getPostId(), userId));
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
    public void changePassword(Long userId, String newPassword) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("userNotFound"));
        member.changePassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public Member dtoToEntity(MemberSignupRequestDTO dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .profileImageUrl(dto.getProfileImageUrl())
                .memberRole(dto.getRole() != null ? dto.getRole() : MemberRole.USER)
                .build();
    }

}
