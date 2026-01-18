package com.codeit.monew.domain.commentuserlike.entity;

import com.codeit.monew.domain.BaseEntity;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "comment_user_likes",
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"user_id", "comment_id"}))
@Getter
@NoArgsConstructor
public class CommentUserLike extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public CommentUserLike(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static CommentUserLike create(User user, Comment comment) {
        return new CommentUserLike(user, comment);
    }
}
