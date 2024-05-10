package org.changppo.account.payment.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentExecutionJobRequest {
    private Long memberId;
    private BigDecimal amount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)  // ObjectMapper issue
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;
}
