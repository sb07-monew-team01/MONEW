package com.codeit.monew.domain.interest.mapper;

import com.codeit.monew.domain.interest.dto.response.InterestSubScriptionResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InterestSubScriptionMapper {
    public InterestSubScriptionResponse toDto(Interest interest, UUID interestUserId) {
        return new InterestSubScriptionResponse(
           interestUserId,
           interest.getId(),
            interest.getName(),
            interest.getKeywords().stream().map(InterestKeyword::getKeyword).toList(),
            interest.getSubscriberCount(),
            interest.getCreatedAt()
        );
    }
}
