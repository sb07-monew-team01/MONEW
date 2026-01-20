package com.codeit.monew.domain.interest.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InterestUpdateRequest (
    @NotEmpty
    List<String> keywords
){
}
