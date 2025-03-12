package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
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

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
    }

    @Test
    @BeforeEach
    public void testInsert() {

        Member member = memberRepository.save(Member.builder()
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .nickname(faker.name().name())
                .memberRole(MemberRole.USER)
                .build());

        postRepository.save(Post.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .writer(member)
                .postImageUrl(faker.internet().image())
                .build());

    }

    @Test
    @Transactional
    public void testRead() {
        Long postId = 1L;
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow();

        Assertions.assertNotNull(post);
        log.info("Post: " + post);
        log.info("Writer: " + post.getWriter());
    }

    @Test
    public void testReadPostByPostId() {
        Long postId = 1L;
        Object result = postRepository.getPostByPostId(postId);
        Object[] arr = (Object[]) result;
        log.info(Arrays.toString(arr));
    }

    @Test
    public void testDelete() {
        Long postId = 1L;
        postRepository.deleteById(postId);
        Optional<Post> result = postRepository.findById(postId);

        Assertions.assertEquals(result, Optional.empty());
        log.info(result);
    }

}
