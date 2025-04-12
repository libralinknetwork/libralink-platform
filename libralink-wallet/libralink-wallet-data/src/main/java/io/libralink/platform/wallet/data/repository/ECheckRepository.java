package io.libralink.platform.wallet.data.repository;

import io.libralink.platform.wallet.data.entity.ECheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link ECheck} entity
 */
@Repository
public interface ECheckRepository extends JpaRepository<ECheck, String> {

    @Query("from ECheck c WHERE c.envelopeId = ?1")
    Optional<ECheck> findByEnvelopeId(String envelopeId);
}
