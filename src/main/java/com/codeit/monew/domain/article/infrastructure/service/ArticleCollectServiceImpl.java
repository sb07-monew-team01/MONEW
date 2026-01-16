package com.codeit.monew.domain.article.infrastructure.service;

import com.codeit.monew.domain.interest.entity.Interest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCollectServiceImpl implements ArticleCollectService {

    @Override
    public void collectAndSave(List<Interest> interests) {
    }
}
