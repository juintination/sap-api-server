package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.CommentDTO;
import com.jay.sapapi.dto.MemberDTO;
import com.jay.sapapi.dto.PostDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private Long postId, commentId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(postService, "PostService should not be null");
        Assertions.assertNotNull(commentService, "CommentService should not be null");
        Assertions.assertNotNull(memberService, "MemberService should not be null");

        log.info(postService.getClass().getName());
        log.info(commentService.getClass().getName());
        log.info(memberService.getClass().getName());
    }

    @Test
    @BeforeEach
    public void testRegister() {

        MemberDTO writerDTO = MemberDTO.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .nickname(faker.name().name())
                .role(MemberRole.USER)
                .build();
        Long userId = memberService.register(writerDTO);

        postId = postService.register(PostDTO.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .writerId(userId)
                .build());

        for (int i = 0; i < 5; i++) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .email(faker.internet().emailAddress())
                    .password(faker.internet().password())
                    .nickname(faker.name().name())
                    .role(MemberRole.USER)
                    .build();
            Long commenterId = memberService.register(memberDTO);

            commentId = commentService.register(CommentDTO.builder()
                    .postId(postId)
                    .commenterId(commenterId)
                    .content(faker.lorem().sentence())
                    .build());
        }

    }

    @Test
    public void testGet() {
        CommentDTO commentDTO = commentService.get(commentId);
        Assertions.assertNotNull(commentDTO);
        log.info("CommentDTO: " + commentDTO);
    }

    @Test
    public void testGetListByPostId() {
        List<CommentDTO> result = commentService.getCommentsByPostId(postId);
        log.info("List: " + result);
    }

    @Test
    public void testModify() {
        CommentDTO commentDTO = commentService.get(commentId);
        commentDTO.setContent("ModifiedContent");
        commentService.modify(commentDTO);

        CommentDTO result = commentService.get(commentId);
        Assertions.assertEquals("ModifiedContent", result.getContent());
        log.info("Modified CommentDTO: " + result);
    }

    @Test
    public void testRemove() {
        commentService.remove(commentId);
        Assertions.assertThrows(NoSuchElementException.class, () -> commentService.get(commentId));
    }

    @Test
    public void testDeleteByPost() {
        postService.remove(postId);

        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        Assertions.assertTrue(comments.isEmpty(), "Comments should be empty");
    }

}
