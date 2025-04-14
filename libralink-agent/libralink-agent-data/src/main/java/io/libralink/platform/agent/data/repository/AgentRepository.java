package io.libralink.platform.agent.data.repository;

import io.libralink.platform.agent.data.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link Agent} entity
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {

    @Query("from Agent a WHERE a.address = ?1")
    Optional<Agent> findByAddress(String address);
}
