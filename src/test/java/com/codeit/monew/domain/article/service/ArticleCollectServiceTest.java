package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
import com.codeit.monew.domain.article.infrastructure.service.ArticleCollectServiceImpl;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ArticleCollectServiceTest {
    @Mock
    ArticleCollector articleCollector;

    @Mock
    ArticleRepository articleRepository;

    @InjectMocks
    ArticleCollectServiceImpl articleCollectService;

    @Test
    @DisplayName("하나의 사이트에서 모든 기사들을 수집하고, 저장할 수 있다.")
    void collect_allArticle_and_save_success() {
        String url1 = "https://test1.com/articles/1";
        String url2 = "https://test1.com/articles/2";
        ArticleCreateRequest createRequest1 = ArticleCreateRequestFixture.createWithTitleAndSummaryWithURL("제목1", "요약 내용입니다.1", url1);
        ArticleCreateRequest createRequest2 = ArticleCreateRequestFixture.createWithTitleAndSummaryWithURL("제목2", "요약 내용입니다.2", url2);
        List<Interest> interests = InterestFixture.createMultiple(
                InterestFixture.create("테스트용 name", List.of("안녕")),
                InterestFixture.create("테스트용 name2", List.of("안녕2"))
        );
        given(articleCollector.collect(any())).willReturn(List.of(createRequest1, createRequest2));
        given(articleRepository.existsBySourceUrl(any())).willReturn(false);

        // when
        articleCollectService.collectAndSave(interests);

        // then
        then(articleRepository).should(times(2)).save(any());

    }


}
