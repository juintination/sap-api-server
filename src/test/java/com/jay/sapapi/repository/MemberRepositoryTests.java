package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MemberRepositoryTests")
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    private Long userId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        log.info(memberRepository.getClass().getName());
    }

    @BeforeEach
    public void insertMember() {
        String email = "sample@example.com";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(faker.internet().password(8, 20, true, true)))
                .nickname("sampleUser")
                .memberRole(MemberRole.USER)
                .build();
        if (!memberRepository.existsByEmail(email)) {
            userId = memberRepository.save(member).getId();
        }
    }

    @AfterEach
    public void cleanup() {
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("ID로 회원 조회")
        public void testReadById() {
            Optional<Member> member = memberRepository.findById(userId);
            member.ifPresent(value ->
                    Assertions.assertEquals("sample@example.com", value.getEmail())
            );
        }

        @Test
        @DisplayName("이메일로 회원 조회")
        public void testReadByEmail() {
            String email = "sample@example.com";
            Member member = memberRepository.findByEmail(email);
            Assertions.assertEquals(userId, member.getId());
        }

    }

    @Nested
    @DisplayName("회원 존재 여부 테스트")
    class ExistTests {

        @Test
        @DisplayName("이메일로 회원 존재 여부 확인")
        public void testExistsByEmail() {
            String email = "sample@example.com";
            Assertions.assertTrue(memberRepository.existsByEmail(email));
        }

        @Test
        @DisplayName("닉네임으로 회원 존재 여부 확인")
        public void testExistsByNickname() {
            String nickname = "sampleUser";
            Assertions.assertTrue(memberRepository.existsByNickname(nickname));
        }

    }

}
