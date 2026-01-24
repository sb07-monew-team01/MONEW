package com.codeit.monew.domain.interest.mapper;

import com.codeit.monew.domain.interest.dto.response.InterestCommonResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import org.springframework.stereotype.Component;

@Component
public class InterestMapper {
    public InterestCommonResponse toDto(Interest interest, Boolean subscribedByMe) {
        return new InterestCommonResponse(
            interest.getId(),
            interest.getName(),
            interest.getKeywords().stream()
                    .map(InterestKeyword::getKeyword).toList(),
            interest.getSubscriberCount(),
            subscribedByMe
        );
    }
}
