package com.codeit.monew.domain.interest.repository;

import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findAll();
}
