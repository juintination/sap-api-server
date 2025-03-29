package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.post.request.PostCreateRequestDTO;
import com.jay.sapapi.dto.post.request.PostModifyRequestDTO;
import com.jay.sapapi.dto.post.response.PostResponseDTO;
import com.jay.sapapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public PostResponseDTO get(Long postId) {
        Object result = postRepository.getPostByPostId(postId);
        if (result == null) {
            throw new NoSuchElementException("postNotFound");
        }
        Object[] arr = (Object[]) result;
        return entityToDTO((Post) arr[0], (Member) arr[1], ((Number) arr[2]).longValue());
    }

    @Override
    public void incrementViewCount(Long postId) {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow(() -> new NoSuchElementException("postNotFound"));
        post.incrementViewCount();
        postRepository.save(post);
    }

    @Override
    public void incrementLikeCount(Long postId) {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow(() -> new NoSuchElementException("postNotFound"));
        post.incrementLikeCount();
        postRepository.save(post);
    }

    @Override
    public void decrementLikeCount(Long postId) {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow(() -> new NoSuchElementException("postNotFound"));
        post.decrementLikeCount();
        postRepository.save(post);
    }

    @Override
    public List<PostResponseDTO> getList() {
        List<Object> postList = postRepository.getAllPosts();
        return postList.stream().map(arr -> {
            Object[] entityArr = (Object[]) arr;
            return entityToDTO((Post) entityArr[0], (Member) entityArr[1],
                    ((Number) entityArr[2]).longValue());
        }).toList();
    }

    @Override
    public Long register(PostCreateRequestDTO postDTO) {
        Post post = dtoToEntity(postDTO);
        Post result = postRepository.save(post);
        return result.getId();
    }

    @Override
    public void modify(Long postId, PostModifyRequestDTO postDTO) {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow(() -> new NoSuchElementException("postNotFound"));
        post.changeTitle(postDTO.getTitle());
        post.changeContent(postDTO.getContent());

        if (postDTO.getPostImageUrl() != null && !postDTO.getPostImageUrl().isEmpty()) {
            post.changePostImageUrl(postDTO.getPostImageUrl());
        }

        postRepository.save(post);
    }

    @Override
    public void remove(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("postNotFound");
        }
        postRepository.deleteById(postId);
    }

    @Override
    public Post dtoToEntity(PostCreateRequestDTO postDTO) {
        return Post.builder()
                .writer(Member.builder().id(postDTO.getUserId()).build())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .postImageUrl(postDTO.getPostImageUrl())
                .build();
    }

}
