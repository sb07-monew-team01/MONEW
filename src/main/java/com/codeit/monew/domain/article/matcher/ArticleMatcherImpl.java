package com.codeit.monew.domain.article.matcher;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleMatcherImpl implements ArticleMatcher {

    @Override
    public boolean match(ArticleCreateRequest articleCreateRequest, List<Interest> interests) {
        return false;
    }
}
