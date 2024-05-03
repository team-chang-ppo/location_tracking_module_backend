package org.changppo.account.scheduled;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.changppo.account.batch.AutomaticPaymentExecutionJobRunner;
import org.changppo.account.batch.DeletionExecutionJobRunner;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final SchedulerService schedulerService;

    @PostConstruct
    public void init() throws SchedulerException {
        schedulerService.addJob(
                AutomaticPaymentExecutionJobRunner.class,
                "AutomaticPaymentExecutionJob",
                "Job to process payments periodically",
                Map.of("Application Start Time", LocalDateTime.now()),
                "0 0/1 * 1/1 * ? *"  //TODO. 매달 1일에 시작하도록 수정
        );

        schedulerService.addJob(
                DeletionExecutionJobRunner.class,
                "DeletionExecutionJobRunner",
                "Job to delete data periodically",
                Map.of("Application Start Time", LocalDateTime.now()),
                "0 0/1 * 1/1 * ? *"  //TODO. 매일 시작하도록 수정
        );
    }
}
