package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link AccountTransaction} entity
 */
@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {

    @Query("from AccountTransaction t WHERE t.note = ?1")
    Optional<AccountTransaction> findByNote(String note);

    @Query("from AccountTransaction t WHERE t.sourceAccountId = ?1 or t.targetAccountId = ?1")
    Page<AccountTransaction> findByAccount(String accountId, Pageable pageable);
}
