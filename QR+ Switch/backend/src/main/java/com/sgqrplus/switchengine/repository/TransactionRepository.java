package com.sgqrplus.switchengine.repository;

import com.sgqrplus.switchengine.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
