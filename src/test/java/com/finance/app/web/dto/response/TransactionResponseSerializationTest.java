package com.finance.app.web.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class TransactionResponseSerializationTest {

    @Autowired
    private JacksonTester<TransactionResponse> json;

    @Test
    void shouldExcludeNullFieldsFromSerialization() throws Exception {
        TransactionResponse response = new TransactionResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null, // parentId
                null, // installmentCount
                null, // installmentNumber
                null, // externalId
                "Test description",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "EXPENSE",
                null, // subtype
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(this.json.write(response)).doesNotHaveJsonPath("$.parentId");
        assertThat(this.json.write(response)).doesNotHaveJsonPath("$.installmentCount");
        assertThat(this.json.write(response)).doesNotHaveJsonPath("$.installmentNumber");
    }

    @Test
    void shouldIncludeFieldsWhenNotNull() throws Exception {
        UUID parentId = UUID.randomUUID();
        TransactionResponse response = new TransactionResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                parentId,
                12,
                1, // installmentNumber
                null, // externalId
                "Installment Test",
                new BigDecimal("50.00"),
                LocalDateTime.now(),
                "EXPENSE",
                "FIXED", // subtype
                "PAID",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertThat(this.json.write(response)).hasJsonPathValue("$.parentId", parentId.toString());
        assertThat(this.json.write(response)).hasJsonPathValue("$.installmentCount", 12);
        assertThat(this.json.write(response)).hasJsonPathValue("$.installmentNumber", 1);
    }
}
