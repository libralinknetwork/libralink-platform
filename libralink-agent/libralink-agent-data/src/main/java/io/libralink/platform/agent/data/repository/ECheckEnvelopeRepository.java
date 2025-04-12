package io.libralink.platform.agent.data.repository;

import io.libralink.platform.agent.data.entity.ECheckEnvelope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Tag JPA Repository to manage {@link ECheckEnvelope} entity
 */
@Repository
public interface ECheckEnvelopeRepository extends JpaRepository<ECheckEnvelope, String> {

}
