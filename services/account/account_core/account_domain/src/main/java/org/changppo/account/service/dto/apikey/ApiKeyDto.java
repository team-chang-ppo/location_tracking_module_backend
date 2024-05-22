package org.changppo.account.service.dto.apikey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.changppo.account.type.GradeType;

import java.time.LocalDateTime;

@Data
public class ApiKeyDto {
    private Long id;
    private String value;
    private GradeType grade;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentFailureBannedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cardDeletionBannedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public ApiKeyDto(Long id, String value, GradeType grade, LocalDateTime paymentFailureBannedAt, LocalDateTime cardDeletionBannedAt, LocalDateTime createdAt) {
        this.id = id;
        this.value = value;
        this.grade = grade;
        this.paymentFailureBannedAt = paymentFailureBannedAt;
        this.cardDeletionBannedAt = cardDeletionBannedAt;
        this.createdAt = createdAt;
    }
}
