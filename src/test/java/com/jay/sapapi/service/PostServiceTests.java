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
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Test
    public void testGet() {
        PostDTO postDTO = postService.get(postId);
        Assertions.assertEquals(title, postDTO.getTitle());
        Assertions.assertEquals(content, postDTO.getContent());
    }

    @Test
    public void testIncreaseViewCount() {
        postService.incrementViewCount(postId);
        PostDTO postDTO = postService.get(postId);
        Assertions.assertEquals(1, postDTO.getViewCount());
    }

    @Test
    public void testGetList() {
        List<PostDTO> result = postService.getList();
        Assertions.assertEquals(POST_COUNT, result.size());
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
    }

    @Test
    public void testRemove() {
        postService.remove(postId);
        Assertions.assertThrows(RuntimeException.class, () -> postService.get(postId));
    }

    @Test
    public void testRemoveByMember() {
        memberService.remove(userId);
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.get(postId));
    }

}
