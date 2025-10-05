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

import java.math.BigDecimal;

@Entity
@Table(name = "BILL_LINE")
public class BillLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BILL_LINE_SEQ")
    @SequenceGenerator(name = "BILL_LINE_SEQ", sequenceName = "BILL_LINE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_ID")
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "LINE_NO", nullable = false)
    private Integer lineNumber;

    @Column(name = "DESCRIPTION", length = 240)
    private String description;

    @Column(name = "QUANTITY", precision = 12, scale = 3)
    private BigDecimal quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "UNIT_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "UNIT_PRICE", precision = 18, scale = 2))
    })
    private Money unitPrice;

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
            @AttributeOverride(name = "currency", column = @Column(name = "LINE_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "LINE_TOTAL", precision = 18, scale = 2))
    })
    private Money lineTotal;

    protected BillLine() {
    }

    public BillLine(Integer lineNumber, BigDecimal quantity, Money unitPrice, Money taxAmount) {
        this.lineNumber = lineNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxAmount = taxAmount;
        calculateAmounts();
    }

    private void calculateAmounts() {
        if (unitPrice == null || quantity == null) {
            return;
        }
        BigDecimal net = unitPrice.getAmount().multiply(quantity);
        this.netAmount = new Money(unitPrice.getCurrency(), net);
        BigDecimal tax = taxAmount != null ? taxAmount.getAmount() : BigDecimal.ZERO;
        this.lineTotal = new Money(unitPrice.getCurrency(), net.add(tax));
        if (taxAmount == null && tax.compareTo(BigDecimal.ZERO) == 0) {
            this.taxAmount = new Money(unitPrice.getCurrency(), BigDecimal.ZERO);
        }
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

    public Product getProduct() {
        return product;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getNetAmount() {
        return netAmount;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public Money getLineTotal() {
        return lineTotal;
    }

    public void useProduct(Product product, String description) {
        this.product = product;
        this.description = description != null ? description : product.getName();
        if (product != null) {
            this.unitPrice = product.getListPrice();
        }
        calculateAmounts();
    }
}