package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.infrastructure.ArticleCollector;
import com.codeit.monew.domain.article.infrastructure.CollectedArticleMapper;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
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
    CollectedArticleMapper collectedArticleMapper;

    @InjectMocks
    ArticleCollectServiceImpl articleCollectService;

    private List<Interest> interests;

    @BeforeEach
    void setUp() {
        articleCollectService = new ArticleCollectServiceImpl(
                List.of(articleCollector1, articleCollector2),
                articleRepository,
                interestRepository,
                collectedArticleMapper
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
        given(articleCollector1.collect(interests)).willReturn(List.of(articleCreateRequest1));
        given(articleCollector2.collect(interests)).willReturn(List.of(articleCreateRequest2));

        // when
        articleCollectService.collectAndSave();

        // then
        then(articleCollector1).should(times(1)).collect(interests);
        then(articleCollector2).should(times(1)).collect(interests);
    }

    @Test
    @DisplayName("수집한 기사 URL들 중 존재하지 않는 기사만 필터링하여 저장한다. ")
    void collect_isNotDuplicate_article_save() {
        // given
        given(interestRepository.findAll()).willReturn(interests);

        // 새로 수집된 기사(1: 중복되지 않은 기사, 2: 중복되는 기사)
        String url = "https://test.com/";
        String duplicatedUrl = "https://test2.com/";
        ArticleCreateRequest request = ArticleCreateRequestFixture.createWithTitleAndSummaryWithURL("제목1", "내용1", url);
        ArticleCreateRequest duplicatedRequest = ArticleCreateRequestFixture.createWithTitleAndSummaryWithURL("제목2", "내용2", duplicatedUrl);

        given(articleCollector1.collect(interests)).willReturn(List.of(request));
        given(articleCollector2.collect(interests)).willReturn(List.of(duplicatedRequest));

        Article existingArticle = ArticleFixture.createEntityWithSourceUrl(duplicatedUrl);
        // 수집된 기사들을 인자로 넣어, 중복되는 기사들을 모두 가져옴
        // 하나씩 검사하는 것보다, 중복된 기사를 가져와, 그것을 제외한 기사들을 save하기 위함
        given(articleRepository.findAllBySourceUrlIn(anyList())).willReturn(List.of(existingArticle));

        Article newArticle = ArticleFixture.createEntity(request);
        given(collectedArticleMapper.toEntity(request)).willReturn(newArticle);

        // when
        articleCollectService.collectAndSave();

        // then
        // saveAll에 전달된 리스트에 신규 기사만 포함되어 있는지 검증
        then(articleRepository).should().saveAll(argThat(iterable -> {
                    List<Article> articles = (List<Article>) iterable;
                    return articles.size() == 1 && articles.get(0).getSourceUrl().equals(url);
                }
        ));

    }

}
