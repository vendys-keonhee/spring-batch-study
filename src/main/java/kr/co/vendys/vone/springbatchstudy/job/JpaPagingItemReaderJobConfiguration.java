package kr.co.vendys.vone.springbatchstudy.job;

import jakarta.persistence.EntityManagerFactory;
import kr.co.vendys.vone.springbatchstudy.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaPagingItemReaderJobConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    @Bean
    public Job jpaPagingItemReaderJob() {
        return new JobBuilder("jpaPagingItemReaderJob", jobRepository)
                .start(jpaPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep() {
        return new StepBuilder("jpaPagingItemReaderStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Member> jpaPagingItemReader() {
        JpaPagingItemReader<Member> jpaPagingItemReader = new JpaPagingItemReader<>();

        jpaPagingItemReader.setName("jpaPagingItemReader");
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(chunkSize);
        jpaPagingItemReader.setQueryString("select m from Member m");

        return jpaPagingItemReader;
    }

    @Bean
    public ItemWriter<Member> jpaPagingItemWriter() {
        return chunk -> {
            log.info("----------------------------");
            for (Member member : chunk.getItems()) {
                log.info("Current Member: {}", member);
            }
        };
    }

}











