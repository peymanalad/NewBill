package org.example.billproject.repository;

import org.example.billproject.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.bill.billNumber = :billNumber order by p.paymentDate desc")
    List<Payment> findByBillNumber(@Param("billNumber") String billNumber);

    @Query("select p from Payment p where p.paymentDate between :start and :end")
    List<Payment> findWithinPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);
}