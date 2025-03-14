package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.domain.PostLike;
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
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    private Long postId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");
        Assertions.assertNotNull(commentRepository, "CommentRepository should not be null");
        Assertions.assertNotNull(postLikeRepository, "HeartRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
        log.info(commentRepository.getClass().getName());
        log.info(postLikeRepository.getClass().getName());
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

        Post savedPost = postRepository.save(Post.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .writer(member)
                .postImageUrl(faker.internet().image())
                .build());
        postId = savedPost.getId();

        for (int i = 0; i < 10; i++) {
            Comment comment = Comment.builder()
                    .post(savedPost)
                    .commenter(member)
                    .content(faker.lorem().sentence())
                    .build();
            commentRepository.save(comment);
        }

        PostLike postLike = PostLike.builder()
                .post(savedPost)
                .member(member)
                .build();
        postLikeRepository.save(postLike);

    }

    @Test
    @Transactional
    public void testRead() {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow();

        Assertions.assertNotNull(post);
        log.info("Post: " + post);
        log.info("Writer: " + post.getWriter());
    }

    @Test
    public void testReadPostByPostId() {
        Object result = postRepository.getPostByPostId(postId);
        Object[] arr = (Object[]) result;
        log.info(Arrays.toString(arr));
        log.info("Comments Count: " + arr[2]);
        log.info("Hearts Count: " + arr[3]);
    }

    @Test
    @Transactional
    public void testReadAll() {
        log.info("Read all posts");
        postRepository.findAll().forEach(post -> {
            log.info("Post: " + post);
            log.info("Writer: " + post.getWriter());
        });
    }

    @Test
    public void testReadAllWithoutTransactional() {
        log.info("Read all posts without @Transactional annotation");
        List<Object> result = postRepository.getAllPosts();
        result.forEach(arr -> {
            Object[] entity = (Object[]) arr;
            log.info(Arrays.toString(entity));
            log.info("Comments Count: " + entity[2]);
            log.info("Hearts Count: " + entity[3]);
        });
    }

    @Test
    public void testDelete() {
        postRepository.deleteById(postId);
        Optional<Post> result = postRepository.findById(postId);

        Assertions.assertEquals(result, Optional.empty());
        log.info(result);
    }

}
