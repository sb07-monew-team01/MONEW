package com.codeit.monew.domain.interestuser.repository;

import com.codeit.monew.domain.interestuser.entity.InterestUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterestUserRepository extends JpaRepository<InterestUser, UUID> {
}
