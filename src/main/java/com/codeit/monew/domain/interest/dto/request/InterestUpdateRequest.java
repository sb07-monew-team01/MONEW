package com.codeit.monew.domain.interest.dto.request;

import java.util.List;
import java.util.UUID;

public record InterestUpdateRequest (
    UUID interestId,
    List<String> keywords
){
}
