package wkv.exclusio.batch.movies;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;

import java.util.List;

@StepScope
public class Writer implements ItemWriter<MovieEntity>{
		
	@Autowired
	private MovieRepository movieRepository;
	
	@Override
	public void write(Chunk<? extends MovieEntity> movies) {
		for (MovieEntity movie : movies) {
			List<MovieEntity> moviesInBase = this.movieRepository.findByTitre(movie.getTitre());
			List<MovieEntity> list = moviesInBase.stream().filter(m -> m.equals(movie)).toList();
			if(list.isEmpty()) {
				this.movieRepository.save(movie);
			}
        }
	}

}
