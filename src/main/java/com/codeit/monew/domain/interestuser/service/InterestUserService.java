package com.codeit.monew.domain.interestuser.service;

import com.codeit.monew.domain.interest.dto.response.InterestSubscriptionResponse;

import java.util.UUID;

public interface InterestUserService {
    InterestSubscriptionResponse subscribe(UUID userId, UUID interestId);
    void unSubscribe(UUID userId, UUID interestId);
}
