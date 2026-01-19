package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.userActivity.dto.SubscriptionDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SubscriptionMapper {

    public SubscriptionDto toDto(InterestUser interestUser) {
        Interest interest = interestUser.getInterest();

        return new SubscriptionDto(
                interestUser.getId(),
                interest.getId(),
                interest.getName(),
                interest.getKeywords().stream()
                        .map(InterestKeyword::getKeyword)
                        .collect(Collectors.toList()),
                0, // interestSubscriberCount - 필요하면 별도로 조회
                interestUser.getCreatedAt()
        );
    }
}
