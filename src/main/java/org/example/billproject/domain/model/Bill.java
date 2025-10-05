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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BILL")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BILL_SEQ")
    @SequenceGenerator(name = "BILL_SEQ", sequenceName = "BILL_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BILL_NO", nullable = false, length = 30, unique = true)
    private String billNumber;

    @Column(name = "ISSUE_DATE", nullable = false)
    private LocalDate issueDate;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "DESCRIPTION", length = 240)
    private String description;

    @Column(name = "STATUS", length = 1, nullable = false)
    private BillStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "NET_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "NET_AMOUNT", precision = 18, scale = 2))
    })
    private Money netAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "TAX_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "TAX_AMOUNT", precision = 18, scale = 2))
    })
    private Money taxAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "TOTAL_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2))
    })
    private Money totalAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "PAID_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "PAID_AMOUNT", precision = 18, scale = 2))
    })
    private Money paidAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillLine> lines = new ArrayList<>();

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Version
    @Column(name = "VERSION")
    private Long version;

    protected Bill() {
    }

    public Bill(String billNumber, LocalDate issueDate, Customer customer) {
        this.billNumber = billNumber;
        this.issueDate = issueDate;
        this.customer = customer;
        this.status = BillStatus.DRAFT;
    }

    public Long getId() {
        return id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public BillStatus getStatus() {
        return status;
    }

    public Money getNetAmount() {
        return netAmount;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Money getPaidAmount() {
        return paidAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<BillLine> getLines() {
        return lines;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void changeStatus(BillStatus status) {
        this.status = status;
    }

    public void schedule(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void describe(String description) {
        this.description = description;
    }

    public void recalculateTotals() {
        if (lines.isEmpty()) {
            this.netAmount = null;
            this.taxAmount = null;
            this.totalAmount = null;
            return;
        }

        String currency = null;
        java.math.BigDecimal net = java.math.BigDecimal.ZERO;
        java.math.BigDecimal tax = java.math.BigDecimal.ZERO;
        for (BillLine line : lines) {
            if (currency == null) {
                if (line.getLineTotal() != null) {
                    currency = line.getLineTotal().getCurrency();
                } else if (line.getNetAmount() != null) {
                    currency = line.getNetAmount().getCurrency();
                }
            }
            if (line.getNetAmount() != null) {
                net = net.add(line.getNetAmount().getAmount());
            }
            if (line.getTaxAmount() != null) {
                tax = tax.add(line.getTaxAmount().getAmount());
            }
        }
        if (currency == null) {
            this.netAmount = null;
            this.taxAmount = null;
            this.totalAmount = null;
            return;
        }
        this.netAmount = new Money(currency, net);
        this.taxAmount = new Money(currency, tax);
        this.totalAmount = new Money(currency, net.add(tax));
    }

    public void addLine(BillLine line) {
        line.attachToBill(this);
        this.lines.add(line);
        recalculateTotals();
    }

    public void addPayment(Payment payment) {
        payment.attachToBill(this);
        this.payments.add(payment);
        recalculatePaidAmount();
    }

    private void recalculatePaidAmount() {
        if (payments.isEmpty()) {
            this.paidAmount = null;
            return;
        }
        String currency = null;
        java.math.BigDecimal totalPaid = java.math.BigDecimal.ZERO;
        for (Payment payment : payments) {
            if (currency == null && payment.getAmount() != null) {
                currency = payment.getAmount().getCurrency();
            }
            if (payment.getAmount() != null) {
                totalPaid = totalPaid.add(payment.getAmount().getAmount());
            }
        }
        if (currency == null) {
            this.paidAmount = null;
            return;
        }
        this.paidAmount = new Money(currency, totalPaid);
    }
}