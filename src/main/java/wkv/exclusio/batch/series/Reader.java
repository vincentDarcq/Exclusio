package wkv.exclusio.batch.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.repositories.MovieRepository;
import wkv.exclusio.repositories.SerieRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@StepScope
public class Reader implements ItemReader<Map<Integer, String>>{
	
	private static final Logger log = LoggerFactory.getLogger(Reader.class);
	
	private RestTemplate restTemplate;
	
	private int id = 1;
	private static final int MAX = 500000;
	
	public Reader(RestTemplate restTemplate, SerieRepository serieRepository) {
		this.restTemplate = restTemplate;
		List<SerieEntity> series = serieRepository.findAll();
		if(!series.isEmpty()) {
			id = Integer.parseInt(series.get(series.size()-1).getCodeHtmlAllocine())+1;
			log.info("id serie: {}", id);
		}
	}

    @Override
    public Map<Integer, String> read() {
    	log.info("reader id : {}", id);
    	Map<Integer, String> res = new HashMap<Integer, String>();
        if (id <= MAX) {
        	String url = "https://www.allocine.fr/series/ficheserie_gen_cserie=" + id + ".html";
        	String content;
        	try {        		
        		content = this.restTemplate.getForObject(url, String.class);
        	}catch(HttpClientErrorException | HttpServerErrorException e) {
        		log.info("catch");
        		res.put(id, "");
				id++;
        		return res;
        	}
            res.put(id, content);
			id++;
            return res;
        } else {
            return null;
        }
    }
}
