package org.example.billproject.domain.model;

public enum BillStatus {
    DRAFT("D"),
    ISSUED("I"),
    PARTIALLY_PAID("P"),
    PAID("F"),
    CANCELLED("C");

    private final String oracleCode;

    BillStatus(String oracleCode) {
        this.oracleCode = oracleCode;
    }

    public String getOracleCode() {
        return oracleCode;
    }

    public static BillStatus fromOracleCode(String code) {
        for (BillStatus status : values()) {
            if (status.oracleCode.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown bill status code: " + code);
    }
}