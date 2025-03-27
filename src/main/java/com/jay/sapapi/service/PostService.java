package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.post.request.PostCreateRequestDTO;
import com.jay.sapapi.dto.post.request.PostModifyRequestDTO;
import com.jay.sapapi.dto.post.response.PostResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PostService {

    @Transactional(readOnly = true)
    PostResponseDTO get(Long postId);

    void incrementViewCount(Long postId);

    @Transactional(readOnly = true)
    List<PostResponseDTO> getList();

    Long register(PostCreateRequestDTO postCreateRequestDTO);

    void modify(Long postId, PostModifyRequestDTO postDTO);

    void remove(Long postId);

    Post dtoToEntity(PostCreateRequestDTO postDTO);

    Post responseDtoToEntity(PostResponseDTO postResponseDTO);

    default PostResponseDTO entityToDTO(Post post, Member writer, int commentCount, int likeCount) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .userId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerEmail(writer.getEmail())
                .writerProfileImageUrl(writer.getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postImageUrl(post.getPostImageUrl())
                .commentCount(commentCount)
                .likeCount(likeCount)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

}
