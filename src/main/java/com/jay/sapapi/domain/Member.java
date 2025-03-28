package com.jay.sapapi.domain;

import com.jay.sapapi.domain.common.TimeStampedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"posts", "comments", "postLikes"})
public class Member extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "올바른 이메일 주소 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
    )
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    @Pattern(regexp = "^\\S+$", message = "닉네임에 공백 불가")
    @Column(unique = true, nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Builder.Default
    private MemberRole memberRole = MemberRole.USER;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "commenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

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
