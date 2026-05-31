package com.finance.app.infrastructure.parser;

import com.finance.app.domain.entity.ParsedTransaction;
import com.finance.app.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NubankCsvParserTest {

    private final NubankCsvParser parser = new NubankCsvParser();

    @Test
    void shouldParseNubankCsvCorrectly() {
        String csvContent = "Data,Valor,Identificador,Descrição\n" +
                "01/05/2026,-65.00,69f55fdd-58db-4f01-b2ad-ee4c96075523,Compra no débito - PizzariaMakelly\n" +
                "04/05/2026,8578.00,69f89fb1-3602-4e64-a8c0-dadd59472714,Transferência recebida\n";

        List<ParsedTransaction> transactions = parser.parse(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

        assertEquals(2, transactions.size());

        ParsedTransaction debit = transactions.get(0);
        assertEquals("69f55fdd-58db-4f01-b2ad-ee4c96075523", debit.externalId());
        assertEquals("Compra no débito - PizzariaMakelly", debit.description());
        assertEquals(new BigDecimal("65.00"), debit.amount());
        assertEquals(TransactionType.EXPENSE, debit.type());

        ParsedTransaction credit = transactions.get(1);
        assertEquals("69f89fb1-3602-4e64-a8c0-dadd59472714", credit.externalId());
        assertEquals("Transferência recebida", credit.description());
        assertEquals(new BigDecimal("8578.00"), credit.amount());
        assertEquals(TransactionType.REVENUE, credit.type());
    }
}
