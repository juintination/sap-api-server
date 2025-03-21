package com.jay.sapapi.service;

import com.jay.sapapi.domain.Member;
import com.jay.sapapi.domain.Post;
import com.jay.sapapi.dto.PostDTO;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface PostService {

    PostDTO get(Long postId);

    void incrementViewCount(Long postId);

    List<PostDTO> getList();

    Long register(PostDTO postDTO);

    void modify(PostDTO postDTO);

    void remove(Long postId);

    Post dtoToEntity(PostDTO postDTO);

    default PostDTO entityToDTO(Post post, Member writer, int commentCount, int likeCount) {
        return PostDTO.builder()
                .id(post.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerEmail(writer.getEmail())
                .writerProfileImageUrl(writer.getProfileImageUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postImageUrl(post.getPostImageUrl())
                .commentCount(commentCount)
                .likeCount(likeCount)
                .regDate(post.getRegDate())
                .modDate(post.getModDate())
                .build();
    }

}
