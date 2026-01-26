package com.codeit.monew.domain.userActivity.event.dto;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestuser.entity.InterestUser;

import java.util.UUID;

public record InterestSubscribedEvent(
        UUID userId,
        Interest interest,
        InterestUser interestUser
) {
}
