package wkv.exclusio.services;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wkv.exclusio.dto.ImdbResponseDto;
import wkv.exclusio.dto.ImdbResultDto;
import wkv.exclusio.entities.MovieEntity;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ImdbService {

    private static final Logger log = LoggerFactory.getLogger(ImdbService.class);
    private final RestTemplate restTemplate;

    public MovieEntity findImdbInfos(MovieEntity entity, String name, boolean cov){
        ImdbResponseDto resultSearchDto;
        try {
            String url = "https://v3.sg.media-imdb.com/suggestion/x/" + name + ".json?includeVideos=1";
            resultSearchDto = this.restTemplate.getForObject(url, ImdbResponseDto.class);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return entity;
        }
        ImdbResultDto movieToFind = resultSearchDto.getD().stream()
                .filter(r -> "movie".equals(r.getQid()))
                .filter(r -> r.getY() == entity.getYear())
                .findFirst().orElse(null);
        if(movieToFind != null) {
            entity.setCodeHtmlImdb(movieToFind.getId());
            MovieEntity movieEntity = findImdbGradeAndCov(entity, cov);
            if(movieEntity != null) {
                return entity;
            }
        }
        return entity;
    }

    public MovieEntity findImdbGradeAndCov(MovieEntity entity, boolean cov){
        log.info("Processing {}, imdb code : {}", entity.getTitre(), entity.getCodeHtmlImdb());
        String url = "https://www.imdb.com/title/" + entity.getCodeHtmlImdb();
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> content = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        if(!cov){
            Document doc = Jsoup.parse(Objects.requireNonNull(content.getBody()));
            Element imdbElement = doc
                    .getElementsByAttributeValue("type", "application/ld+json")
                    .first();
            String imdbString = imdbElement.html();
            while(imdbString.contains("\r")) {
                imdbString = imdbString.substring(0, imdbString.indexOf("\r"))+imdbString.substring(imdbString.indexOf("\r")+2);
            }
            JSONObject imdbObject = new JSONObject(imdbString);
            if(imdbObject.has("image")) {
                entity.setCovPortrait(imdbObject.getString("image"));
            }else {
                log.info("No imdb cov portrait");
            }
        }
        String html = Objects.requireNonNull(content.getBody());
        int start = html.indexOf("⭐");
        if(start != -1) {
            int end = html.indexOf(" |", start);
            if(end == -1 || end - start > 7) {
                end = html.indexOf("\"", start);
            }
            entity.setImdbGrade(Float.parseFloat(html.substring(start + 2, end).trim()));
        }else {
            log.info("No imdb grade");
        }
        return entity;
    }
}
