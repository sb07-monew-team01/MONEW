package com.codeit.monew.domain.interest;

import java.util.List;

public class InterestServiceImpl implements InterestService{
    @Override
    public Interest create(String name, List<String> keywords) {
        return new Interest(name, keywords);
    }
}
