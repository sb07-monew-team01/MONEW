package com.codeit.monew.domain.interest.service;

import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public interface InterestService {
    Interest create(String name, List<String> keywords);
}
