package org.changppo.cost_management_service.entity.apikey;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.changppo.cost_management_service.entity.member.Member;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE api_key SET deleted_at = CURRENT_TIMESTAMP WHERE api_key_id = ?")
@SQLRestriction("deleted_at is NULL")
public class ApiKey extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_key_id")
    private Long id;

    @Column(name = "`value`", unique = true, nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime bannedAt; // TODO. 정기 결제 실패로 인한 정지. 추후 다른 이유로 인한 정지 기능 추가. member의 정지 해제로 인한 key의 정지 해제 기능 구현 시 다른 사유의 정지까지 해제 해버리는 것 주의 필요

    @Builder
    public ApiKey(String value, Grade grade, Member member) {
        this.value = value;
        this.grade = grade;
        this.member = member;
        this.deletedAt = null;
        this.bannedAt = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isBanned() {
        return this.bannedAt != null;
    }

    public void updateValue(String value){
        this.value = value;
    }

    public void ban() {
        this.bannedAt = LocalDateTime.now();
    }

    public void unban() {
        this.bannedAt = null;
    }
}
