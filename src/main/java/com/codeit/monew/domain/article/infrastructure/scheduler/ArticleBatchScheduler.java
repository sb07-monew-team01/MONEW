package com.codeit.monew.domain.article.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job articleCollectJob; // Config의 @Bean 메서드 명과 일치됨

    @Scheduled(cron = "0 0/5 * * * *") // 5분마다 실행
    public void runJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Article Batch Job 시작");
            jobLauncher.run(articleCollectJob, params);
            log.info("Article Batch Job 완료");

        } catch (Exception e) {
            log.error("Article Batch Job 실행 중 에러 발생: {} ", e.getMessage());
        }
    }
}