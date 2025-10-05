package org.example.billproject.web.dto;

import jakarta.validation.constraints.NotNull;

import org.example.billproject.domain.model.BillStatus;

public class BillStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BillStatus status;

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }
}