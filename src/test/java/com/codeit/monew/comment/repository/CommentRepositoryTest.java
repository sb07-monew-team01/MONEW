package com.codeit.monew.comment.repository;

import com.codeit.monew.article.repository.TestQueryDslConfig;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@DataJpaTest
@Import(TestQueryDslConfig.class)
@DisplayName("댓글 레포지토리 테스트")
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("성공: 댓글 저장 시 기본값이 올바르게 설정된다.")
    void saveComment_Success() {
        // given - EntityManager로 연관 엔티티 직접 생성
        User user = new User(
                "test@email.com",
                "nick",
                "1234"
        );
        entityManager.persist(user);

        Article article = new Article(
                ArticleSource.NAVER,
                "https://naver.com/article/123",
                "테스트 제목",
                LocalDateTime.now(),
                "요약입니다.",
                null
        );
        entityManager.persist(article);

        String content = "댓글 내용입니다.";
        Comment comment = new Comment(user, article, content);

        // when - 실제 테스트할 동작
        Comment saved = commentRepository.save(comment);
        entityManager.flush();
        entityManager.clear(); // 1차 캐시 지우기

        // then - DB에서 다시 조회하고 검증하기
        Comment found = commentRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getContent()).isEqualTo(content);
        assertThat(found.getDeletedAt()).isNull();
        assertThat(found.getCreatedAt()).isNotNull();
    }

}
