package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.comment.CommentDTO;
import com.jay.sapapi.dto.member.request.MemberSignupRequestDTO;
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

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("CommentServiceTests")
public class CommentServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private final int COMMENT_COUNT = 5;

    private Long postId, commentId, commenterId;

    private String content;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(postService, "PostService should not be null");
        Assertions.assertNotNull(commentService, "CommentService should not be null");
        Assertions.assertNotNull(memberService, "MemberService should not be null");

        log.info(postService.getClass().getName());
        log.info(commentService.getClass().getName());
        log.info(memberService.getClass().getName());
    }

    @BeforeEach
    public void registerComments() {

        MemberSignupRequestDTO writerDTO = MemberSignupRequestDTO.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 20, true, true))
                .nickname(faker.regexify("[A-Za-z0-9]{5,10}"))
                .role(MemberRole.USER)
                .build();
        Long writerId = memberService.register(writerDTO);

        postId = postService.register(PostDTO.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .userId(writerId)
                .build());

        for (int i = 0; i < COMMENT_COUNT; i++) {
            MemberSignupRequestDTO memberDTO = MemberSignupRequestDTO.builder()
                    .email(faker.internet().emailAddress())
                    .password(faker.internet().password(8, 20, true, true))
                    .nickname(faker.regexify("[A-Za-z0-9]{5,10}"))
                    .role(MemberRole.USER)
                    .build();
            commenterId = memberService.register(memberDTO);

            content = faker.lorem().sentence();
            commentId = commentService.register(CommentDTO.builder()
                    .postId(postId)
                    .userId(commenterId)
                    .content(content)
                    .build());
        }

    }

    @AfterEach
    public void cleanup() {
        try {
            commentService.getCommentsByPostId(postId).forEach(commentDTO -> commentService.remove(commentDTO.getId()));
        } catch (NoSuchElementException e) {
            log.error(e.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 조회 테스트")
    class ReadTests {

        @Test
        @DisplayName("단일 댓글 조회")
        public void testGet() {
            CommentDTO commentDTO = commentService.get(commentId);
            Assertions.assertEquals(content, commentDTO.getContent());
        }

        @Test
        @DisplayName("게시글의 댓글 리스트 조회")
        public void testGetListByPostId() {
            List<CommentDTO> result = commentService.getCommentsByPostId(postId);
            Assertions.assertEquals(COMMENT_COUNT, result.size());
        }

    }

    @Nested
    @DisplayName("댓글 등록 테스트")
    class RegisterTests {

        @Test
        @DisplayName("댓글 내용 없음")
        public void testRegister() {
            CommentDTO dto = CommentDTO.builder()
                    .postId(postId)
                    .userId(commenterId)
                    .content(null)
                    .build();

            Assertions.assertThrows(CustomValidationException.class, () -> commentService.register(dto));
        }

    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class ModifyTests {

        @Test
        @DisplayName("댓글 수정")
        public void testModify() {
            CommentDTO commentDTO = commentService.get(commentId);
            commentDTO.setContent("ModifiedContent");
            commentService.modify(commentDTO);

            CommentDTO result = commentService.get(commentId);
            Assertions.assertEquals("ModifiedContent", result.getContent());
        }

        @Test
        @DisplayName("존재하지 않는 댓글 수정")
        public void testModifyInvalidComment() {
            CommentDTO commentDTO = CommentDTO.builder()
                    .id(0L)
                    .content("ModifiedContent")
                    .build();

            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> commentService.modify(commentDTO));
            Assertions.assertEquals("commentNotFound", e.getMessage());
        }

    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteTests {

        @Test
        @DisplayName("댓글 삭제")
        public void testRemove() {
            commentService.remove(commentId);
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> commentService.get(commentId));
            Assertions.assertEquals("commentNotFound", e.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 댓글 삭제")
        public void testRemoveNonExistent() {
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> commentService.remove(0L));
            Assertions.assertEquals("commentNotFound", e.getMessage());
        }

        @Test
        @DisplayName("게시글 삭제 시 댓글 삭제")
        public void testDeleteByPostDelete() {
            List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
            postService.remove(postId);

            comments.forEach(comment -> {
                NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> commentService.get(comment.getId()));
                Assertions.assertEquals("commentNotFound", e.getMessage());
            });
        }

        @Test
        @DisplayName("회원 삭제 시 댓글 삭제")
        public void testDeleteByMemberDelete() {
            memberService.remove(commenterId);
            NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> commentService.get(commentId));
            Assertions.assertEquals("commentNotFound", e.getMessage());
        }

    }

}
