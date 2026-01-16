package com.codeit.monew.domain.article.matcher;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleMatcherImpl implements ArticleMatcher {

    @Override
    public boolean match(ArticleCreateRequest articleCreateRequest, List<Interest> interests) {
        String title = articleCreateRequest.title().toLowerCase();
        String summary = articleCreateRequest.summary().toLowerCase();
        String target = title + " " + summary;

        return interests.stream()
                .anyMatch(interest -> matchInterest(interest, target));

    }

    private boolean matchInterest(Interest interest, String target) {
        // interest 이름이 포함되지 않으면 실패
        if (!target.contains(interest.getName().toLowerCase().trim())) {
            return false;
        }

        // 모든 키워드가 포함되어야 함
        return interest.getKeywords().stream()
                .allMatch(keyword -> target.contains(keyword.getKeyword().toLowerCase().trim()));
    }

}
