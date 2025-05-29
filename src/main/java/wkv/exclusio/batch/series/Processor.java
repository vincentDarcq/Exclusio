package wkv.exclusio.batch.series;

import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.services.ImdbService;
import wkv.exclusio.services.SerieService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@StepScope
public class Processor implements ItemProcessor<Map<Integer, String>, SerieEntity>{
	
	private static final Logger log = LoggerFactory.getLogger(Processor.class);
	@Autowired
	private SerieService serieService;
	
    @Override
    public SerieEntity process(Map<Integer, String> content) {
		try {
			return parseContent(content);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
        return null;
    }
    
    private SerieEntity parseContent(Map<Integer, String> content) {
    	List<Integer> arr = new ArrayList<>(content.keySet());
    	Document doc = Jsoup.parse(content.get(arr.get(0)));
    	
    	Element body = doc.body();
        if(Strings.isBlank(content.get(arr.get(0)))) {
        	log.info("page inexistante");
        	return null;
        }else {
        	Element serieElement = body
        			.getElementsByAttributeValue("type", "application/ld+json")
                    .first();
        	
        	if(serieElement == null) {
        		log.info("page existante mais sans data json");
        		return null;
        	}else {
        		String serieObjString = serieElement.html();
				while(serieObjString.indexOf("\r") != -1) {
					serieObjString = serieObjString.substring(0, serieObjString.indexOf("\r"))+serieObjString.substring(serieObjString.indexOf("\r")+2);
				}
				JSONObject serieObject = new JSONObject(serieObjString);

        		SerieEntity serie = new SerieEntity();
				serie.setCodeHtmlAllocine(arr.get(0).toString());
				if(serieObject.has("name")){
					serie.setTitre(serieObject.getString("name"));
					log.info("processing serie : {}", serie.getTitre());
				}
				if(serieObject.has("numberOfEpisodes")){
					serie.setEpisodes(Integer.parseInt(serieObject.getString("numberOfEpisodes")));
				}
				if(serieObject.has("numberOfSeasons")){
					serie.setSeasons(Integer.parseInt(serieObject.getString("numberOfSeasons")));
				}
				if(serieObject.has("image")){
					serie.setCovPortrait(serieObject.getString("image"));
				}
				if(serieObject.has("description")){
					serie.setSynopsis(serieObject.getString("description"));
					if(serie.getSynopsis().length() >3000) return null;
				}
				List<Genres> genresToSave = new ArrayList<Genres>();
				if(serieObject.get("genre") instanceof String) {
					this.serieService.putGenre(serieObject.getString("genre"), genresToSave);
				}else {
					JSONArray arrJson = serieObject.getJSONArray("genre");
					String[] genres = new String[arrJson.length()];
					for(int i = 0; i < arrJson.length(); i++) {
						genres[i] = arrJson.getString(i);
						this.serieService.putGenre(genres[i], genresToSave);
					}
				}
				serie.setGenre(genresToSave);

				Elements creators = doc.select("div.meta-body-item.meta-body-direction");
				List<String> creatorNames = new ArrayList<>();
				for (Element div : creators) {
					Elements links = div.select("a.dark-grey-link");
					for (Element link : links) {
						creatorNames.add(link.text());
					}
				}
				serie.setRealisateur(creatorNames);

				Elements actors = doc.select("div.meta-body-item.meta-body-actor");
				List<String> actorNames = new ArrayList<>();
				for (Element div : actors) {
					Elements links = div.select("span.dark-grey-link");
					for (Element link : links) {
						actorNames.add(link.text());
					}
				}
				serie.setCasting(actorNames);

				Elements infos = doc.select("div.meta-body-item.meta-body-info");
				for (Element info : infos) {
					String fullText = info.text();
					String[] parts = fullText.split("\\|");

					if (parts.length >= 2) {
						serie.setYear(parts[0].trim());
						serie.setFormatEpisode(parts[1].trim());
					}
				}

				Elements notes = doc.select("div.stareval.stareval-small.stareval-theme-default");
				if(!notes.isEmpty()) {
					Elements note = notes.get(0).select("span.stareval-note");
					serie.setAlloGrade(Float.parseFloat(note.text().replace(",", ".")));
				}
				return serie;
        	}
        }
    }

}
