package com.jay.sapapi.service;

import com.jay.sapapi.domain.PostLike;
import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.PostDTO;
import com.jay.sapapi.dto.PostLikeDTO;
import com.jay.sapapi.repository.PostLikeRepository;
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
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;

    private final PostService postService;

    @Override
    public PostLikeDTO get(Long postId, Long userId) {
        Optional<PostLike> result = postLikeRepository.findByPostIdAndUserId(postId, userId);
        PostLike postLike = result.orElseThrow(() -> new NoSuchElementException("heartNotFound"));
        return entityToDTO(postLike);
    }

    @Override
    public List<PostLikeDTO> getHeartsByPost(Long postId) {
        PostDTO postDTO = postService.get(postId);
        List<PostLike> result = postLikeRepository.getPostLikesByPostOrderByCreatedAt(postService.dtoToEntity(postDTO));
        return result.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Long register(Long postId, Long userId) {
        PostLikeDTO postLikeDTO = PostLikeDTO.builder()
                .postId(postId)
                .userId(userId)
                .build();

        Optional<PostLike> existingHeart = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingHeart.isPresent()) {
            throw new CustomValidationException("heartAlreadyExists");
        }

        PostLike postLike = postLikeRepository.save(dtoToEntity(postLikeDTO));
        return postLike.getId();
    }

    @Override
    public void remove(Long postId, Long userId) {
        Optional<PostLike> existingHeart = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingHeart.isEmpty()) {
            throw new NoSuchElementException("heartNotFound");
        }
        Long heartId = existingHeart.get().getId();
        postLikeRepository.deleteById(heartId);
    }

    @Override
    public PostLike dtoToEntity(PostLikeDTO postLikeDTO) {
        return PostLike.builder()
                .id(postLikeDTO.getId())
                .member(Member.builder().id(postLikeDTO.getUserId()).build())
                .post(Post.builder().id(postLikeDTO.getPostId()).build())
                .build();
    }

}
