package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.domain.Heart;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HeartRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HeartRepository heartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    private Long postId, userId, heartId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");
        Assertions.assertNotNull(heartRepository, "HeartRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
        log.info(heartRepository.getClass().getName());
    }

    @Test
    @BeforeEach
    public void testInsert() {

        Member writer = memberRepository.save(Member.builder()
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .nickname(faker.name().name())
                .memberRole(MemberRole.USER)
                .build());

        Post savedPost = postRepository.save(Post.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .writer(writer)
                .postImageUrl(faker.internet().image())
                .build());
        postId = savedPost.getId();

        for (int i = 0; i < 5; i++) {
            Member member = memberRepository.save(Member.builder()
                    .email(faker.internet().emailAddress())
                    .password(passwordEncoder.encode(faker.internet().password()))
                    .nickname(faker.name().name())
                    .memberRole(MemberRole.USER)
                    .build());
            userId = member.getId();

            heartId = heartRepository.save(Heart.builder()
                    .post(savedPost)
                    .member(member)
                    .build()).getId();
        }

    }

    @Test
    @Transactional
    public void testRead() {
        Optional<Heart> result = heartRepository.findById(heartId);
        Heart heart = result.orElseThrow();

        Assertions.assertNotNull(heart);
        log.info("Post: " + heart.getPost());
        log.info("Member: " + heart.getMember());
    }

    @Test
    public void testReadByPostIdAndUserId() {
        Optional<Heart> result = heartRepository.findByPostIdAndUserId(postId, userId);
        Heart heart = result.orElseThrow();
        Assertions.assertNotNull(heart);

        log.info("Post: " + heart.getPost());
        log.info("Member: " + heart.getMember());
    }

    @Test
    public void testReadListByPost() {
        List<Heart> hearts = heartRepository.getHeartsByPostOrderByRegDate(Post.builder().id(postId).build());
        Assertions.assertNotNull(hearts);
        hearts.forEach(log::info);
    }

    @Test
    public void testDelete() {
        heartRepository.deleteById(heartId);
        Optional<Heart> result = heartRepository.findById(heartId);

        Assertions.assertEquals(result, Optional.empty());
    }

    @Test
    public void testDeleteByPost() {
        List<Heart> hearts = heartRepository.getHeartsByPostOrderByRegDate(Post.builder().id(postId).build());
        postRepository.deleteById(postId);

        hearts.forEach(heart -> {
            Optional<Heart> result = heartRepository.findById(heart.getId());
            Assertions.assertEquals(result, Optional.empty());
        });
    }

}
