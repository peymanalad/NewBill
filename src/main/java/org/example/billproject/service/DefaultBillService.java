package org.example.billproject.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.example.billproject.domain.exception.BillNotFoundException;
import org.example.billproject.domain.model.Bill;
import org.example.billproject.domain.model.BillStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DefaultBillService implements BillService {

    private final Map<UUID, Bill> store = new ConcurrentHashMap<>();

    @Override
    public Bill create(Bill bill) {
        UUID id = Optional.ofNullable(bill.getId()).orElse(UUID.randomUUID());
        bill.setId(id);
        bill.setStatus(Optional.ofNullable(bill.getStatus()).orElse(BillStatus.ISSUED));
        OffsetDateTime now = OffsetDateTime.now();
        bill.setIssuedAt(Optional.ofNullable(bill.getIssuedAt()).orElse(now));
        bill.setUpdatedAt(now);
        store.put(id, cloneBill(bill));
        return cloneBill(bill);
    }

    @Override
    public Bill update(UUID id, Bill bill) {
        Bill existing = getRequired(id);
        bill.setId(id);
        bill.setStatus(Optional.ofNullable(bill.getStatus()).orElse(existing.getStatus()));
        bill.setIssuedAt(Optional.ofNullable(bill.getIssuedAt()).orElse(existing.getIssuedAt()));
        bill.setUpdatedAt(nextUpdateTimestamp(existing.getUpdatedAt()));
        bill.setLastPaymentReference(existing.getLastPaymentReference());
        store.put(id, cloneBill(bill));
        return cloneBill(bill);
    }

    @Override
    public Bill updateStatus(UUID id, BillStatus status) {
        Bill existing = getRequired(id);
        existing.setStatus(status);
        existing.setUpdatedAt(nextUpdateTimestamp(existing.getUpdatedAt()));
        store.put(id, cloneBill(existing));
        return cloneBill(existing);
    }

    @Override
    public Bill registerPayment(UUID id, String paymentReference) {
        Bill existing = getRequired(id);
        existing.setStatus(BillStatus.PAID);
        existing.setUpdatedAt(nextUpdateTimestamp(existing.getUpdatedAt()));
        existing.setLastPaymentReference(paymentReference);
        store.put(id, cloneBill(existing));
        return cloneBill(existing);
    }

    @Override
    public Bill findById(UUID id) {
        return cloneBill(getRequired(id));
    }

    @Override
    public List<Bill> findAll(BillStatus status) {
        return store.values().stream()
                .filter(bill -> Objects.isNull(status) || bill.getStatus() == status)
                .sorted(Comparator.comparing(Bill::getIssuedAt).reversed())
                .map(this::cloneBill)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (store.remove(id) == null) {
            throw new BillNotFoundException(id);
        }
    }

    private Bill getRequired(UUID id) {
        Bill bill = store.get(id);
        if (bill == null) {
            throw new BillNotFoundException(id);
        }
        return cloneBill(bill);
    }

    private Bill cloneBill(Bill bill) {
        Bill copy = new Bill(
                bill.getId(),
                bill.getCustomerId(),
                bill.getCustomerName(),
                bill.getDescription(),
                bill.getAmount(),
                bill.getDueDate(),
                bill.getStatus(),
                bill.getIssuedAt(),
                bill.getUpdatedAt(),
                bill.getLastPaymentReference());
        copy.setNetAmount(bill.getNetAmount());
        return copy;
    }
    private OffsetDateTime nextUpdateTimestamp(OffsetDateTime previous) {
        OffsetDateTime now = OffsetDateTime.now();
        if (previous == null) {
            return now;
        }
        if (!now.isAfter(previous)) {
            return previous.plusNanos(1);
        }
        return now;
    }
}