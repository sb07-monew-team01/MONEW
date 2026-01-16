package com.codeit.monew.domain.interestkeyword;

import com.codeit.monew.domain.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "interest_keywords",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_interest_keywords_interest_keyword",
                columnNames = {"interest_id", "keyword"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestKeyword{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name="keyword", nullable = false, length = 255)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="interest_id", nullable = false)
    private Interest interest;

    public InterestKeyword(Interest interest, String keyword) {
        this.interest = interest;
        this.keyword = keyword;
    }
}
