package org.example.billproject.repository;

import org.example.billproject.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCode(String code);

    @Query("select c from Customer c left join fetch c.bills where c.code = :code")
    Optional<Customer> findDetailedByCode(@Param("code") String code);
}