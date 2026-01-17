package com.codeit.monew.domain.article.infrastructure.scheduler;

import com.codeit.monew.domain.article.infrastructure.service.ArticleCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCollectScheduler {

    private final ArticleCollectService articleCollectService;

    @Scheduled(cron = "0 0/5 * * * *") // 매 5분마다 (0분, 5분, 10분 ...)
    public void scheduleArticleCollection() {
        log.info("기사 수집 스케줄러 시작: {}", LocalDateTime.now());

        try {
            articleCollectService.collectAndSave();
            log.info("기사 수집 스케줄러 완료: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("기사 수집 스케줄러 중 예상치 못한 오류 발생", e);
        }
    }
}
