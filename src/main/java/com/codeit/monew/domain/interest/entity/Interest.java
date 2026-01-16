package com.codeit.monew.domain.interest.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
import com.codeit.monew.domain.interestkeyword.InterestKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        this.name = name;
        this.keywords = new ArrayList<>();
        keywords.forEach(this::addKeyword);
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
}
