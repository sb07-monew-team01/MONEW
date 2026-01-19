package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface InterestService {
    Interest create(String name, List<String> keywords);
    Interest editKeywords(UUID id, List<String> keywords);
    void delete(UUID id);
    Interest findById(UUID id);
    Slice<Interest> getInterests(
            String keyword,
            String orderBy,
            String direction,
            String cursor,
            String after,
            Integer limit
    );
}
