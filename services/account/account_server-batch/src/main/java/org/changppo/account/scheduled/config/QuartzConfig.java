package org.changppo.account.scheduled.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;
import java.io.IOException;
import java.util.Properties;

import static org.changppo.account.batch.config.database.TransactionManagerConfig.META_TRANSACTION_MANAGER;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;
    private final PlatformTransactionManager metaTransactionManager;

    public QuartzConfig(ApplicationContext applicationContext, @Qualifier(META_TRANSACTION_MANAGER)PlatformTransactionManager metaTransactionManager) {
        this.applicationContext = applicationContext;
        this.metaTransactionManager = metaTransactionManager;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        AutoWiringSpringBeanJobFactory autoWiringSpringBeanJobFactory = new AutoWiringSpringBeanJobFactory();

        autoWiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(autoWiringSpringBeanJobFactory);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setTransactionManager(metaTransactionManager);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        return schedulerFactoryBean;
    }

    private Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("security/quartz.properties"));
        Properties properties = null;

        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);

            return job;
        }
    }

}
