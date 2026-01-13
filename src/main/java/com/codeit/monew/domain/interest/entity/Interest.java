package com.codeit.monew.domain.interest.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
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

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "interest_keywords",
            joinColumns = @JoinColumn(name = "interest_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = new ArrayList<>(keywords);
    }

    public Interest update(List<String> keywords) {
        this.keywords = new ArrayList<>(keywords);
        return this;
    }
}
