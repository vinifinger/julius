package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.web.dto.request.CreateAccountRequest;
import com.finance.app.web.dto.response.AccountResponse;
import com.finance.app.web.dto.response.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;

    public AccountResponse create(CreateAccountRequest request, UUID userId) {
        String currency = Objects.nonNull(request.currency()) ? request.currency() : "BRL";

        LocalDateTime now = LocalDateTime.now();
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .name(request.name())
                .balance(request.balance().setScale(2, RoundingMode.HALF_EVEN))
                .currency(currency)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.fromDomain(savedAccount);
    }

    public List<AccountResponse> listByUser(UUID userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountResponse::fromDomain)
                .toList();
    }

    public AccountResponse getBalance(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountResponse.fromDomain(account);
    }

    public BalanceResponse getTotalBalance(UUID userId) {
        BigDecimal totalBalance = accountRepository.sumBalanceByUserId(userId);
        return new BalanceResponse(totalBalance.setScale(2, RoundingMode.HALF_EVEN));
    }

}
