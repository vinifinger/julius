package com.finance.app.domain.service;

import com.finance.app.domain.exception.InstallmentValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstallmentCalculator {

    private static final BigDecimal RESIDUE_TOLERANCE_PER_INSTALLMENT = new BigDecimal("0.01");

    public static BigDecimal calculateInstallmentAmount(BigDecimal totalAmount, int installments) {
        validateInstallmentCount(installments);
        return totalAmount.divide(BigDecimal.valueOf(installments), 2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateTotalAmount(BigDecimal installmentAmount, int installments) {
        validateInstallmentCount(installments);
        return installmentAmount.multiply(BigDecimal.valueOf(installments)).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static void validateHybridInput(BigDecimal totalAmount, BigDecimal installmentAmount, int installments) {
        validateInstallmentCount(installments);
        BigDecimal calculatedTotal = calculateTotalAmount(installmentAmount, installments);
        BigDecimal diff = totalAmount.subtract(calculatedTotal).abs();
        
        // Allowed divergence is 0.01 per installment
        BigDecimal maxAllowedDiff = RESIDUE_TOLERANCE_PER_INSTALLMENT.multiply(BigDecimal.valueOf(installments));
        
        if (diff.compareTo(maxAllowedDiff) > 0) {
            throw new InstallmentValidationException("Divergence between total amount and installment amount * n exceeds tolerance");
        }
    }

    public static BigDecimal calculateCurrentInstallmentAmount(
            int currentNumber, 
            int totalCount, 
            BigDecimal totalAmount, 
            BigDecimal installmentAmount, 
            BigDecimal sumOfPrevious) {
        
        if (currentNumber == totalCount) {
            return totalAmount.subtract(sumOfPrevious);
        }
        return installmentAmount;
    }

    public static void validateInstallmentCount(int installments) {
        if (installments < 2) {
            throw new InstallmentValidationException("Number of installments must be at least 2");
        }
    }
}
