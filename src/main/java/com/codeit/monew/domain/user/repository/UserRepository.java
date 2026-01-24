package com.codeit.monew.domain.user.repository;

import com.codeit.monew.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("delete from User u where u.deletedAt < :threshold")
    long deleteAllByDeletedAtBefore(@Param("threshold") LocalDateTime threshold);
}
