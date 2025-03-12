package com.jay.sapapi.domain;

import com.jay.sapapi.domain.common.TimeStampedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "writer")
public class Post extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    private String content;

    private String postImageUrl;

    @Builder.Default
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    private Member writer;

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
