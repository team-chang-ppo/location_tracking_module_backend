package org.changppo.account.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
public class JpaConfig {
    public static final String META_ENTITY_MANAGER_FACTORY = "metaEntityManagerFactory";
    public static final String DOMAIN_ENTITY_MANAGER_FACTORY = "domainEntityManagerFactory";
    public static final String META_TRANSACTION_MANAGER = "metaTransactionManager";
    public static final String DOMAIN_TRANSACTION_MANAGER = "domainTransactionManager";
    public static final String CHAINED_TRANSACTION_MANAGER = "chainedTransactionManager";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders;
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Primary
    @Bean(name = META_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean metaEntityManagerFactory(
            @Qualifier(DataSourceConfig.META_DATASOURCE) DataSource dataSource)  {
        return EntityManagerFactoryCreator.builder()
                .properties(jpaProperties)
                .hibernateProperties(hibernateProperties)
                .metadataProviders(metadataProviders)
                .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                .dataSource(dataSource)
                .packages("org.changppo.account.batch")
                .persistenceUnit("metaUnit")
                .build()
                .create();
    }

    @Bean(name = DOMAIN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean domainEntityManagerFactory(
            @Qualifier(DataSourceConfig.DOMAIN_DATASOURCE) DataSource dataSource)  {
        return EntityManagerFactoryCreator.builder()
                .properties(jpaProperties)
                .hibernateProperties(hibernateProperties)
                .metadataProviders(metadataProviders)
                .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                .dataSource(dataSource)
                .packages("org.changppo.account.entity")
                .persistenceUnit("domainUnit")
                .build()
                .create();
    }

    @Primary
    @Bean(name = META_TRANSACTION_MANAGER)
    public PlatformTransactionManager metaTransactionManager(
            @Qualifier(META_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean(name = DOMAIN_TRANSACTION_MANAGER)
    public PlatformTransactionManager domainTransactionManager(
            @Qualifier(DOMAIN_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean(name = CHAINED_TRANSACTION_MANAGER)
    public PlatformTransactionManager chainedTransactionManager(
            @Qualifier(META_TRANSACTION_MANAGER) PlatformTransactionManager metaTransactionManager
            ,@Qualifier(DOMAIN_TRANSACTION_MANAGER) PlatformTransactionManager domainTransactionManager) {
        return new ChainedTransactionManager(metaTransactionManager, domainTransactionManager);
    }

    @Configuration
    @EnableJpaRepositories(
            basePackages = "org.changppo.account.batch"
            ,entityManagerFactoryRef = JpaConfig.META_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfig.META_TRANSACTION_MANAGER
    )
    public static class MetaJpaRepositoriesConfig{}

    @Configuration
    @EnableJpaRepositories(
            basePackages = "org.changppo.account.repository"
            ,entityManagerFactoryRef = JpaConfig.DOMAIN_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfig.DOMAIN_TRANSACTION_MANAGER
    )
    public static class DomainJpaRepositoriesConfig{}

}
