package org.example.billproject.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {

    @Test
    void shouldResolveFromOracleCode() {
        assertThat(PaymentMethod.fromOracleCode("C")).isEqualTo(PaymentMethod.CASH);
        assertThat(PaymentMethod.fromOracleCode("D")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromOracleCode("B")).isEqualTo(PaymentMethod.BANK_TRANSFER);
        assertThat(PaymentMethod.fromOracleCode("Q")).isEqualTo(PaymentMethod.CHEQUE);
    }

    @Test
    void shouldFailForUnknownCode() {
        assertThatThrownBy(() -> PaymentMethod.fromOracleCode("X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown payment method code: X");
    }
}