package com.jay.sapapi.domain;

import com.jay.sapapi.domain.common.TimeStampedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"posts", "comments", "hearts"})
public class Member extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nickname;

    private String profileImageUrl;

    @Builder.Default
    private MemberRole memberRole = MemberRole.USER;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "commenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts;

    public void changeRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
