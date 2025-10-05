package org.example.billproject.domain.model;

public enum PaymentMethod {
    CASH("C"),
    CARD("D"),
    BANK_TRANSFER("B"),
    CHEQUE("Q");

    private final String oracleCode;

    PaymentMethod(String oracleCode) {
        this.oracleCode = oracleCode;
    }

    public String getOracleCode() {
        return oracleCode;
    }

    public static PaymentMethod fromOracleCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.oracleCode.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method code: " + code);
    }
}