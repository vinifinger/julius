package com.finance.app.domain.service;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionType;

public class TransactionService {

    public void processTransaction(Transaction transaction, Account account) {
        if (transaction.isPaid()) {
            account.updateBalance(transaction.getAmount(), transaction.getType());
        }
    }

    public void reverseTransaction(Transaction transaction, Account account) {
        if (transaction.isPaid()) {
            TransactionType reversedType = TransactionType.EXPENSE.equals(transaction.getType())
                    ? TransactionType.REVENUE
                    : TransactionType.EXPENSE;
            account.updateBalance(transaction.getAmount(), reversedType);
        }
    }

}
