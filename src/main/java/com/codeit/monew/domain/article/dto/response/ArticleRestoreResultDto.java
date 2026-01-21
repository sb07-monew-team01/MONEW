package com.codeit.monew.domain.article.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ArticleRestoreResultDto(
    LocalDateTime restoreDate,
    List<UUID> restoreArticleIds,
    Long restoredArticleCount
) {

}
