package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.domain.Comment;
import com.jay.sapapi.domain.PostLike;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PostRepositoryTests")
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

    private Member member;

    private final int COMMENT_COUNT = 10, POST_COUNT = 10;

    private Long postId;

    private String title, content;

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

    @BeforeEach
    public void insertPosts() {
        member = memberRepository.save(Member.builder()
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .nickname(faker.regexify("[A-Za-z0-9]{5,10}"))
                .memberRole(MemberRole.USER)
                .build());

        title = faker.lorem().characters(1, 20, true, true);
        content = faker.lorem().sentence();

        for (int i = 0; i < POST_COUNT; i++) {
            Post savedPost = postRepository.save(Post.builder()
                    .title(title)
                    .content(content)
                    .writer(member)
                    .postImageUrl(faker.internet().image())
                    .build());
            postId = savedPost.getId();

            for (int j = 0; j < COMMENT_COUNT; j++) {
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
    }

    @AfterEach
    public void cleanup() {
        postLikeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("ID로 게시글 조회")
        @Transactional(readOnly = true)
        public void testRead() {
            Optional<Post> result = postRepository.findById(postId);
            Post post = result.orElseThrow();
            Assertions.assertEquals(member, post.getWriter());
            Assertions.assertEquals(title, post.getTitle());
            Assertions.assertEquals(content, post.getContent());
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 Id로 게시글 조회")
        public void testReadWithoutTransactional() {
            Object result = postRepository.getPostByPostId(postId);
            Object[] arr = (Object[]) result;
            log.info(Arrays.toString(arr));

            Post post = (Post) arr[0];
            Assertions.assertEquals(title, post.getTitle());
            Assertions.assertEquals(content, post.getContent());

            Member writer = (Member) arr[1];
            Assertions.assertEquals(member.toString(), writer.toString());

            Long commentsCount = (Long) arr[2];
            Assertions.assertEquals(COMMENT_COUNT, commentsCount);
            log.info("Comments Count: {}", commentsCount);
        }

        @Test
        @DisplayName("모든 게시글 조회")
        @Transactional(readOnly = true)
        public void testReadAll() {
            List<Post> result = postRepository.findAll();
            Assertions.assertEquals(POST_COUNT, result.size());
            result.forEach(post -> {
                log.info("Post: {}", post);
                Assertions.assertEquals(title, post.getTitle());
                Assertions.assertEquals(content, post.getContent());

                log.info("Writer: {}", post.getWriter());
                Assertions.assertEquals(member, post.getWriter());
            });
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 모든 게시글 조회")
        public void testReadAllWithoutTransactional() {
            List<Object> result = postRepository.getAllPosts();
            Assertions.assertEquals(POST_COUNT, result.size());
            result.forEach(arr -> {
                Object[] entity = (Object[]) arr;

                Post post = (Post) entity[0];
                Assertions.assertEquals(title, post.getTitle());
                Assertions.assertEquals(content, post.getContent());

                Member writer = (Member) entity[1];
                Assertions.assertEquals(member.toString(), writer.toString());

                Long commentsCount = (Long) entity[2];
                Assertions.assertEquals(COMMENT_COUNT, commentsCount);
                log.info("Comments Count: {}", commentsCount);
            });
        }

    }

    @Nested
    @DisplayName("삭제 테스트")
    class DeleteTests {

        @Test
        @DisplayName("Id로 게시글 삭제")
        public void testDelete() {
            postRepository.deleteById(postId);
            Optional<Post> result = postRepository.findById(postId);
            Assertions.assertEquals(result, Optional.empty());
        }

        @Test
        @DisplayName("회원 삭제 시 게시글 삭제")
        public void testDeleteByMemberDelete() {
            memberRepository.deleteById(member.getId());
            Optional<Post> post = postRepository.findById(postId);
            Assertions.assertEquals(Optional.empty(), post);
        }

    }

}
