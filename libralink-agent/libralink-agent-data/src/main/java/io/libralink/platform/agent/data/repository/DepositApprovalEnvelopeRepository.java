package io.libralink.platform.agent.data.repository;

import io.libralink.platform.agent.data.entity.DepositApprovalEnvelope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link DepositApprovalEnvelope} entity
 */
@Repository
public interface DepositApprovalEnvelopeRepository extends JpaRepository<DepositApprovalEnvelope, String> {

}
