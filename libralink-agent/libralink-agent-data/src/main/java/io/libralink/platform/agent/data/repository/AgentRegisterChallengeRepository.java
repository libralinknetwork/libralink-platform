package io.libralink.platform.agent.data.repository;

import io.libralink.platform.agent.data.entity.AgentRegisterConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Tag JPA Repository to manage {@link AgentRegisterConfirm} entity
 */
@Repository
public interface AgentRegisterChallengeRepository extends JpaRepository<AgentRegisterConfirm, String> {

}
