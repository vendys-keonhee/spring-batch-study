package kr.co.vendys.vone.springbatchstudy.job;

import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job simpleJob1() {
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new StepBuilder("simpleStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step 1");
                    log.info(">>>>> requestDate = {}", requestDate);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new StepBuilder("simpleStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step 2");
                    log.info(">>>>> requestDate = {}", requestDate);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


}
