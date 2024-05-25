package org.changppo.account.batch.controller;

import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.changppo.account.batch.job.JobConfig.PAYMENT_JOB;

@RestController
@Slf4j
@RequestMapping("/batch")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;

    public BatchController(JobLauncher jobLauncher, JobRepository jobRepository, @Qualifier(PAYMENT_JOB) Job paymentExecutionJob) {
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.paymentExecutionJob = paymentExecutionJob;
    }

    @PostMapping("/executePayment")
    public ResponseEntity<PaymentExecutionJobResponse> executePayment(@RequestBody PaymentExecutionJobRequest req) {
        try {
            JobParameters jobParameters = createJobParameters(req);
            JobExecution jobExecution = createJobExecution(jobParameters);
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                PaymentExecutionJobResponse paymentExecutionJobResponse = extractPaymentDetails(jobExecution);
                return ResponseEntity.status(HttpStatus.OK).body(paymentExecutionJobResponse);
            }
        } catch (Exception e) {
            log.error("Failed to process payment execution for User ID: {}", req.getMemberId(), e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private JobParameters createJobParameters(PaymentExecutionJobRequest req) {
        return new JobParametersBuilder()
                .addLong("memberId", req.getMemberId())
                .addString("amount", req.getAmount().stripTrailingZeros().toPlainString()) // 불필요한 0을 제거
                .addLocalDate("date", req.getDate())
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

    private PaymentExecutionJobResponse extractPaymentDetails(JobExecution jobExecution) {
        String key = safeExtractString(jobExecution, "key");
        String cardType = safeExtractString(jobExecution, "cardType");
        String cardIssuerCorporation = safeExtractString(jobExecution, "cardIssuerCorporation");
        String cardBin = safeExtractString(jobExecution, "cardBin");
        return new PaymentExecutionJobResponse(key, cardType, cardIssuerCorporation, cardBin);
    }

    private String safeExtractString(JobExecution jobExecution, String key) {
        Object value = jobExecution.getExecutionContext().get(key);
        return value != null ? value.toString() : "Unknown";
    }
}
