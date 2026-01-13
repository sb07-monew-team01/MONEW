package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;
import java.util.UUID;

public interface InterestService {
    Interest create(String name, List<String> keywords);
    Interest editKeywords(UUID id, List<String> keywords);
}
