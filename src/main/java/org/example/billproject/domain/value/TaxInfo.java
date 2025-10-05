package org.example.billproject.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class TaxInfo {

    @Column(name = "TAX_NUMBER", length = 32)
    private String taxNumber;

    @Column(name = "VAT_NUMBER", length = 32)
    private String vatNumber;

    protected TaxInfo() {
    }

    public TaxInfo(String taxNumber, String vatNumber) {
        this.taxNumber = taxNumber;
        this.vatNumber = vatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxInfo taxInfo)) return false;
        return Objects.equals(taxNumber, taxInfo.taxNumber) && Objects.equals(vatNumber, taxInfo.vatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxNumber, vatNumber);
    }
}