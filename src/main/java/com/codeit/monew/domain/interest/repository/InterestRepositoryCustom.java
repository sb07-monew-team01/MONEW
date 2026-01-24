package com.codeit.monew.domain.interest.repository;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.data.domain.Slice;

public interface InterestRepositoryCustom {
    Slice<Interest> findAllByCursor(InterestCursorQuery query);
}
