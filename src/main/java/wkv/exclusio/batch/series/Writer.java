package wkv.exclusio.batch.series;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.repositories.MovieRepository;
import wkv.exclusio.repositories.SerieRepository;

import java.util.List;

@StepScope
public class Writer implements ItemWriter<SerieEntity>{
		
	@Autowired
	private SerieRepository serieRepository;
	
	@Override
	public void write(Chunk<? extends SerieEntity> series) {
		for (SerieEntity serie : series) {
			List<SerieEntity> seriesInBase = this.serieRepository.findByTitre(serie.getTitre());
			List<SerieEntity> list = seriesInBase.stream().filter(s -> s.equals(serie)).toList();
			if(list.isEmpty()) {
				this.serieRepository.save(serie);
			}
        }
	}

}
