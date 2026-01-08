package com.codeit.monew.domain.interest;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Interest {
    private final String name;
    private final List<String> keywords;

    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = new ArrayList<>(keywords);
    }
}
