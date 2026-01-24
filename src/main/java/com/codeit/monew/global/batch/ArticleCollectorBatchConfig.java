package com.codeit.monew.global.batch;

import com.codeit.monew.domain.article.infrastructure.CollectReport;
import com.codeit.monew.domain.article.infrastructure.service.ArticleCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ArticleCollectorBatchConfig extends DefaultBatchConfiguration {

    private final ArticleCollectService articleCollectService;

    @Override
    @NonNull
    protected String getTablePrefix() {
        return "batch.batch_";
    }

    @Bean
    public Job articleCollectJob(JobRepository jobRepository, Step articleCollectStep, ArticleJobListener listener) {
        return new JobBuilder("articleCollectJob", jobRepository)
                .listener(listener) // 리스너 등록
                .start(articleCollectStep)
                .build();
    }

    @Bean
    public Step articleCollectStep() {
        // jobRepository()와 getTransactionManager() 모두 부모 메서드 사용
        return new StepBuilder("articleCollectStep", jobRepository())
                .tasklet((contribution, chunkContext) -> {
                    CollectReport collectReport = articleCollectService.collectAndSave();
                    chunkContext.getStepContext()
                            .getStepExecution()
                            .setReadCount(collectReport.fetchedArticle());
                    contribution.incrementWriteCount(collectReport.savedArticle());
                    return RepeatStatus.FINISHED;
                }, getTransactionManager())
                .build();
    }


}
