package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.domain.Comment;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("CommentRepositoryTests")
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

    private final int COMMENT_COUNT = 5;

    private Long postId, commentId, commenterId;

    private String content;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");
        Assertions.assertNotNull(commentRepository, "CommentRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
        log.info(commentRepository.getClass().getName());
    }

    @BeforeEach
    public void insertComments() {

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

        for (int i = 0; i < COMMENT_COUNT; i++) {
            Member commenter = memberRepository.save(Member.builder()
                    .email(faker.internet().emailAddress())
                    .password(passwordEncoder.encode(faker.internet().password()))
                    .nickname(faker.name().name())
                    .memberRole(MemberRole.USER)
                    .build());
            commenterId = commenter.getId();

            content = faker.lorem().sentence();
            commentId = commentRepository.save(Comment.builder()
                    .post(savedPost)
                    .commenter(commenter)
                    .content(content)
                    .build()).getId();
        }
    }

    @AfterEach
    public void cleanup() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("Id로 댓글 조회")
        @Transactional(readOnly = true)
        public void testRead() {
            Optional<Comment> result = commentRepository.findById(commentId);
            Comment comment = result.orElseThrow();
            Assertions.assertEquals(content, comment.getContent());
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 Id로 댓글 조회")
        public void testReadWithoutTransactional() {
            Optional<Comment> result = commentRepository.getCommentByCommentId(commentId);
            Comment comment = result.orElseThrow();
            Assertions.assertEquals(content, comment.getContent());
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 게시글 Id로 댓글 리스트 조회")
        public void testReadListByPost() {
            List<Comment> comments = commentRepository.getCommentsByPostOrderById(Post.builder().id(postId).build());
            comments.forEach(log::info);
            Assertions.assertEquals(COMMENT_COUNT, comments.size());
        }

    }

    @Nested
    @DisplayName("삭제 테스트")
    class DeleteTests {

        @Test
        @DisplayName("Id로 댓글 삭제")
        public void testDelete() {
            commentRepository.deleteById(commentId);
            Optional<Comment> result = commentRepository.findById(commentId);
            Assertions.assertEquals(Optional.empty(), result);
        }

        @Test
        @DisplayName("게시글 삭제 시 댓글 삭제")
        public void testDeleteByPostDelete() {
            List<Comment> comments = commentRepository.getCommentsByPostOrderById(Post.builder().id(postId).build());
            postRepository.deleteById(postId);
            comments.forEach(comment -> {
                Optional<Comment> result = commentRepository.findById(comment.getId());
                Assertions.assertEquals(Optional.empty(), result);
            });
        }

        @Test
        @DisplayName("회원 삭제 시 댓글 삭제")
        public void testDeleteByMemberDelete() {
            memberRepository.deleteById(commenterId);
            Optional<Comment> comment = commentRepository.findById(commentId);
            Assertions.assertEquals(Optional.empty(), comment);
        }

    }

}
