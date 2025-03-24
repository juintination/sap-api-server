package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.PostLikeDTO;
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
public class PostLikeServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

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
                .userId(userId)
                .build());

        for (int i = 0; i < 5; i++) {
            MemberDTO memberDTO = MemberDTO.builder()
                    .email(faker.internet().emailAddress())
                    .password(faker.internet().password())
                    .nickname(faker.name().name())
                    .role(MemberRole.USER)
                    .build();
            this.userId = memberService.register(memberDTO);

            postLikeService.register(PostLikeDTO.builder()
                    .postId(postId)
                    .userId(this.userId)
                    .build());
        }

    }

    @Test
    public void testGet() {
        PostLikeDTO postLikeDTO = postLikeService.get(postId, userId);
        Assertions.assertNotNull(postLikeDTO);
        log.info("HeartDTO: " + postLikeDTO);
    }

    @Test
    public void testGetListByPostId() {
        List<PostLikeDTO> result = postLikeService.getHeartsByPost(postId);
        log.info("List: " + result);
    }

    @Test
    public void testRemove() {
        postLikeService.remove(postId, userId);
        Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.get(postId, userId));
    }

    @Test
    public void testDeleteByPost() {
        List<PostLikeDTO> hearts = postLikeService.getHeartsByPost(postId);
        postService.remove(postId);

        for (PostLikeDTO heart : hearts) {
            Assertions.assertThrows(NoSuchElementException.class, () -> postLikeService.get(heart.getPostId(), heart.getUserId()));
        }
    }

}
