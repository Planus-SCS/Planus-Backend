package scs.planus.domain.member.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import scs.planus.domain.BaseTimeEntity;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.TodoCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    private String birth;

    private String email;

    private String password;

    private String description;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<TodoCategory> todoCategories = new ArrayList<>();

    @Builder
    public Member(String name, String nickname, String birth, String email, String password, String description,
                  String profileImageUrl, SocialType socialType, Status status, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
        this.socialType = socialType;
        this.status = status;
        this.role = role;
    }

    public void updateProfile(String nickname, String description, String profileImageUrl) {
        this.nickname = nickname;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
    }

    public void changeStatusToInactive() {
        this.status = Status.INACTIVE;
    }

    // 탈퇴 후, 재가입시 정보 초기화 메서드
    public void init(String nickname) {
        this.nickname = nickname;
        this.description = null;
        this.profileImageUrl = null;
        this.status = Status.ACTIVE;
    }
}
