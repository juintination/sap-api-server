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

    private MemberDTO memberDTO;

    private Long userId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberService, "MemberService should not be null");
        log.info(memberService.getClass().getName());
    }

    @BeforeEach
    public void registerMember() {
        memberDTO = MemberDTO.builder()
                .email("sample@example.com")
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
        Assertions.assertEquals(this.memberDTO.getEmail(), memberDTO.getEmail());
        Assertions.assertEquals(this.memberDTO.getNickname(), memberDTO.getNickname());
    }

    @Test
    public void testExistsByEmail() {
        Assertions.assertTrue(memberService.existsByEmail(memberDTO.getEmail()));
    }

    @Test
    public void testExistsByNickname() {
        Assertions.assertTrue(memberService.existsByNickname(memberDTO.getNickname()));
    }

    @Test
    public void testCheckPasswordInvalidUser() {
        NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> memberService.checkPassword(0L, faker.internet().password()));
        Assertions.assertEquals("userNotFound", e.getMessage());
    }

    @Test
    public void testCheckPasswordInvalidPassword() {
        CustomValidationException e = Assertions.assertThrows(CustomValidationException.class, () -> memberService.checkPassword(userId, faker.internet().password()));
        Assertions.assertEquals("invalidPassword", e.getMessage());
    }

    @Test
    public void testModify() {
        String email = "modified@example.com";
        String nickname = "ModifiedUser";
        MemberDTO memberDTO = MemberDTO.builder()
                .id(userId)
                .email(email)
                .password(faker.internet().password())
                .nickname(nickname)
                .role(MemberRole.MANAGER)
                .build();
        memberService.modify(memberDTO);

        MemberDTO result = memberService.get(userId);
        Assertions.assertNotEquals(this.memberDTO.getEmail(), result.getEmail());
        Assertions.assertEquals(email, result.getEmail());

        Assertions.assertNotEquals(this.memberDTO.getNickname(), result.getNickname());
        Assertions.assertEquals(nickname, result.getNickname());
    }

    @Test
    public void testRemove() {
        memberService.remove(userId);
        Assertions.assertThrows(NoSuchElementException.class, () -> memberService.get(userId));
    }

}
