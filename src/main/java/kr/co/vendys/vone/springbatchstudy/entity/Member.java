package kr.co.vendys.vone.springbatchstudy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class Member {

    @Id
    private Long id;

    private String name;

    private Integer version;

    private LocalDateTime createDate;

    private String createUser;

    private LocalDateTime updateDate;

    private String updateUser;

    @Builder
    public Member(Long id, String name, Integer version, LocalDateTime createDate, String createUser, LocalDateTime updateDate, String updateUser) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.createDate = createDate;
        this.createUser = createUser;
        this.updateDate = updateDate;
        this.updateUser = updateUser;
    }
}













