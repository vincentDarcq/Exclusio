package wkv.exclusio.batch.movies;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;

@Configuration
public class JobConfMovies {
		
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Bean(name = "alloJobMovies")
    public Job job(Step step, JobRepository jobRepository) {
        return new JobBuilder("job", jobRepository)
        	.incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }
	
    @Bean
    public Step step(
    		JobRepository jobRepository,
    		PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("step", jobRepository)
            .<Map<Integer, String>, MovieEntity>chunk(1, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public ItemReader<Map<Integer, String>> reader() {
    	return new Reader(restTemplate, movieRepository);
    }
    @Bean
    public ItemProcessor<Map<Integer, String>, MovieEntity> processor() {
    	return new Processor();
    }
    @Bean
    public ItemWriter<MovieEntity> writer() {
    	return new Writer();
    }
}
