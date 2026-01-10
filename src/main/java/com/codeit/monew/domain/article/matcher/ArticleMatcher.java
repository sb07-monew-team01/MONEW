package com.codeit.monew.domain.article.matcher;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public interface ArticleMatcher {
    boolean match(ArticleCreateRequest articleCreateRequest, List<Interest> interests);
}
