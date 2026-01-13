package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public interface ArticleCollector {

    List<ArticleCreateRequest> collect(List<Interest> interests);
}
