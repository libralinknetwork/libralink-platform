package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link Transaction} entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("from Transaction t WHERE t.reference = ?1")
    Optional<Transaction> findByReference(String reference);

    @Query("from Transaction t WHERE t.userId = ?1 and t.accountId = ?2")
    Page<Transaction> findByUserAccount(String userId, String accountId, Pageable pageable);
}
