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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.example.billproject.domain.value.Address;
import org.example.billproject.domain.value.TaxInfo;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMER")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMER_SEQ")
    @SequenceGenerator(name = "CUSTOMER_SEQ", sequenceName = "CUSTOMER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, length = 20, unique = true)
    private String code;

    @Column(name = "NAME", nullable = false, length = 120)
    private String name;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @Column(name = "PHONE", length = 32)
    private String phone;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "ADDR_LINE1", length = 120)),
            @AttributeOverride(name = "line2", column = @Column(name = "ADDR_LINE2", length = 120)),
            @AttributeOverride(name = "city", column = @Column(name = "ADDR_CITY", length = 80)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "ADDR_POST_CODE", length = 16)),
            @AttributeOverride(name = "countryCode", column = @Column(name = "ADDR_COUNTRY", length = 2))
    })
    private Address address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "taxNumber", column = @Column(name = "TAX_NUMBER", length = 32)),
            @AttributeOverride(name = "vatNumber", column = @Column(name = "VAT_NUMBER", length = 32))
    })
    private TaxInfo taxInfo;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Bill> bills = new ArrayList<>();

    protected Customer() {
    }

    public Customer(String code, String name) {
        this.code = code;
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    public TaxInfo getTaxInfo() {
        return taxInfo;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void updateContact(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public void changeAddress(Address address) {
        this.address = address;
    }

    public void changeTaxInfo(TaxInfo taxInfo) {
        this.taxInfo = taxInfo;
    }
}