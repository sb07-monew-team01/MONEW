package com.codeit.monew.domain.interest.policy;

import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InterestNamePolicy {
    void apply(String name, List<Interest> list);
}
