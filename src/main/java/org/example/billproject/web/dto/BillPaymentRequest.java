package org.example.billproject.web.dto;

import jakarta.validation.constraints.NotBlank;

public class BillPaymentRequest {

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}