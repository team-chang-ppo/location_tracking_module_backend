package org.changppo.account.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;

    @PostMapping("/executePayment")
    public ResponseEntity<PaymentExecutionJobResponse> executePayment(@RequestBody PaymentExecutionJobRequest req) {
        try {
            JobParameters jobParameters = createJobParameters(req);
            JobExecution jobExecution = createJobExecution(jobParameters);
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                PaymentCardInfo paymentCardInfo =  (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
                return ResponseEntity.status(HttpStatus.OK).body(new PaymentExecutionJobResponse(paymentCardInfo.getType(), paymentCardInfo.getIssuerCorporation(), paymentCardInfo.getBin()));
            }
        } catch (Exception e) {
            log.error("Failed to process payment execution for User ID: {}", req.getMemberId(), e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new PaymentExecutionJobResponse(null, null, null));
    }

    private JobParameters createJobParameters(PaymentExecutionJobRequest req) {
        return new JobParametersBuilder()
                .addLong("memberId", req.getMemberId())
                .addString("amount", req.getAmount().stripTrailingZeros().toPlainString()) // 불필요한 0을 제거
                .addLocalDateTime("date", req.getDate())
                .toJobParameters();
    }

    private JobExecution createJobExecution(JobParameters jobParameters) {
        return Optional.ofNullable(jobRepository.getLastJobExecution(paymentExecutionJob.getName(), jobParameters))
                .map(jobExecution -> {
                    if (jobExecution.getStatus() == BatchStatus.FAILED) {
                        try {
                            return jobLauncher.run(paymentExecutionJob, jobParameters);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return jobExecution;
                })
                .orElseThrow(() -> new RuntimeException("Failed to get Last JobExecution"));
    }
}

