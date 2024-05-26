package org.changppo.account.batch.config;

import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class CustomTaskConfigurer extends DefaultTaskConfigurer {

    private final PlatformTransactionManager metaTransactionManager;

    public CustomTaskConfigurer(DataSource dataSource, String tablePrefix, ApplicationContext applicationContext, PlatformTransactionManager metaTransactionManager) {
        super(dataSource, tablePrefix, applicationContext);
        this.metaTransactionManager = metaTransactionManager;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return metaTransactionManager;
    }

}
