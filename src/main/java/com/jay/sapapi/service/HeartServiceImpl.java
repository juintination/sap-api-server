package com.jay.sapapi.service;

import com.jay.sapapi.domain.Heart;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.HeartDTO;
import com.jay.sapapi.repository.HeartRepository;
import com.jay.sapapi.util.exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

    private final HeartRepository heartRepository;

    @Override
    public HeartDTO get(Long postId, Long userId) {
        Optional<Heart> result = heartRepository.findByPostIdAndUserId(postId, userId);
        Heart heart = result.orElseThrow(() -> new NoSuchElementException("heartNotFound"));
        return entityToDTO(heart);
    }

    @Override
    public List<HeartDTO> getHeartsByPost(Long postId) {
        List<Heart> result = heartRepository.getHeartsByPostOrderByRegDate(Post.builder().postId(postId).build());
        return result.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Long register(HeartDTO heartDTO) {

        Optional<Heart> existingHeart = heartRepository.findByPostIdAndUserId(heartDTO.getPostId(), heartDTO.getUserId());
        if (existingHeart.isPresent()) {
            throw new CustomValidationException("heartAlreadyExists");
        }

        Heart heart = dtoToEntity(heartDTO);
        Heart result = heartRepository.save(heart);
        return result.getHeartId();
    }

    @Override
    public void remove(Long postId, Long userId) {
        Optional<Heart> existingHeart = heartRepository.findByPostIdAndUserId(postId, userId);
        if (existingHeart.isEmpty()) {
            throw new NoSuchElementException("heartNotFound");
        }
        Long heartId = existingHeart.get().getHeartId();
        heartRepository.deleteById(heartId);
    }

    @Override
    public Heart dtoToEntity(HeartDTO heartDTO) {
        return Heart.builder()
                .heartId(heartDTO.getHeartId())
                .member(Member.builder().userId(heartDTO.getUserId()).build())
                .post(Post.builder().postId(heartDTO.getPostId()).build())
                .build();
    }

}
