package wkv.exclusio.batch.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;


@StepScope
public class Reader implements ItemReader<Map<Integer, String>>{
	
	private static final Logger log = LoggerFactory.getLogger(Reader.class);
	
	private RestTemplate restTemplate;
	
	private int id = 1;
	private static final int MAX = 500000;
	
	public Reader(RestTemplate restTemplate, MovieRepository movieRepository) {
		this.restTemplate = restTemplate;
		List<MovieEntity> movies = movieRepository.findAll();
		if(movies.size() > 0) {			
			id = Integer.parseInt(movies.get(movies.size()-1).getCodeHtmlAllocine())+1;
			log.info("id : {}", id);
		}
	}

    @Override
    public Map<Integer, String> read() throws Exception {
    	log.info("reader id : {}", id);
    	Map<Integer, String> res = new HashMap<Integer, String>();
        if (id <= MAX) {
        	String url = "https://www.allocine.fr/film/fichefilm_gen_cfilm=" + id + ".html";
        	String content;
        	try {        		
        		content = this.restTemplate.getForObject(url, String.class);
        	}catch(HttpClientErrorException | HttpServerErrorException e) {
        		log.info("catch");
        		id++;
        		res.put(id, "");
        		return res;
        	}
            id++;
            res.put(id, content);
            return res;
        } else {
            return null;
        }
    }
}
