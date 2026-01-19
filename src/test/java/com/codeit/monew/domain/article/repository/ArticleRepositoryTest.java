package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private EntityManager  entityManager;

    @Nested
    @DisplayName("기사 물리 삭제 테스트")
    class ArticleDelete {
        @Test
        @DisplayName("물리 삭제 성공 : 기사 삭제 시 연관된 댓글과 조회 이력도 모두 삭제된다")
        void hardDelete_article_and_cascades() {
            // given: 기사, 댓글, 조회이력 저장
            User user = new User("test@email.com", "testNickName", "test_1234");
            entityManager.persist(user);

            Article article = ArticleFixture.createDefaultEntity();
            entityManager.persist(article);
            UUID articleId = article.getId();

            entityManager.flush();
            
            
            Comment comment = new Comment(user, article, "댓글 내용");
            entityManager.persist(comment);
            UUID commentId = comment.getId();

            entityManager.flush();
            entityManager.clear();

            // when
            articleRepository.deleteById(articleId);

            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(entityManager.find(Article.class, articleId)).isNull();
            assertThat(entityManager.find(Comment.class, commentId)).isNull();
        }
    }
}