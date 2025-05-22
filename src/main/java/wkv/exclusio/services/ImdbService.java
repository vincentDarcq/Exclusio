package wkv.exclusio.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wkv.exclusio.dto.ImdbResponseDto;
import wkv.exclusio.dto.ImdbResultDto;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ImdbService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    public MovieEntity findGrade(MovieEntity entity, String name){
        ImdbResponseDto resultSearchDto = this.restTemplate.getForObject("https://v3.sg.media-imdb.com/suggestion/x/" + name + ".json?includeVideos=1", ImdbResponseDto.class);
        ImdbResultDto movieToFind = resultSearchDto.getD().stream().filter(r -> "movie".equals(r.getQid()))
                .findFirst().orElse(null);
        if(movieToFind != null) {
            entity.setCodeHtmlImdb(movieToFind.getId());
            String url = "https://www.imdb.com/title/" + movieToFind.getId();
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> content = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            String html = Objects.requireNonNull(content.getBody());
            int start = html.indexOf("⭐");
            if(start != -1) {
                int end = html.indexOf(" |", start);
                if(end == -1 || end - start > 7) {
                    end = html.indexOf("\"", start);
                }
                entity.setImdbGrade(Float.parseFloat(html.substring(start + 2, end).trim()));
                return entity;
            }
        }
        return entity;
    }
}
