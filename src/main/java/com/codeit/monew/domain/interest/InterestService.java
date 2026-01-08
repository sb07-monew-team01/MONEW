package com.codeit.monew.domain.interest;

import java.util.List;

public interface InterestService {
    Interest create(String name, List<String> keywords);
}
