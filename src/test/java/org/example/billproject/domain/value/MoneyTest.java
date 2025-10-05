package org.example.billproject.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void addShouldCombineAmountsWhenCurrencyMatches() {
        Money left = new Money("USD", new BigDecimal("10.00"));
        Money right = new Money("USD", new BigDecimal("5.50"));

        Money result = left.add(right);

        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getAmount()).isEqualByComparingTo("15.50");
    }

    @Test
    void subtractShouldDecreaseAmountWhenCurrencyMatches() {
        Money left = new Money("EUR", new BigDecimal("20.00"));
        Money right = new Money("EUR", new BigDecimal("7.25"));

        Money result = left.subtract(right);

        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getAmount()).isEqualByComparingTo("12.75");
    }

    @Test
    void operationsWithDifferentCurrenciesShouldFail() {
        Money left = new Money("USD", new BigDecimal("10.00"));
        Money right = new Money("EUR", new BigDecimal("5.00"));

        assertThatThrownBy(() -> left.add(right))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currencies must match");

        assertThatThrownBy(() -> left.subtract(right))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currencies must match");
    }
}