package com.codeit.monew.domain.article.matcher;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArticleMatcherImpl implements ArticleMatcher {

    @Override
    public boolean match(ArticleCreateRequest articleCreateRequest, List<Interest> interests) {
        String title = articleCreateRequest.title().toLowerCase();

        return interests.stream()
                .anyMatch(interest -> matchInterest(interest, title));


    }

    private boolean matchInterest(Interest interest, String title) {

        List<String> searchItems = new ArrayList<>();
        searchItems.add(interest.getName().toLowerCase().trim());

        interest.getKeywords()
                .forEach(keyword -> searchItems.add(keyword.trim().toLowerCase()));

        return searchItems.stream()
                .anyMatch(keyword -> keyword.trim().toLowerCase().equals(title));
    }
}
