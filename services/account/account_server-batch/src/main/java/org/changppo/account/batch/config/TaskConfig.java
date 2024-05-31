package org.changppo.account.batch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.batch.listener.TaskBatchExecutionListener;
import org.springframework.cloud.task.batch.listener.support.JdbcTaskBatchDao;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.changppo.account.batch.config.database.DataSourceConfig.META_DATASOURCE;
import static org.changppo.account.batch.config.database.TransactionManagerConfig.META_TRANSACTION_MANAGER;

@Configuration
public class TaskConfig {

    private final DataSource metaDataSource;
    private final PlatformTransactionManager metaTransactionManager;
    private final ApplicationContext applicationContext;

    public TaskConfig(@Qualifier(META_DATASOURCE)DataSource metaDataSource, @Qualifier(META_TRANSACTION_MANAGER)PlatformTransactionManager metaTransactionManager, ApplicationContext applicationContext) {
        this.metaDataSource = metaDataSource;
        this.metaTransactionManager = metaTransactionManager;
        this.applicationContext = applicationContext;
    }

    @Bean
    public TaskConfigurer taskConfigurer() {
        return new CustomTaskConfigurer(metaDataSource, "BOOT3_TASK_", applicationContext, metaTransactionManager);
    }

    @Bean
    public TaskBatchExecutionListener taskBatchExecutionListener() {
        return new TaskBatchExecutionListener(new JdbcTaskBatchDao(metaDataSource, "BOOT3_TASK_"));
    }
}
