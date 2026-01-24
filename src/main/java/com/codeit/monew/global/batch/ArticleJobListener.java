package com.codeit.monew.global.batch;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleJobListener implements JobExecutionListener {

    private final MeterRegistry meterRegistry;

    // 실시간 수치를 담아둘 그릇 (게이지)
    // AtomicReference 같은 객체를 사용하면, Gauge는 이 객체의 참조를 딱 붙잡고 있다가,
    // 사용자가 호출할 때마다 get() 메서드를 호출해서 최신 값을 안전하게 가져올 수 있습니다.
    private final AtomicReference<Double> readCountGauge = new AtomicReference<>(0.0);
    private final AtomicReference<Double> writeCountGauge = new AtomicReference<>(0.0);

    @PostConstruct
    public void init() {
        // Read 건수 등록
        Gauge.builder("article.collect.read.count", readCountGauge, AtomicReference::get)
                .description("이번 배치에서 읽어온 총 기사 수")
                .register(meterRegistry);

        // Write 건수 등록
        Gauge.builder("article.collect.write.count", writeCountGauge, AtomicReference::get)
                .description("이번 배치에서 저장된 신규 기사 수")
                .register(meterRegistry);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        // 직접 계산 로직 사용
        long duration = getMillis(jobExecution.getStartTime(), jobExecution.getEndTime());

        log.info("=======================================================");
        log.info(" Job 상세 리포트: {}", jobName);
        log.info("-------------------------------------------------------");
        log.info("총 소요 시간 : {} ms", duration);
        log.info("최종 상태    : {}", jobExecution.getStatus());
        log.info("-------------------------------------------------------");

        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        for (StepExecution step : stepExecutions) {
            long stepDuration = getMillis(step.getStartTime(), step.getEndTime());

            log.info("     Step 이름      : {}", step.getStepName());
            log.info("     - 소요 시간    : {} ms", stepDuration);
            log.info("     - 읽은 데이터(Read)   : {} 건", step.getReadCount());
            log.info("     - 쓴 데이터(Write)    : {} 건", step.getWriteCount());
            log.info("     - 커밋 횟수(Commit)   : {} 회", step.getCommitCount());
            log.info("     - 실패/롤백(Rollback) : {} 회", step.getRollbackCount());

            if (!step.getFailureExceptions().isEmpty()) {
                log.error("      에러 발생: {}", step.getFailureExceptions().get(0).getMessage());
            }
            log.info("-------------------------------------------------------");
        }
        log.info("=======================================================");

        for (StepExecution step : jobExecution.getStepExecutions()) {
            if ("articleCollectStep".equals(step.getStepName())) {
                // 실시간 수치 업데이트
                readCountGauge.set((double) step.getReadCount());
                writeCountGauge.set((double) step.getWriteCount());
            }
        }
    }

    // Spring Batch 5(Java 17+) 시간 계산 유틸리티
    private long getMillis(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return Duration.between(start, end).toMillis();
    }
}