package org.example.billproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.example.billproject.domain.exception.BillNotFoundException;
import org.example.billproject.domain.model.Bill;
import org.example.billproject.domain.model.BillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultBillServiceTest {

    private DefaultBillService service;

    @BeforeEach
    void setUp() {
        service = new DefaultBillService();
    }

    @Test
    void createShouldPopulateIdentifierAndTimestamps() {
        Bill request = buildBill();
        request.setId(null);
        request.setStatus(null);
        request.setIssuedAt(null);
        request.setUpdatedAt(null);

        Bill created = service.create(request);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BillStatus.ISSUED);
        assertThat(created.getIssuedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
        assertThat(created.getLastPaymentReference()).isNull();
        assertThat(created).isNotSameAs(request);
    }

    @Test
    void updateShouldPreserveIssuedAtAndStatusWhenAbsent() {
        Bill created = service.create(buildBill());
        UUID id = created.getId();
        OffsetDateTime originalIssuedAt = created.getIssuedAt();

        Bill update = buildBill();
        update.setStatus(null);
        update.setIssuedAt(null);

        Bill updated = service.update(id, update);

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getIssuedAt()).isEqualTo(originalIssuedAt);
        assertThat(updated.getStatus()).isEqualTo(created.getStatus());
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(created.getUpdatedAt());
    }

    @Test
    void updateShouldFailWhenBillMissing() {
        Bill update = buildBill();
        assertThatThrownBy(() -> service.update(UUID.randomUUID(), update))
                .isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void updateStatusShouldChangeStatusAndTimestamp() {
        Bill created = service.create(buildBill());
        UUID id = created.getId();

        Bill updated = service.updateStatus(id, BillStatus.CANCELLED);

        assertThat(updated.getStatus()).isEqualTo(BillStatus.CANCELLED);
        assertThat(updated.getUpdatedAt()).isAfter(created.getUpdatedAt());
    }

    @Test
    void registerPaymentShouldMarkBillAsPaid() {
        Bill created = service.create(buildBill());
        UUID id = created.getId();

        Bill updated = service.registerPayment(id, "PAY-123");

        assertThat(updated.getStatus()).isEqualTo(BillStatus.PAID);
        assertThat(updated.getLastPaymentReference()).isEqualTo("PAY-123");
    }

    @Test
    void findByIdShouldReturnCopy() {
        Bill created = service.create(buildBill());

        Bill fetched = service.findById(created.getId());

        assertThat(fetched).usingRecursiveComparison().isEqualTo(created);
        assertThat(fetched).isNotSameAs(created);
    }

    @Test
    void findAllShouldFilterByStatus() {
        Bill draft = buildBill();
        draft.setStatus(BillStatus.DRAFT);
        service.create(draft);
        Bill issued = buildBill();
        issued.setStatus(BillStatus.ISSUED);
        service.create(issued);

        List<Bill> all = service.findAll(null);
        List<Bill> drafts = service.findAll(BillStatus.DRAFT);

        assertThat(all).hasSize(2);
        assertThat(drafts).hasSize(1);
        assertThat(drafts.get(0).getStatus()).isEqualTo(BillStatus.DRAFT);
    }

    @Test
    void deleteShouldRemoveBill() {
        Bill created = service.create(buildBill());
        UUID id = created.getId();

        service.delete(id);

        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(BillNotFoundException.class);
    }

    @Test
    void deleteShouldFailWhenBillMissing() {
        assertThatThrownBy(() -> service.delete(UUID.randomUUID()))
                .isInstanceOf(BillNotFoundException.class);
    }

    private Bill buildBill() {
        Bill bill = new Bill();
        bill.setId(UUID.randomUUID());
        bill.setCustomerId("CUST-1");
        bill.setCustomerName("Acme Corp");
        bill.setDescription("Quarterly subscription");
        bill.setAmount(new BigDecimal("199.99"));
        bill.setDueDate(LocalDate.now().plusDays(30));
        bill.setStatus(BillStatus.ISSUED);
        bill.setIssuedAt(OffsetDateTime.now().minusDays(1));
        bill.setUpdatedAt(OffsetDateTime.now().minusDays(1));
        return bill;
    }
}