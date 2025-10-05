package org.example.billproject.repository;

import org.example.billproject.domain.model.Bill;
import org.example.billproject.repository.projection.BillSummary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @EntityGraph(attributePaths = {"customer", "lines", "payments"})
    Optional<Bill> findByBillNumber(String billNumber);

    @Query("select new org.example.billproject.repository.projection.BillSummary(b.billNumber, b.status, b.totalAmount.amount, b.paidAmount.amount) " +
            "from Bill b where b.customer.code = :customerCode order by b.issueDate desc")
    List<BillSummary> findSummaryByCustomer(@Param("customerCode") String customerCode);

    @Query("select b from Bill b join fetch b.customer where b.issueDate between :start and :end")
    List<Bill> findIssuedBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}