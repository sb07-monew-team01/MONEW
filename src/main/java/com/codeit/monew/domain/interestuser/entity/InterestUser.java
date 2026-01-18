package com.codeit.monew.domain.interestuser.entity;

import com.codeit.monew.domain.BaseEntity;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "interest_users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_interest_users_interest_users",
                columnNames = {"interest_id", "user_Id"}
        ))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InterestUser extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
