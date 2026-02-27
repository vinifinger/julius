package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Optional<Account> findById(UUID id);

    List<Account> findByUserId(UUID userId);

    Account save(Account account);

    BigDecimal sumBalanceByUserId(UUID userId);

}
