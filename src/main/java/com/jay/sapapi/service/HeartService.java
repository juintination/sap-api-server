package com.jay.sapapi.service;

import com.jay.sapapi.domain.Heart;
import com.jay.sapapi.dto.HeartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface HeartService {

    HeartDTO get(Long postId, Long userId);

    List<HeartDTO> getHeartsByPost(Long postId);

    Long register(HeartDTO heartDTO);

    void remove(Long postId, Long userId);

    Heart dtoToEntity(HeartDTO heartDTO);

    default HeartDTO entityToDTO(Heart heart) {
        return HeartDTO.builder()
                .heartId(heart.getHeartId())
                .postId(heart.getPost().getPostId())
                .userId(heart.getMember().getUserId())
                .regDate(heart.getRegDate())
                .build();
    }

}
