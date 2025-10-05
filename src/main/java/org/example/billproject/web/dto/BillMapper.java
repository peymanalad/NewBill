package org.example.billproject.web.dto;

import org.example.billproject.domain.model.Bill;

public final class BillMapper {

    private BillMapper() {
    }

    public static Bill toDomain(BillRequest request) {
        Bill bill = new Bill();
        bill.setCustomerId(request.getCustomerId());
        bill.setCustomerName(request.getCustomerName());
        bill.setDescription(request.getDescription());
        bill.setAmount(request.getAmount());
        bill.setDueDate(request.getDueDate());
        bill.setStatus(request.getStatus());
        return bill;
    }

    public static void copyToDomain(BillRequest request, Bill bill) {
        bill.setCustomerId(request.getCustomerId());
        bill.setCustomerName(request.getCustomerName());
        bill.setDescription(request.getDescription());
        bill.setAmount(request.getAmount());
        bill.setDueDate(request.getDueDate());
        bill.setStatus(request.getStatus());
    }

    public static BillResponse toResponse(Bill bill) {
        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setCustomerId(bill.getCustomerId());
        response.setCustomerName(bill.getCustomerName());
        response.setDescription(bill.getDescription());
        response.setAmount(bill.getAmount());
        response.setDueDate(bill.getDueDate());
        response.setStatus(bill.getStatus());
        response.setIssuedAt(bill.getIssuedAt());
        response.setUpdatedAt(bill.getUpdatedAt());
        response.setLastPaymentReference(bill.getLastPaymentReference());
        return response;
    }
}