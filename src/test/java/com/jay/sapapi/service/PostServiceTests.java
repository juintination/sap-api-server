package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.member.MemberDTO;
import com.jay.sapapi.dto.post.PostDTO;
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
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PostServiceTests")
public class PostServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private final int POST_COUNT = 10;

    private Long postId, userId;

    private String title, content;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(postService, "PostService should not be null");
        Assertions.assertNotNull(memberService, "MemberService should not be null");

        log.info(postService.getClass().getName());
        log.info(memberService.getClass().getName());
    }

    @BeforeEach
    public void registerPosts() {

        MemberDTO memberDTO = MemberDTO.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .nickname(faker.name().name())
                .role(MemberRole.USER)
                .build();
        userId = memberService.register(memberDTO);

        for (int i = 0; i < POST_COUNT; i++) {
            title = faker.book().title();
            content = faker.lorem().sentence();
            postId = postService.register(PostDTO.builder()
                    .title(title)
                    .content(content)
                    .userId(userId)
                    .build());
        }

    }

    @AfterEach
    public void cleanup() {
        postService.getList().forEach(postDTO -> postService.remove(postDTO.getId()));
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("단일 게시글 조회")
        public void testGet() {
            PostDTO postDTO = postService.get(postId);
            Assertions.assertEquals(title, postDTO.getTitle());
            Assertions.assertEquals(content, postDTO.getContent());
        }

        @Test
        @DisplayName("게시글 조회수 증가")
        public void testIncreaseViewCount() {
            postService.incrementViewCount(postId);
            PostDTO postDTO = postService.get(postId);
            Assertions.assertEquals(1, postDTO.getViewCount());
        }

        @Test
        @DisplayName("게시글 목록 조회")
        public void testGetList() {
            List<PostDTO> result = postService.getList();
            Assertions.assertEquals(POST_COUNT, result.size());
        }

    }

    @Nested
    @DisplayName("게시글 등록 테스트")
    class RegisterTests {

        @Test
        @DisplayName("게시글 제목 없음")
        public void testRegisterEmailNull() {
            PostDTO postDTO = PostDTO.builder()
                    .title(null)
                    .content(faker.lorem().sentence())
                    .userId(userId)
                    .build();

            CustomValidationException e = Assertions.assertThrows(CustomValidationException.class, () -> postService.register(postDTO));
            Assertions.assertEquals("invalidPostTitle", e.getMessage());
        }

        @Test
        @DisplayName("게시글 내용 없음")
        public void testRegisterNicknameNull() {
            PostDTO postDTO = PostDTO.builder()
                    .title(faker.book().title())
                    .content(null)
                    .userId(userId)
                    .build();

            CustomValidationException e = Assertions.assertThrows(CustomValidationException.class, () -> postService.register(postDTO));
            Assertions.assertEquals("invalidPostContent", e.getMessage());
        }

        @Test
        @DisplayName("게시글 작성자 없음")
        public void testRegisterRoleNull() {
            PostDTO postDTO = PostDTO.builder()
                    .title(faker.book().title())
                    .content(faker.lorem().sentence())
                    .userId(null)
                    .build();

            Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> postService.register(postDTO));
        }

    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class ModifyTests {

        @Test
        @DisplayName("게시글 수정")
        public void testModify() {
            PostDTO postDTO = PostDTO.builder()
                    .id(postId)
                    .title("ModifiedTitle")
                    .content("ModifiedContent")
                    .postImageUrl(faker.internet().image())
                    .build();

            postService.modify(postDTO);

            PostDTO result = postService.get(postId);
            Assertions.assertEquals("ModifiedTitle", result.getTitle());
            Assertions.assertEquals("ModifiedContent", result.getContent());
            Assertions.assertNotNull(result.getPostImageUrl());
        }

        @Test
        @DisplayName("존재하지 않는 게시글 수정")
        public void testModifyInvalidPost() {
            PostDTO postDTO = PostDTO.builder()
                    .id(0L)
                    .title("ModifiedTitle")
                    .content("ModifiedContent")
                    .build();

            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postService.modify(postDTO));
            Assertions.assertEquals("postNotFound", e.getMessage());
        }

    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class RemoveTests {

        @Test
        @DisplayName("게시글 삭제")
        public void testRemove() {
            postService.remove(postId);
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postService.get(postId));
            Assertions.assertEquals("postNotFound", e.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 게시글 삭제")
        public void testRemoveInvalidPost() {
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> postService.remove(0L));
            Assertions.assertEquals("postNotFound", e.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 시 게시글 삭제")
        public void testRemoveByMemberDelete() {
            memberService.remove(userId);
            Assertions.assertThrows(NoSuchElementException.class, () -> postService.get(postId));
        }

    }

}
