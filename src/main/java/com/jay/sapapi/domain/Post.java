package com.jay.sapapi.domain;

import com.jay.sapapi.domain.common.TimeStampedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"writer", "comments" ,"postLikes"})
public class Post extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 20, message = "제목은 최대 20자까지 가능합니다.")
    @Column(nullable = false, length = 20)
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    @Column(nullable = false)
    private String content;

    @Size(max = 255, message = "이미지 URL은 최대 255자까지 가능합니다.")
    @Column(length = 255)
    private String postImageUrl;

    @Builder.Default
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member writer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changePostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

}
