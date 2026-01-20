package com.codeit.monew.domain.interestuser.service;

import com.codeit.monew.domain.interest.dto.response.InterestSubScriptionResponse;
import com.codeit.monew.domain.interestuser.entity.InterestUser;

import java.util.UUID;

public interface InterestUserService {
    InterestSubScriptionResponse subscribe(UUID userId, UUID interestId);
    void unSubscribe(UUID userId, UUID interestId);
}
