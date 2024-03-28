package org.changppo.cost_management_service.dto.apikey;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.cost_management_service.entity.apikey.GradeType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiKeyDto {
    private Long id;
    private String value;
    private GradeType grade;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
}
