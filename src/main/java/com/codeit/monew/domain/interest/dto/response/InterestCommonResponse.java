package com.codeit.monew.domain.interest.dto.response;

import java.util.List;
import java.util.UUID;

public record InterestCommonResponse(
    UUID id,
    String name,
    List<String> keywords,
    long subscriberCount,
    Boolean subscribeByMe
){

}
