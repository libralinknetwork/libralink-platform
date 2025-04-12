package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.DepositApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link DepositApproval} entity
 */
@Repository
public interface DepositApprovalRepository extends JpaRepository<DepositApproval, String> {

    @Query("from DepositApproval a WHERE a.envelopeId = ?1")
    Optional<DepositApproval> findByEnvelopeId(String envelopeId);
}
