package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.member.request.MemberSignupRequestDTO;
import com.jay.sapapi.dto.post.request.PostCreateRequestDTO;
import com.jay.sapapi.dto.post.response.PostResponseDTO;
import com.jay.sapapi.dto.postlike.response.PostLikeResponseDTO;
import com.jay.sapapi.util.exception.CustomValidationException;
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

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PostLikeServiceTests")
public class PostLikeServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private final int POST_LIKE_COUNT = 5;

    private Long postId, userId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(postService, "PostService should not be null");
        Assertions.assertNotNull(postLikeService, "HeartService should not be null");
        Assertions.assertNotNull(memberService, "MemberService should not be null");

        log.info(postService.getClass().getName());
        log.info(postLikeService.getClass().getName());
        log.info(memberService.getClass().getName());
    }

    @BeforeEach
    public void registerPostLikes() {

        MemberSignupRequestDTO writerDTO = MemberSignupRequestDTO.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 20, true, true))
                .nickname(faker.regexify("[A-Za-z0-9]{5,10}"))
                .role(MemberRole.USER)
                .build();
        Long writerId = memberService.register(writerDTO);

        postId = postService.register(PostCreateRequestDTO.builder()
                .title(faker.lorem().characters(1, 20, true, true))
                .content(faker.lorem().sentence())
                .userId(writerId)
                .build());

        for (int i = 0; i < POST_LIKE_COUNT; i++) {
            MemberSignupRequestDTO memberDTO = MemberSignupRequestDTO.builder()
                    .email(faker.internet().emailAddress())
                    .password(faker.internet().password(8, 20, true, true))
                    .nickname(faker.regexify("[A-Za-z0-9]{5,10}"))
                    .role(MemberRole.USER)
                    .build();
            userId = memberService.register(memberDTO);

            postLikeService.register(postId, userId);
        }

    }

    @AfterEach
    public void cleanup() {
        try {
            postLikeService.getHeartsByPost(postId).forEach(postLikeDTO -> postLikeService.remove(postLikeDTO.getPostId(), postLikeDTO.getUserId()));
        } catch (NoSuchElementException e) {
            log.error(e.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 좋아요 조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("단일 게시글 좋아요 조회")
        public void testGet() {
            PostLikeResponseDTO postLikeDTO = postLikeService.get(postId, userId);
            Assertions.assertEquals(postId, postLikeDTO.getPostId());
            Assertions.assertEquals(userId, postLikeDTO.getUserId());
        }

        @Test
        @DisplayName("게시글의 좋아요 리스트 조회")
        public void testGetListByPostId() {
            List<PostLikeResponseDTO> result = postLikeService.getHeartsByPost(postId);
            Assertions.assertEquals(POST_LIKE_COUNT, result.size());
        }

    }

    @Nested
    @DisplayName("게시글 좋아요 등록 테스트")
    class RegisterTests {

        @Test
        @DisplayName("게시글 좋아요 중복 등록")
        public void testRegisterDuplicate() {
            CustomValidationException e = Assertions.assertThrows(CustomValidationException.class, () -> postLikeService.register(postId, userId));
            Assertions.assertEquals("heartAlreadyExists", e.getMessage());
        }

    }

    @Nested
    @DisplayName("게시글 좋아요 삭제 테스트")
    class RemoveTests {

        @Test
        @DisplayName("게시글 좋아요 삭제")
        public void testRemove() {
            postLikeService.remove(postId, userId);
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.get(postId, userId));
            Assertions.assertEquals("heartNotFound", e.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 게시글의 좋아요 삭제")
        public void testRemoveFail() {
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.remove(0L, userId));
            Assertions.assertEquals("heartNotFound", e.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 회원의 좋아요 삭제")
        public void testRemoveInvalidUser() {
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.remove(postId, 0L));
            Assertions.assertEquals("heartNotFound", e.getMessage());
        }

        @Test
        @DisplayName("게시글 삭제 시 좋아요 삭제")
        public void testDeleteByPostDelete() {
            List<PostLikeResponseDTO> hearts = postLikeService.getHeartsByPost(postId);
            postService.remove(postId);

            for (PostLikeResponseDTO heart : hearts) {
                NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.get(heart.getPostId(), heart.getUserId()));
                Assertions.assertEquals("heartNotFound", e.getMessage());
            }
        }

        @Test
        @DisplayName("회원 삭제 시 좋아요 삭제")
        public void testDeleteByMemberDelete() {
            memberService.remove(userId);
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.get(postId, userId));
            Assertions.assertEquals("heartNotFound", e.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 시 게시글 좋아요 수 감소")
        public void testDecreaseLikeCountByMemberDelete() {
            log.info("Before member delete: {}", postService.get(postId).getLikeCount());
            memberService.remove(userId);
            PostResponseDTO postDTO = postService.get(postId);
            log.info("After member delete: {}", postDTO.getLikeCount());
            Assertions.assertEquals(POST_LIKE_COUNT - 1, postDTO.getLikeCount());
        }

    }

}
