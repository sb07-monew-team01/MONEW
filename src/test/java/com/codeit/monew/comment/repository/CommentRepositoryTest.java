package com.codeit.monew.comment.repository;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.user.User;
import com.codeit.monew.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("댓글 레포지토리 통합 테스트")
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;

    @Test
    @DisplayName("성공: 댓글 저장 시 기본값이 올바르게 설정된다.")
    void saveComment_Success() {
        // given
        User user = userRepository.save(new User(
                "test@email.com",
                "nick",
                "1234"));
        Article article = articleRepository.save(new Article(
                ArticleSource.NAVER,
                "https://naver.com/article/123",
                "테스트 제목",
                LocalDateTime.now(),
                "요약입니다.",
                null
        ));

        String content = "댓글 내용입니다.";
        Comment comment = commentRepository.save(new Comment(user, article, content));

        // when
        Comment saved = commentRepository.save(comment);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo(content);
        assertThat(saved.getDeletedAt()).isNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

}
