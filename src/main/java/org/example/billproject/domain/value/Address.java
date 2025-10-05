package org.example.billproject.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class Address {

    @Column(name = "ADDR_LINE1", length = 120)
    private String line1;

    @Column(name = "ADDR_LINE2", length = 120)
    private String line2;

    @Column(name = "ADDR_CITY", length = 80)
    private String city;

    @Column(name = "ADDR_POST_CODE", length = 16)
    private String postalCode;

    @Column(name = "ADDR_COUNTRY", length = 2)
    private String countryCode;

    protected Address() {
    }

    public Address(String line1, String line2, String city, String postalCode, String countryCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(line1, address.line1) && Objects.equals(line2, address.line2) && Objects.equals(city, address.city) && Objects.equals(postalCode, address.postalCode) && Objects.equals(countryCode, address.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, city, postalCode, countryCode);
    }
}