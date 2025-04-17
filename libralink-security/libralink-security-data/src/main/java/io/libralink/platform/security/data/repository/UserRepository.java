package io.libralink.platform.security.data.repository;

import io.libralink.platform.security.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tag JPA Repository to manage {@link User} entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("from User u WHERE u.platform = ?1 and u.platformId = ?2")
    Optional<User> findByPlatformId(String platform, String platformId);
}
