package org.example.billproject.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.example.billproject.domain.value.Money;
import org.junit.jupiter.api.Test;

class BillLineTest {

    @Test
    void shouldCalculateAmountsWhenConstructed() {
        Money unitPrice = new Money("USD", new BigDecimal("10.00"));
        Money tax = new Money("USD", new BigDecimal("2.00"));

        BillLine line = new BillLine(1, new BigDecimal("3"), unitPrice, tax);

        assertThat(line.getNetAmount().getAmount()).isEqualByComparingTo("30.00");
        assertThat(line.getLineTotal().getAmount()).isEqualByComparingTo("32.00");
        assertThat(line.getTaxAmount().getAmount()).isEqualByComparingTo("2.00");
    }

    @Test
    void useProductShouldFillDetailsFromProduct() {
        BillLine line = new BillLine(1, new BigDecimal("1"), new Money("USD", new BigDecimal("0")), null);
        Product product = new Product("PRD-1", "Cloud Backup", new Money("USD", new BigDecimal("19.99")), new BigDecimal("21"));

        line.useProduct(product, null);

        assertThat(line.getDescription()).isEqualTo("Cloud Backup");
        assertThat(line.getUnitPrice()).isNotNull();
        assertThat(line.getUnitPrice().getAmount()).isEqualByComparingTo("19.99");
        assertThat(line.getNetAmount().getAmount()).isEqualByComparingTo("19.99");
    }
}