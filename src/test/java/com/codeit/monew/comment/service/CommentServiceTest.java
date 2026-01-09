package com.codeit.monew.comment.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.comment.service.CommentServiceImpl;
import com.codeit.monew.domain.user.User;
import com.codeit.monew.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 서비스 테스트")
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    @DisplayName("댓글을 등록할 수 있다.")
    class CreateComment {

        @Test
        @DisplayName("유저와 기사가 있으면 댓글이 정상적으로 등록된다")
        void createComment_success() {
            // given
            String content = "test";
            UUID articleId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            User user = new User("test@email.com","nick","1234");
            Article article = new Article(
                    ArticleSource.NAVER,
                    "www.naver.com/article/123",
                    "제목입니다.",
                    LocalDateTime.now(),
                    "요약입니다.",
                    null
            );

            CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId, content);
            Comment savedComment = new Comment(user, article, content);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

            // when
            CommentDto response = commentService.createComment(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEqualTo(content);

            verify(userRepository).findById(userId);
            verify(articleRepository).findById(articleId);
            verify(commentRepository).save(any(Comment.class));
        }
    }

}
