package com.codeit.monew.domain.interest.dto.request;

import java.util.List;

public record InterestUpdateRequest (
    List<String> keywords
){
}
