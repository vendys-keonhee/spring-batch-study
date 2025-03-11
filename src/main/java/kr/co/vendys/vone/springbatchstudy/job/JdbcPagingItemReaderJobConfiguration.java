package kr.co.vendys.vone.springbatchstudy.job;

import kr.co.vendys.vone.springbatchstudy.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcPagingItemReaderJobConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final DataSource dataSource;

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception {
        return new JobBuilder("jdbcPagingItemReaderJob", jobRepository)
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() throws Exception {
        return new StepBuilder("jdbcPagingItemReaderStep", jobRepository)
                .<Member, Member>chunk(chunkSize, transactionManager)
                .reader(jdbcPagingItemReader())
                .writer(jdbcPagingItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Member> jdbcPagingItemReader() throws Exception {
        JdbcPagingItemReader<Member> reader = new JdbcPagingItemReader<>();

        reader.setPageSize(chunkSize);
        reader.setFetchSize(chunkSize);
        reader.setDataSource(dataSource);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Member.class));
        reader.setName("jdbcPagingItemReader");
        reader.setQueryProvider(pagingQueryProvider());

        return reader;
    }

    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, name, version, createDate, createUser, updateDate, updateUser");
        queryProvider.setFromClause("from Member");

        queryProvider.setSortKeys(Map.of("id", Order.DESCENDING));

        return queryProvider.getObject();
    }

    @Bean
    public ItemWriter<Member> jdbcPagingItemWriter() {
        return chunk -> {
            log.info("--------------------------");
            for (Member member : chunk.getItems()) {
                log.info("Current Member : {}", member);
            }
        };
    }
}
