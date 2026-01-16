package com.codeit.monew.domain.interest.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
import com.codeit.monew.domain.interest.exception.KeywordValidException;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.global.enums.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "interests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest extends BaseUpdatableEntity {

    //Fields
    @Column(nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "interest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InterestKeyword> keywords;

    //Constructors
    public Interest(String name, List<String> keywords) {
        if(keywords == null){
            throw new KeywordValidException(ErrorCode.INTEREST_NULL_KEYWORD);
        }

        this.name = name;
        this.keywords = new ArrayList<>();
        keywords.forEach(this::addKeyword);
        checkKeyword(keywords);
    }

    //Methods
    public Interest update(List<String> keywords) {
        super.touch();
        this.keywords.clear();
        keywords.forEach(this::addKeyword);
        return this;
    }

    private void addKeyword(String keyword) {
        this.keywords.add(new InterestKeyword(this, keyword));
    }

    //키워드 검증 메소드 분리
    private void checkKeyword(List<String> keywords) {
        if(keywords.isEmpty()){
            throw new KeywordValidException(ErrorCode.INTEREST_EMPTY_KEYWORD);
        }
        if(keywords.size() > 10){
            throw new KeywordValidException(ErrorCode.TOO_MANY_KEYWORD);
        }
        checkDuplicateKeyword(keywords);
    }

    //같은 관심사 내에 중복 키워드가 있는지 체크하고, 있으면 예외를 발생시키는 메소드
    private void checkDuplicateKeyword(List<String> keywords) {
        if(keywords.size() != keywords.stream().distinct().count()){
            throw new KeywordValidException(
                    ErrorCode.INTEREST_KEYWORD_DUPLICATE
            );
        }
    }
}
