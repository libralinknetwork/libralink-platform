package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link Account} entity
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("from Account b WHERE b.walletId = ?1")
    List<Account> findByWallet(String walletId);

    @Query("from Account a WHERE a.walletId = ?1 and a.id = ?2")
    Optional<Account> findByAccountId(String walletId, String accountId);
}
