package org.example.billproject.repository;

import org.example.billproject.domain.model.BillLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillLineRepository extends JpaRepository<BillLine, Long> {
    List<BillLine> findByBillIdOrderByLineNumber(Long billId);
}