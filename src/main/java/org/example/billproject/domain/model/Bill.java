package org.example.billproject.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.example.billproject.domain.value.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Bill {

    private UUID id;
    private String customerId;
    private String customerName;
    private String description;
    private BigDecimal amount;
    private LocalDate dueDate;
    private BillStatus status;
    private OffsetDateTime issuedAt;
    private OffsetDateTime updatedAt;
    private String lastPaymentReference;

    public Bill() {
    }

    public Bill(UUID id,
                String customerId,
                String customerName,
                String description,
                BigDecimal amount,
                LocalDate dueDate,
                BillStatus status,
                OffsetDateTime issuedAt,
                OffsetDateTime updatedAt,
                String lastPaymentReference) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.issuedAt = issuedAt;
        this.updatedAt = updatedAt;
        this.lastPaymentReference = lastPaymentReference;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "NET_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "NET_AMOUNT", precision = 18, scale = 2))
    })
    private Money netAmount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastPaymentReference() {
        return lastPaymentReference;
    }

    public void setLastPaymentReference(String lastPaymentReference) {
        this.lastPaymentReference = lastPaymentReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}