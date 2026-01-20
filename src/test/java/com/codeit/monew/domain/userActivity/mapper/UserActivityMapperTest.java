package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.articleView.dto.mapper.ArticleViewMapper;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("사용자 활동 Mapper 테스트")
class UserActivityMapperTest {

    UserActivityMapper userActivityMapper;
    SubscriptionMapper subscriptionMapper;
    ArticleViewMapper articleViewMapper;

    @BeforeEach
    void setUp() {
        subscriptionMapper = new SubscriptionMapper();
        articleViewMapper = new ArticleViewMapper();
        userActivityMapper = new UserActivityMapper(subscriptionMapper, articleViewMapper);
    }

    @Test
    @DisplayName("Mapper변환 성공 테스트")
    void toDto() {
        // given
        User user = Instancio.create(User.class);
        Interest interest = Instancio.create(Interest.class);
        InterestUser interestUser = new InterestUser(user, interest);
        Article article = Instancio.create(Article.class);
        Comment comment = new Comment(user, article, "댓글 내용");
        CommentUserLike commentLike = new CommentUserLike(user, comment);
        ArticleView articleView = new ArticleView(user, article);

        // when
        UserActivityDto dto = userActivityMapper.toDto(
                user,
                List.of(interestUser),
                List.of(comment),
                List.of(commentLike),
                List.of(articleView)
        );

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.subscriptions()).isNotNull();
        assertThat(dto.subscriptions()).isNotEmpty();
        assertThat(dto.comments()).isNotNull();
        assertThat(dto.comments()).isNotEmpty();
        assertThat(dto.commentLikes()).isNotNull();
        assertThat(dto.commentLikes()).isNotEmpty();
        assertThat(dto.articleViews()).isNotNull();
        assertThat(dto.articleViews()).isNotEmpty();
    }
}
