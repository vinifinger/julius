package com.finance.app.infrastructure.parser;

import com.finance.app.domain.entity.ParsedTransaction;
import com.finance.app.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NubankOfxParserTest {

    private final NubankOfxParser parser = new NubankOfxParser();

    @Test
    void shouldParseNubankOfxCorrectly() {
        String ofxContent = "OFXHEADER:100\n" +
                "<OFX>\n" +
                "<STMTTRN>\n" +
                "<TRNTYPE>DEBIT</TRNTYPE>\n" +
                "<DTPOSTED>20260504000000[-3:BRT]</DTPOSTED>\n" +
                "<TRNAMT>-560.01</TRNAMT>\n" +
                "<FITID>69f89fff-88dd-4ac2-8c52-f9b9a282061e</FITID>\n" +
                "<MEMO>Pagamento de fatura</MEMO>\n" +
                "</STMTTRN>\n" +
                "<STMTTRN>\n" +
                "<TRNTYPE>CREDIT</TRNTYPE>\n" +
                "<DTPOSTED>20260505120000[-3:BRT]</DTPOSTED>\n" +
                "<TRNAMT>150.00</TRNAMT>\n" +
                "<FITID>abc-123</FITID>\n" +
                "<MEMO>Transferencia</MEMO>\n" +
                "</STMTTRN>\n" +
                "</OFX>";

        List<ParsedTransaction> transactions = parser.parse(new ByteArrayInputStream(ofxContent.getBytes(StandardCharsets.UTF_8)));

        assertEquals(2, transactions.size());

        ParsedTransaction t1 = transactions.get(0);
        assertEquals("69f89fff-88dd-4ac2-8c52-f9b9a282061e", t1.externalId());
        assertEquals("Pagamento de fatura", t1.description());
        assertEquals(new BigDecimal("560.01"), t1.amount());
        assertEquals(TransactionType.EXPENSE, t1.type());
        assertEquals(2026, t1.dateTime().getYear());
        assertEquals(5, t1.dateTime().getMonthValue());
        assertEquals(4, t1.dateTime().getDayOfMonth());

        ParsedTransaction t2 = transactions.get(1);
        assertEquals("abc-123", t2.externalId());
        assertEquals("Transferencia", t2.description());
        assertEquals(new BigDecimal("150.00"), t2.amount());
        assertEquals(TransactionType.REVENUE, t2.type());
    }
}
