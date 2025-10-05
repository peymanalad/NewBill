package org.example.billproject.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.example.billproject.domain.value.Money;

import java.time.LocalDate;

@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_SEQ")
    @SequenceGenerator(name = "PAYMENT_SEQ", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_ID")
    private Bill bill;

    @Column(name = "PAYMENT_DATE", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "METHOD", length = 1, nullable = false)
    private PaymentMethod method;

    @Column(name = "REFERENCE", length = 60)
    private String reference;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "PAY_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "PAY_AMOUNT", precision = 18, scale = 2))
    })
    private Money amount;

    protected Payment() {
    }

    public Payment(LocalDate paymentDate, PaymentMethod method, Money amount) {
        this.paymentDate = paymentDate;
        this.method = method;
        this.amount = amount;
    }

    void attachToBill(Bill bill) {
        this.bill = bill;
    }

    public Long getId() {
        return id;
    }

    public Bill getBill() {
        return bill;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getReference() {
        return reference;
    }

    public Money getAmount() {
        return amount;
    }

    public void reference(String reference) {
        this.reference = reference;
    }
}