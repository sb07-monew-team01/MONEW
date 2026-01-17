package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
import com.codeit.monew.domain.article.infrastructure.service.ArticleCollectServiceImpl;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ArticleCollectServiceTest {

    @Mock
    ArticleCollector articleCollector1;

    @Mock
    ArticleCollector articleCollector2;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    InterestRepository interestRepository;

    @Mock
    ArticleMapper articleMapper;

    @InjectMocks
    ArticleCollectServiceImpl articleCollectService;

    private List<Interest> interests;

    @BeforeEach
    void setUp() {
        articleCollectService = new ArticleCollectServiceImpl(
                List.of(articleCollector1, articleCollector2),
                articleRepository,
                interestRepository,
                articleMapper
        );

        interests = InterestFixture.createMultiple(
                InterestFixture.create("축구", List.of("손흥민", "메시")),
                InterestFixture.create("경제", List.of("코인"))
        );
    }

    @Test
    @DisplayName("저장된 모든 Interests를 조회할 수 있다.")
    void collectAllArticle_FindAllInterests() {
        // given
        given(interestRepository.findAll()).willReturn(interests);

        // when
        articleCollectService.collectAndSave();

        // then
        then(interestRepository).should().findAll();
    }

    @Test
    @DisplayName("Interest가 1개 이상 존재한다면 기사를 수집한다.")
    void collect_when_isNotEmpty_start_collect() {
        // given
        given(interestRepository.findAll()).willReturn(interests);
        ArticleCreateRequest articleCreateRequest1 = ArticleCreateRequestFixture.createWithTitleAndSummary("제목1", "기사내용입니다1");
        ArticleCreateRequest articleCreateRequest2 = ArticleCreateRequestFixture.createWithTitleAndSummary("제목2", "기사내용입니다2");
        given(articleCollector1.collect(interests)).willReturn(List.of(articleCreateRequest1, articleCreateRequest2));
        given(articleCollector2.collect(interests)).willReturn(List.of(articleCreateRequest1, articleCreateRequest2));

        // when
        articleCollectService.collectAndSave();

        then(articleCollector1).should(times(1)).collect(interests);
        then(articleCollector2).should(times(1)).collect(interests);
    }

}
