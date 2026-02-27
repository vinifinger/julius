package com.finance.app.infrastructure.config;

import com.finance.app.domain.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public TransactionService transactionService() {
        return new TransactionService();
    }

}
