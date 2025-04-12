package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link Wallet} entity
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

    @Query("from Wallet w WHERE w.userId = ?1")
    Optional<Wallet> findByUser(String userId);
}
