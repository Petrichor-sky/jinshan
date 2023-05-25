package com.haizhi.empower.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author CristianWindy
 * @使用多数据源时需要
 * @手动创建sessionfactory
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class SessionFactoryConfiguration implements EnvironmentAware {

    @Resource(name = "db")
    DataSource db;

    private String mapperLocations;

    @Bean("sessionFactory")
    public SqlSessionFactory sqlSessionFactory1() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(db);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }


    @Bean("sessionTemplate")
    public SqlSessionTemplate sessionTemplate1() throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory1());
        return sqlSessionTemplate;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.mapperLocations = environment.getProperty("mybatis.mapperLocations");
    }

    /**
     * 定义事务管理器
     *
     * @param dataSource 数据源
     * @return 事务管理器
     */
    @Bean(name = "mysqlTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("db") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 定义事务模板
     *
     * @param transactionManager 事务管理器
     * @return 事务模板
     * @throws Exception 异常
     */
    @Bean(name = "mysqlTransactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("mysqlTransactionManager") DataSourceTransactionManager transactionManager) throws Exception {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
        defaultTransactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        defaultTransactionDefinition.setTimeout(60);
        return new TransactionTemplate(transactionManager, defaultTransactionDefinition);
    }
}