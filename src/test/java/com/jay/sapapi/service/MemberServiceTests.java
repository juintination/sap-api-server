package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.MemberDTO;
import com.jay.sapapi.util.exception.CustomValidationException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberServiceTests {

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private Long userId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberService, "MemberService should not be null");
        log.info(memberService.getClass().getName());
    }

    @Test
    @BeforeEach
    public void testRegister() {
        String email = "sample@example.com";

        MemberDTO memberDTO = MemberDTO.builder()
                .email(email)
                .password(faker.internet().password())
                .nickname("SampleUser")
                .role(MemberRole.USER)
                .build();

        try {
            userId = memberService.register(memberDTO);
        } catch (CustomValidationException e) {
            if ("emailAlreadyExists".equals(e.getMessage())) {
                memberDTO.setEmail(faker.internet().emailAddress());
                memberDTO.setNickname(faker.name().name());
                userId = memberService.register(memberDTO);
            }
        }
    }

    @Test
    public void testGet() {
        MemberDTO memberDTO = memberService.get(userId);
        log.info(memberDTO);
    }

    @Test
    public void testModify() {
        MemberDTO memberDTO = MemberDTO.builder()
                .id(userId)
                .email("Modified@example.com")
                .password("NewPassword")
                .nickname("ModifiedUser")
                .role(MemberRole.MANAGER)
                .build();
        memberService.modify(memberDTO);

        MemberDTO result = memberService.get(userId);
        log.info(result);
    }

    @Test
    public void testRemove() {
        memberService.remove(userId);
        Assertions.assertThrows(NoSuchElementException.class, () -> memberService.get(userId));
    }

}
