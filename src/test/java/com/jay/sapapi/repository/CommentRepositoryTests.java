package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.domain.Comment;
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
public class CommentRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    private Long postId, commentId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");
        Assertions.assertNotNull(commentRepository, "CommentRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
        log.info(commentRepository.getClass().getName());
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
            Member commenter = memberRepository.save(Member.builder()
                    .email(faker.internet().emailAddress())
                    .password(passwordEncoder.encode(faker.internet().password()))
                    .nickname(faker.name().name())
                    .memberRole(MemberRole.USER)
                    .build());

            commentId = commentRepository.save(Comment.builder()
                    .post(savedPost)
                    .commenter(commenter)
                    .content(faker.lorem().sentence())
                    .build()).getId();
        }

    }

    @Test
    @Transactional
    public void testRead() {
        Optional<Comment> result = commentRepository.findById(commentId);
        Comment comment = result.orElseThrow();

        Assertions.assertNotNull(comment);
        log.info("Post: " + comment.getPost());
        log.info("Commenter: " + comment.getCommenter());
    }

    @Test
    public void testReadWithoutTransactional() {
        Optional<Comment> result = commentRepository.getCommentByCommentId(commentId);
        Comment comment = result.orElseThrow();

        Assertions.assertNotNull(comment);
        log.info("Post: " + comment.getPost());
        log.info("Commenter: " + comment.getCommenter());
    }

    @Test
    public void testReadListByPost() {
        List<Comment> comments = commentRepository.getCommentsByPostOrderById(Post.builder().id(postId).build());
        Assertions.assertNotNull(comments);
        comments.forEach(log::info);
    }

    @Test
    public void testDelete() {
        commentRepository.deleteById(commentId);
        Optional<Comment> result = commentRepository.findById(commentId);

        Assertions.assertEquals(result, Optional.empty());
    }

    @Test
    public void testDeleteByPost() {
        List<Comment> comments = commentRepository.getCommentsByPostOrderById(Post.builder().id(postId).build());
        postRepository.deleteById(postId);

        comments.forEach(comment -> {
            Optional<Comment> result = commentRepository.findById(comment.getId());
            Assertions.assertEquals(result, Optional.empty());
        });
    }

}
