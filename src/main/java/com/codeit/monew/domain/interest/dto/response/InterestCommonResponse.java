package com.codeit.monew.domain.interest.dto.response;

import java.util.List;
import java.util.UUID;

public record InterestCommonResponse(
    UUID interestId,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribeByMe
){

}
