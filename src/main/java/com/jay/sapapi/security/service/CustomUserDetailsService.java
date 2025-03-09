package com.jay.sapapi.security.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.repository.MemberRepository;
import com.jay.sapapi.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);

        if (member == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        log.info("loadUserByUsername: " + member);
        return new CustomUserDetails(member);

    }

}
