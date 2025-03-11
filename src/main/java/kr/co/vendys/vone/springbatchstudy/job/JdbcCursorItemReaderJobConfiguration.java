package kr.co.vendys.vone.springbatchstudy.job;

import kr.co.vendys.vone.springbatchstudy.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcCursorItemReaderJobConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final DataSource dataSource;

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcCursorItemReaderJob() {
        return new JobBuilder("jdbcCursorItemReaderJob", jobRepository)
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return new StepBuilder("jdbcCursorItemReaderStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Member> jdbcCursorItemReader() {
        JdbcCursorItemReader<Member> jdbcCursorItemReader = new JdbcCursorItemReader<>();

        jdbcCursorItemReader.setFetchSize(chunkSize);
        jdbcCursorItemReader.setDataSource(dataSource);
        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>(Member.class));
        jdbcCursorItemReader.setSql("select member.id, member.name, member.version, member.createDate, member.createUser, member.updateDate, member.updateDate from Member member");
        jdbcCursorItemReader.setName("jdbcCursorItemReader");

        return jdbcCursorItemReader;
    }

    @Bean
    public ItemWriter<Member> jdbcCursorItemWriter() {
        log.info("-------------------------------");
        return chunk -> {
            for (Member member : chunk.getItems()) {
                log.info("Current Member: {}", member);
            }
        };
    }


}



















