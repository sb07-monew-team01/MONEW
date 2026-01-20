package com.codeit.monew.domain.article.service.s3.scheduler;

import com.codeit.monew.domain.article.service.s3.ArticleBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class s3BackupScheduler {

    private final ArticleBackupService articleBackupService;

    @Scheduled(cron = "0 1 0 * * *")
    public void schedulerArticleBackup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate backupTarget = now.minusDays(2).toLocalDate();

        log.info("백업 스케줄러 시작 - 실행 시간: {}, 대상 날짜 {}", now, backupTarget);
        try {
            articleBackupService.backupArticles(backupTarget);
            log.info("{} 스케줄러 성공", backupTarget);
        } catch (Exception e) {
            log.error("{} 스케줄러 실패",backupTarget, e);
        }
    }
}
