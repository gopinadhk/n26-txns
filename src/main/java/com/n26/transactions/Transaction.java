package com.n26.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Transaction {
    private final BigDecimal amount;
    private final ZonedDateTime timestamp;

    @JsonCreator
    public Transaction(@JsonProperty("amount") final BigDecimal amount,
                       @JsonProperty("timestamp") final ZonedDateTime timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
