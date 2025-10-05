package org.example.billproject.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.example.billproject.domain.value.Money;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_SEQ")
    @SequenceGenerator(name = "PRODUCT_SEQ", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "NAME", nullable = false, length = 120)
    private String name;

    @Column(name = "UNIT", length = 16)
    private String unitOfMeasure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "LIST_CURRENCY", length = 3)),
            @AttributeOverride(name = "amount", column = @Column(name = "LIST_PRICE", precision = 18, scale = 2))
    })
    private Money listPrice;

    @Column(name = "TAX_RATE", precision = 5, scale = 2)
    private BigDecimal taxRate;

    protected Product() {
    }

    public Product(String code, String name, Money listPrice, BigDecimal taxRate) {
        this.code = code;
        this.name = name;
        this.listPrice = listPrice;
        this.taxRate = taxRate;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Money getListPrice() {
        return listPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void updateCatalogInfo(String name, String unitOfMeasure, Money listPrice, BigDecimal taxRate) {
        this.name = name;
        this.unitOfMeasure = unitOfMeasure;
        this.listPrice = listPrice;
        this.taxRate = taxRate;
    }
}