package org.example.firstlabis.config.jta;


import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * @author AlexV
 * Конфигурация transactionManager с использованим реализации Narayna
 */
@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig {
    @Bean
    public UserTransaction userTransaction() {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    @Bean
    public TransactionManager narayanaTransactionManager() {
        return com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    /**
     * Конфигурируем основной интерфейс по работе с транзакциями
     * И привязываем к нему реализацию Narayana
     */
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager jtaTransactionManager() {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(narayanaTransactionManager());
        jtaTransactionManager.setUserTransaction(userTransaction());
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        jtaTransactionManager.setNestedTransactionAllowed(true);
        jtaTransactionManager.setDefaultTimeout(60);
        return jtaTransactionManager;
    }
}
