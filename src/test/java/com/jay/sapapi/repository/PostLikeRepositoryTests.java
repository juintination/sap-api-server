package com.jay.sapapi.repository;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.domain.Post;
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

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PostLikeRepositoryTests")
public class PostLikeRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    private final int HEART_COUNT = 5;

    private Long postId, userId, heartId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(memberRepository, "MemberRepository should not be null");
        Assertions.assertNotNull(postRepository, "PostRepository should not be null");
        Assertions.assertNotNull(postLikeRepository, "HeartRepository should not be null");

        log.info(memberRepository.getClass().getName());
        log.info(postRepository.getClass().getName());
        log.info(postLikeRepository.getClass().getName());
    }

    @BeforeEach
    public void insertPostLikes() {

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

        for (int i = 0; i < HEART_COUNT; i++) {
            Member member = memberRepository.save(Member.builder()
                    .email(faker.internet().emailAddress())
                    .password(passwordEncoder.encode(faker.internet().password()))
                    .nickname(faker.name().name())
                    .memberRole(MemberRole.USER)
                    .build());
            userId = member.getId();

            heartId = postLikeRepository.save(PostLike.builder()
                    .post(savedPost)
                    .member(member)
                    .build()).getId();
        }
    }

    @AfterEach
    public void cleanup() {
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("단일 게시글 좋아요 조회")
        @Transactional(readOnly = true)
        public void testRead() {
            Optional<PostLike> result = postLikeRepository.findById(heartId);
            PostLike postLike = result.orElseThrow();
            Assertions.assertEquals(postId, postLike.getPost().getId());
            Assertions.assertEquals(userId, postLike.getMember().getId());
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 단일 게시글 좋아요 조회")
        public void testReadWithoutTransactional() {
            Optional<PostLike> result = postLikeRepository.findByPostIdAndUserId(postId, userId);
            PostLike postLike = result.orElseThrow();
            Assertions.assertEquals(heartId, postLike.getId());
        }

        @Test
        @DisplayName("JOIN FETCH 쿼리를 사용하여 @Transactional 없이 게시글에 Id로 좋아요 리스트 조회")
        public void testReadListByPost() {
            List<PostLike> postLikes = postLikeRepository.getPostLikesByPostOrderByCreatedAt(Post.builder().id(postId).build());
            postLikes.forEach(log::info);
            Assertions.assertEquals(HEART_COUNT, postLikes.size());
        }

    }

    @Nested
    @DisplayName("삭제 테스트")
    class DeleteTests {

        @Test
        @DisplayName("게시글 좋아요 삭제")
        public void testDelete() {
            postLikeRepository.deleteById(heartId);
            Optional<PostLike> result = postLikeRepository.findById(heartId);
            Assertions.assertEquals(Optional.empty(), result);
        }

        @Test
        @DisplayName("게시글 삭제 시 좋아요 삭제")
        public void testDeleteByPostDelete() {
            List<PostLike> postLikes = postLikeRepository.getPostLikesByPostOrderByCreatedAt(Post.builder().id(postId).build());
            postRepository.deleteById(postId);
            postLikes.forEach(postLike -> {
                Optional<PostLike> result = postLikeRepository.findById(postLike.getId());
                Assertions.assertEquals(Optional.empty(), result);
            });
        }

        @Test
        @DisplayName("회원 삭제 시 좋아요 삭제")
        public void testDeleteByMemberDelete() {
            memberRepository.deleteById(userId);
            Optional<PostLike> postLike = postLikeRepository.findById(heartId);
            Assertions.assertEquals(Optional.empty(), postLike);
        }

    }

}
