package com.jay.sapapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.sapapi.config.RestDocsConfiguration;
import com.jay.sapapi.domain.MemberRole;
import com.jay.sapapi.dto.MemberDTO;
import com.jay.sapapi.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(MemberController.class)
public class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestDocumentationResultHandler restDocumentationResultHandler;

    @MockitoBean
    private MemberService memberService;

    private MemberDTO memberDTO;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocumentationResultHandler)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        memberDTO = MemberDTO.builder()
                .id(1L)
                .email("sample@example.com")
                .nickname("sampleUser")
                .profileImageUrl("sampleImage.png")
                .role(MemberRole.USER)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void testGetMember() throws Exception {
        Mockito.when(memberService.get(1L)).thenReturn(memberDTO);
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterMember() throws Exception {
        MemberDTO registerMemberDTO = MemberDTO.builder()
                .email("sample@example.com")
                .password("samplePassword")
                .nickname("sampleUser")
                .profileImageUrl("sampleImage.png")
                .build();
        Mockito.when(memberService.register(any(MemberDTO.class))).thenReturn(1L);
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/users/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerMemberDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testModifyMember() throws Exception {
        MemberDTO modifyMemberDTO = MemberDTO.builder()
                .email("modified@example.com")
                .password("newPassword")
                .nickname("modifiedUser")
                .profileImageUrl("modifiedImage.png")
                .build();

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/api/users/{userId}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(modifyMemberDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveMember() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

}
