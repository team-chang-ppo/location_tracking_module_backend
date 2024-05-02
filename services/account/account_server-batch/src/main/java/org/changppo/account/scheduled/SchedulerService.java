package org.changppo.account.scheduled;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SchedulerService {

    private final Scheduler scheduler;

    public <T extends QuartzJobBean> void addJob(Class<T> jobClass, final String jobName, String description, Map<String, Object> paramsMap, String cronExpression) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(jobClass, jobName, description, paramsMap);
        Trigger trigger = buildCronTrigger(jobDetail, cronExpression, jobName);

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private <T extends QuartzJobBean> JobDetail buildJobDetail(Class<T> jobClass, String jobName, String description, Map<String, Object> params) {
        JobDataMap jobDataMap = new JobDataMap(params);
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobName)
                .withDescription(description)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronTrigger(JobDetail jobDetail, String cronExp, String triggerName){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}
