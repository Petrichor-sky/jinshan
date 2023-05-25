package com.haizhi.empower.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author Coder.JiA
 **/
@Configuration
public class MysqlDataSourceConfig {

   /* @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "hikari.mysql")
    public HikariDataSource dataSource() {
        return new HikariDataSource();
    }*/

    @Bean(name = "mysqlJdbcTpl")
    public JdbcTemplate mysqlJdbcTemplate(
            @Qualifier("db") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    /**
     * 定义事务管理器
     * @param dataSource 数据源
     * @return 事务管理器
     */
    @Bean(name = "mysqlTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("db") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 定义事务模板
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
