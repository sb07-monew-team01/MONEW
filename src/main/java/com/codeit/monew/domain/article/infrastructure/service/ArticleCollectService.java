package com.codeit.monew.domain.article.infrastructure.service;

import com.codeit.monew.domain.interest.entity.Interest;

import java.util.List;

public interface ArticleCollectService {

    void collectAndSave(List<Interest> interests);
}
