package com.jay.sapapi.service;

import com.github.javafaker.Faker;
import com.jay.sapapi.domain.MemberRole;
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

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostServiceTests {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    private final Faker faker = new Faker();

    private Long postId;

    @BeforeAll
    public void setup() {
        Assertions.assertNotNull(postService, "PostService should not be null");
        Assertions.assertNotNull(memberService, "MemberService should not be null");

        log.info(postService.getClass().getName());
        log.info(memberService.getClass().getName());
    }

    @Test
    @BeforeEach
    public void testRegister() {

        MemberDTO memberDTO = MemberDTO.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .nickname(faker.name().name())
                .role(MemberRole.USER)
                .build();
        Long userId = memberService.register(memberDTO);

        postId = postService.register(PostDTO.builder()
                .title(faker.book().title())
                .content(faker.lorem().sentence())
                .writerId(userId)
                .build());

    }

    @Test
    public void testGet() {
        PostDTO postDTO = postService.get(postId);
        Assertions.assertNotNull(postDTO);
        log.info("PostDTO: " + postDTO);
        log.info(postService.dtoToEntity(postDTO));
    }

    @Test
    public void testIncreaseViewCount() {
        postService.incrementViewCount(postId);
        PostDTO postDTO = postService.get(postId);
        Assertions.assertEquals(1, postDTO.getViewCount());
        log.info("PostDTO: " + postDTO);
        log.info("ViewCount: " + postDTO.getViewCount());
    }

    @Test
    public void testGetList() {
        List<PostDTO> result = postService.getList();
        log.info("List: " + result);
    }

    @Test
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
        log.info("Modified PostDTO: " + result);
    }

    @Test
    public void testRemove() {
        postService.remove(postId);
        Assertions.assertThrows(RuntimeException.class, () -> postService.get(postId));
    }

}
