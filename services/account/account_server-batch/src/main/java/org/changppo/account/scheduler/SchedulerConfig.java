package org.changppo.account.scheduler;

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
                Map.of("SchedulerStartTime", LocalDateTime.now()),
                "0 0 1 ? * TUE" //시범 운영을 위해 일주일 단위로 실행 TODO. 매월 시작하도록 수정
        );

        schedulerService.addJob(
                DeletionExecutionJobRunner.class,
                "DeletionExecutionJobRunner",
                "Job to delete data periodically",
                Map.of("SchedulerStartTime", LocalDateTime.now()),
                "0 0 3 * * ?"
        );
    }
}
