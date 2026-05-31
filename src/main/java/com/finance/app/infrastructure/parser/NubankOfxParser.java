package com.finance.app.infrastructure.parser;

import com.finance.app.domain.entity.ParsedTransaction;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.service.StatementParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class NubankOfxParser implements StatementParser {

    private static final Pattern STMTTRN_PATTERN = Pattern.compile("<STMTTRN>(.*?)</STMTTRN>", Pattern.DOTALL);
    private static final Pattern TRNTYPE_PATTERN = Pattern.compile("<TRNTYPE>(.*?)</", Pattern.DOTALL);
    private static final Pattern DTPOSTED_PATTERN = Pattern.compile("<DTPOSTED>(.*?)</", Pattern.DOTALL);
    private static final Pattern TRNAMT_PATTERN = Pattern.compile("<TRNAMT>(.*?)</", Pattern.DOTALL);
    private static final Pattern FITID_PATTERN = Pattern.compile("<FITID>(.*?)</", Pattern.DOTALL);
    private static final Pattern MEMO_PATTERN = Pattern.compile("<MEMO>(.*?)</", Pattern.DOTALL);

    // Format: YYYYMMDDHHMMSS[-3:BRT]
    // We can extract just the YYYYMMDDHHMMSS part
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public List<ParsedTransaction> parse(InputStream inputStream) {
        List<ParsedTransaction> transactions = new ArrayList<>();

        try {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            
            // NuBank OFX usually doesn't have closing tags for everything, but let's assume STMTTRN is wrapped 
            // or we can find STMTTRN blocks. Actually OFX 1.02 might not have </STMTTRN>. Let's check.
            // If the sample had </STMTTRN> we can use the pattern above.
            // Otherwise, we split by <STMTTRN>
            
            String[] blocks = content.split("<STMTTRN>");
            for (int i = 1; i < blocks.length; i++) { // Skip the first block before the first <STMTTRN>
                String block = blocks[i];
                // block might contain </STMTTRN> or just end at the next tag.
                
                String fitid = extract(FITID_PATTERN, block);
                String amountStr = extract(TRNAMT_PATTERN, block);
                String dtpostedStr = extract(DTPOSTED_PATTERN, block);
                String memo = extract(MEMO_PATTERN, block);
                
                if (fitid == null || amountStr == null || dtpostedStr == null) {
                    continue; // invalid block
                }

                BigDecimal amount = new BigDecimal(amountStr);
                TransactionType type = amount.compareTo(BigDecimal.ZERO) < 0 ? TransactionType.EXPENSE : TransactionType.REVENUE;
                BigDecimal finalAmount = amount.abs();
                
                // Parse date, taking only the first 14 characters (YYYYMMDDHHMMSS)
                String datePart = dtpostedStr.substring(0, 14);
                LocalDateTime dateTime = LocalDateTime.parse(datePart, DATE_FORMATTER);
                
                transactions.add(new ParsedTransaction(fitid, memo != null ? memo : "", finalAmount, dateTime, type));
            }

        } catch (Exception e) {
            log.error("Error parsing Nubank OFX file", e);
            throw new RuntimeException("Failed to parse OFX file", e);
        }

        return transactions;
    }
    
    private String extract(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            // Remove any trailing XML tags or whitespace just in case
            return matcher.group(1).split("<")[0].trim();
        }
        // Fallback for tags without closing tag (common in OFX SGML)
        // e.g. <FITID>12345\n
        String tagName = pattern.pattern().substring(0, pattern.pattern().indexOf(">") + 1);
        int idx = text.indexOf(tagName);
        if (idx != -1) {
            int start = idx + tagName.length();
            int end = text.indexOf("<", start);
            if (end == -1) {
                end = text.indexOf("\n", start);
            }
            if (end != -1) {
                return text.substring(start, end).trim();
            }
        }
        return null;
    }

    @Override
    public boolean supports(String fileName, String contentType) {
        return fileName != null && fileName.toLowerCase().endsWith(".ofx");
    }
}
