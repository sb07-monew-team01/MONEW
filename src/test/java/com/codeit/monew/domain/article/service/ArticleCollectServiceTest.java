package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
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

    @BeforeEach
    void setUp() {
        articleCollectService = new ArticleCollectServiceImpl(
                List.of(articleCollector1, articleCollector2),
                articleRepository,
                articleMapper
        );
    }

    @Test
    @DisplayName("저장된 모든 Interests를 조회할 수 있다.")
    void collectAllNews_FindAllInterests() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                InterestFixture.create("축구", List.of("손흥민", "메시")),
                InterestFixture.create("경제", List.of("코인"))
        );

        given(interestRepository.findAll()).willReturn(interests);

        articleCollectService.collectAndSave(interests);

        then(interestRepository).should().findAll();
    }

}
