package com.finance.app.infrastructure.parser;

import com.finance.app.domain.entity.ParsedTransaction;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.service.StatementParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NubankCsvParser implements StatementParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public List<ParsedTransaction> parse(InputStream inputStream) {
        List<ParsedTransaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Skip header
            if (line != null && !line.startsWith("Data,Valor")) {
                log.warn("CSV header does not match expected Nubank format");
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split by comma. We assume descriptions do not contain unescaped commas for Nubank.
                // A better approach for robust CSV is using a library, but NuBank statements are typically simple.
                // Let's use a regex that splits by comma but respects quotes, just in case.
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length < 4) {
                    log.warn("Skipping invalid CSV line: {}", line);
                    continue;
                }

                String dateStr = parts[0].trim();
                String amountStr = parts[1].trim();
                String externalId = parts[2].trim();
                String description = parts[3].trim().replaceAll("^\"|\"$", ""); // Remove quotes if any

                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                LocalDateTime dateTime = date.atStartOfDay();

                BigDecimal amount = new BigDecimal(amountStr);
                TransactionType type = amount.compareTo(BigDecimal.ZERO) < 0 ? TransactionType.EXPENSE : TransactionType.REVENUE;
                
                // Absolute value for amount
                BigDecimal finalAmount = amount.abs();

                transactions.add(new ParsedTransaction(externalId, description, finalAmount, dateTime, type));
            }
        } catch (Exception e) {
            log.error("Error parsing Nubank CSV file", e);
            throw new RuntimeException("Failed to parse CSV file", e);
        }

        return transactions;
    }

    @Override
    public boolean supports(String fileName, String contentType) {
        return fileName != null && fileName.toLowerCase().endsWith(".csv");
    }
}
