package com.codeit.monew.domain.interestuser.repository;

import com.codeit.monew.domain.interestuser.entity.InterestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterestUserRepository extends JpaRepository<InterestUser, UUID> {
    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
    Optional<InterestUser> findByUserIdAndInterestId(UUID userId, UUID interestId);

    @Query("SELECT iu FROM InterestUser iu WHERE iu.interest.id IN :interestIds")
    List<InterestUser> findAllByInterestIdIn(@Param("interestIds") Collection<UUID> interestIds);
}

