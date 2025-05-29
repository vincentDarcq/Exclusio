package wkv.exclusio.batch.series;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.repositories.SerieRepository;

import java.util.Map;

@Configuration
public class JobConfSeries {
		
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SerieRepository serieRepository;
	
	@Bean(name = "alloJobSeries")
    public Job jobSerie(@Qualifier("stepSerie") Step step, JobRepository jobRepository) {
        return new JobBuilder("jobSerie", jobRepository)
        	.incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }
	
    @Bean(name = "stepSerie")
    public Step stepSerie(
    		JobRepository jobRepository,
    		PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("step", jobRepository)
            .<Map<Integer, String>, SerieEntity>chunk(1, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean(name = "readerSerie")
    public ItemReader<Map<Integer, String>> reader() {
    	return new Reader(restTemplate, serieRepository);
    }
    @Bean(name = "processorSerie")
    public ItemProcessor<Map<Integer, String>, SerieEntity> processor() {
    	return new Processor();
    }
    @Bean(name = "writerSerie")
    public ItemWriter<SerieEntity> writer() {
    	return new Writer();
    }
}
