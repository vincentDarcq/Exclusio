package wkv.exclusio.batch.config;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;

@StepScope
public class Writer implements ItemWriter<MovieEntity>{
		
	@Autowired
	private MovieRepository movieRepository;
	
	@Override
	public void write(Chunk<? extends MovieEntity> movies) throws Exception {
		for (MovieEntity movie : movies) {
			this.movieRepository.save(movie);
        }
	}

}
