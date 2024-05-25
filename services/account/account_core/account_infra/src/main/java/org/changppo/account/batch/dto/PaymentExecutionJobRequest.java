package org.changppo.account.batch.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentExecutionJobRequest {
    private Long memberId;
    private BigDecimal amount;
    @JsonSerialize(using = LocalDateSerializer.class)  // ObjectMapper issue
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
}
