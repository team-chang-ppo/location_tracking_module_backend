package org.changppo.account.batch.config;

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig extends DefaultBatchConfiguration {

    private final DataSource mainDataSource;
    private final PlatformTransactionManager mainTransactionManager;

    public BatchConfig(@Qualifier(DataSourceConfig.META_DATASOURCE) DataSource metaDataSource, @Qualifier(JpaConfig.META_TRANSACTION_MANAGER) PlatformTransactionManager metaTransactionManager) {
        this.mainDataSource = metaDataSource;
        this.mainTransactionManager = metaTransactionManager;
    }

    @Override
    protected ExecutionContextSerializer getExecutionContextSerializer() {
        return new Jackson2ExecutionContextStringSerializer();
    }

    @Override
    protected boolean getValidateTransactionState() {
        return false;
    }

    @Override
    protected DataSource getDataSource() {
        return mainDataSource;
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return mainTransactionManager;
    }
}
