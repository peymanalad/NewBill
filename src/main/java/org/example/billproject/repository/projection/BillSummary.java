package org.example.billproject.repository.projection;

import org.example.billproject.domain.model.BillStatus;

import java.math.BigDecimal;

public record BillSummary(
        String billNumber,
        BillStatus status,
        BigDecimal totalAmount,
        BigDecimal paidAmount
) {
}