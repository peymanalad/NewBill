package org.example.billproject.service;

import java.util.List;
import java.util.UUID;

import org.example.billproject.domain.model.Bill;
import org.example.billproject.domain.model.BillStatus;

public interface BillService {

    Bill create(Bill bill);

    Bill update(UUID id, Bill bill);

    Bill updateStatus(UUID id, BillStatus status);

    Bill registerPayment(UUID id, String paymentReference);

    Bill findById(UUID id);

    List<Bill> findAll(BillStatus status);

    void delete(UUID id);
}