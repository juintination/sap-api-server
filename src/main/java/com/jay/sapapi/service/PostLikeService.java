package com.jay.sapapi.service;

import com.jay.sapapi.domain.PostLike;
import com.jay.sapapi.dto.postlike.request.PostLikeCreateRequestDTO;
import com.jay.sapapi.dto.postlike.response.PostLikeResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PostLikeService {

    @Transactional(readOnly = true)
    PostLikeResponseDTO get(Long postId, Long userId);

    @Transactional(readOnly = true)
    List<PostLikeResponseDTO> getHeartsByPost(Long postId);

    @Transactional(readOnly = true)
    List<PostLikeResponseDTO> getHeartsByMember(Long userId);

    Long register(Long postId, Long userId);

    void remove(Long postId, Long userId);

    PostLike dtoToEntity(PostLikeCreateRequestDTO postLikeDTO);

    default PostLikeResponseDTO entityToDTO(PostLike postLike) {
        return PostLikeResponseDTO.builder()
                .id(postLike.getId())
                .postId(postLike.getPost().getId())
                .userId(postLike.getMember().getId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }

}
