package org.example.billproject.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Embeddable
public class Money {

    @Column(name = "CURRENCY_CODE", length = 3)
    private String currency;

    @Column(name = "AMOUNT", precision = 18, scale = 2)
    private BigDecimal amount;

    protected Money() {
        // For JPA
    }

    public Money(String currency, BigDecimal amount) {
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = amount == null ? BigDecimal.ZERO : amount;
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(currency, amount.add(other.amount));
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(currency, amount.subtract(other.amount));
    }

    private void validateSameCurrency(Money other) {
        if (!Objects.equals(currency, other.currency)) {
            throw new IllegalArgumentException("Currencies must match");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Objects.equals(currency, money.currency) && Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }
}