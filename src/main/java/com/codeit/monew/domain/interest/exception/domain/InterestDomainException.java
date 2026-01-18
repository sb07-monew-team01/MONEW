package com.codeit.monew.domain.interest.exception.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterestDomainException extends RuntimeException{
    private final InterestErrorCode interestErrorCode;
}
