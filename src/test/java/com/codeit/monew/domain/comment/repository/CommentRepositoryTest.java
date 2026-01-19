package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentWithLikeCount;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
@ActiveProfiles("test")
@DisplayName("댓글 레포지토리 테스트")
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TestEntityManager entityManager;

    private Comment createComment(User user, Article article, String content, LocalDateTime createdAt) {
        Comment comment = new Comment(user, article, content);
        entityManager.persist(comment);

        // createdAt 강제 세팅 (정렬 테스트용)
        entityManager.getEntityManager()
                .createQuery("update Comment c set c.createdAt = :createdAt where c.id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", comment.getId())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        return commentRepository.findById(comment.getId()).orElseThrow();
    }


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

    @Test
    @DisplayName("성공: 기사별 댓글 조회 시 likeCount가 포함된다")
    void findByArticleIdOrderBy_likeCountIncluded() {
        // given
        User user = new User("a@test.com", "userA", "1234");
        entityManager.persist(user);

        Article article = new Article(
                ArticleSource.NAVER,
                "url",
                "제목",
                LocalDateTime.now(),
                "요약",
                null
        );
        entityManager.persist(article);

        Comment comment1 = createComment(user, article, "댓글1", LocalDateTime.now().minusMinutes(5));
        Comment comment2 = createComment(user, article, "댓글2", LocalDateTime.now().minusMinutes(3));

        // 좋아요 2개
        entityManager.persist(new CommentUserLike(user, comment1));
        entityManager.persist(new CommentUserLike(user, comment1));

        entityManager.flush();
        entityManager.clear();

        // when
        var slice = commentRepository.findByArticleIdOrderBy(
                article.getId(),
                CommentOrderBy.LIKE_COUNT,
                SortDirection.DESC,
                null,
                null,
                10
        );

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent().get(0).comment().getId()).isEqualTo(comment1.getId());
        assertThat(slice.getContent().get(0).likeCount()).isEqualTo(2L);
    }



}
