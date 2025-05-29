package wkv.exclusio.batch.movies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.services.ImdbService;
import wkv.exclusio.services.MovieService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@StepScope
public class Processor implements ItemProcessor<Map<Integer, String>, MovieEntity>{
	
	private static final Logger log = LoggerFactory.getLogger(Processor.class);
	@Autowired
	private MovieService movieService;
	@Autowired
	private ImdbService imdbService;
	
    @Override
    public MovieEntity process(Map<Integer, String> content) {
		try {
			return parseContent(content);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
        return null;
    }
    
    private MovieEntity parseContent(Map<Integer, String> content) {
    	List<Integer> arr = new ArrayList<>(content.keySet());
    	Document doc = Jsoup.parse(content.get(arr.get(0)));
    	
    	Element body = doc.body();
        if(Strings.isBlank(content.get(arr.get(0)))) {
        	log.info("page inexistante");
        	return null;
        }else {
        	Element movieElement = body
        			.getElementsByAttributeValue("type", "application/ld+json")
                    .first();
        	
        	if(movieElement == null) {
        		log.info("page existante mais sans data json");
        		return null;
        	}else {
        		String movieObjString = movieElement.html();

        		MovieEntity movie = new MovieEntity();

        		movie.setCodeHtmlAllocine(arr.get(0).toString());

        		Elements possiblesProductionYear = body.getElementsByAttributeValue("class", "item");
        		for(Element element : possiblesProductionYear) {
        			if(element.childrenSize() > 0 && element.child(0).html().contains("Année de production")) {
        				if(!Strings.isEmpty(element.child(1).html())) {        					
        					movie.setYear(Integer.parseInt(element.child(1).html()));
        				}
        			}
        		}
				Elements avertissement_pegi = body.getElementsByAttributeValue("class", "certificate-text");
        		if(avertissement_pegi.size() > 1) {
        			if(avertissement_pegi.get(0).html().contains("Interdit")) {
        				movie = fillPegi(movie, avertissement_pegi.get(0));
        			}else {
        				movie = fillPegi(movie, avertissement_pegi.get(1));
        			}
        			movie.setAvertissement("Avertissement : des scènes, des propos ou des images peuvent heurter la sensibilité des spectateurs");
        		}else if(avertissement_pegi.size() == 1) {
        			if(avertissement_pegi.get(0).html().contains("Interdit")) {
        				movie = fillPegi(movie, avertissement_pegi.get(0));
        			}else {
        				movie.setAvertissement("Avertissement : des scènes, des propos ou des images peuvent heurter la sensibilité des spectateurs");
        			}
        		}
        		while(movieObjString.indexOf("\r") != -1) {
        			movieObjString = movieObjString.substring(0, movieObjString.indexOf("\r"))+movieObjString.substring(movieObjString.indexOf("\r")+2);
        		}
        		JSONObject movieObject = new JSONObject(movieObjString);
        		movie.setTitre(movieObject.getString("name"));
				String imdbTitle = movie.getTitre().replace("%", "%25").replace(" ", "%20").replace("?", "3F").replace(",", "%2C").replace("#", "%23").replace("$", "%24").replace("&", "%26");
				movie = imdbService.findGrade(movie, imdbTitle);
        		log.info("processing movie : {}", movie.getTitre());
        		List<Genres> genresToSave = new ArrayList<Genres>();
        		if(movieObject.get("genre") instanceof String) {
        			this.movieService.putGenre(movieObject.getString("genre"), genresToSave);
        		}else {
        			JSONArray arrJson = movieObject.getJSONArray("genre");
        			String[] genres = new String[arrJson.length()];
        			for(int i = 0; i < arrJson.length(); i++) {
        				genres[i] = arrJson.getString(i);
        				this.movieService.putGenre(genres[i], genresToSave);
        			}
        		}
        		movie.setGenre(genresToSave);
        		if(movieObject.has("duration")){
        			String duration = movieObject.getString("duration");
        			String heure = "";
        			if(duration.contains("01H")) {
        				heure = "60";
        			}else if(duration.contains("02H")) {
        				heure = "120";
        			}else if(duration.contains("03H")) {
        				heure = "180";
        			}else if(duration.contains("04H")) {
        				heure = "240";
        			}
        			int index = duration.indexOf('H');
        			int min = Integer.parseInt(duration.substring(index+1, index+3));
        			int duree = 0;
        			if(!heure.equals("")){
        				duree = Integer.parseInt(heure) + min;
        			}else{
        				duree = min;
        			}
        			movie.setTime(duree+"min");
        		}
        		if(movieObject.has("description")){
        			movie.setSynopsis(movieObject.getString("description"));
        		}
        		if(movieObject.has("image")) {
        			try {        				
        				JSONObject image = movieObject.getJSONObject("image");
        				movie.setCovPortrait(image.getString("url"));    				
        			}catch(JSONException e) {
        				log.info("impossible d'extraire l'image avec l'objet: {}", movieObject);
        			}
        		}
        		if(movieObject.has("director")){
        			List<String> realisateurs = new ArrayList<String>();
        			if(movieObject.get("director") instanceof JSONObject){
        				JSONObject jsonDirector = movieObject.getJSONObject("director");
        				int indexVirguleDirector = jsonDirector.getString("name").indexOf("&#039;");
        				if(indexVirguleDirector != -1) {
        					realisateurs.add(jsonDirector.getString("name").substring(0, indexVirguleDirector));
        					realisateurs.add(jsonDirector.getString("name").substring(indexVirguleDirector+6));
        					realisateurs.add(
        							jsonDirector.getString("name").substring(0, indexVirguleDirector) + "'" +
        									jsonDirector.getString("name").substring(indexVirguleDirector+6));
        				}else {
        					realisateurs.add(jsonDirector.getString("name"));
        				}
        				movie.setRealisateur(realisateurs);
        			}else {
        				JSONArray arrDirectors = movieObject.getJSONArray("director");
        				for(int i = 0; i < arrDirectors.length(); i++) {
        					realisateurs.add(arrDirectors.getJSONObject(i).getString("name"));
        				}
        				movie.setRealisateur(realisateurs);
        			}
        		}
        		if(movieObject.has("actor")) {
        			List<String> casting = new ArrayList<String>();
        			if(movieObject.get("actor") instanceof JSONObject){
        				JSONObject jsonActor = movieObject.getJSONObject("actor");
        				casting.add(jsonActor.getString("name"));
        				movie.setCasting(casting);
        			}else {
        				JSONArray actors = new JSONArray(movieObject.getJSONArray("actor"));
        				for (int i = 0; i < actors.length(); i++) {
        					JSONObject actor = actors.getJSONObject(i);
        					int indexVirguleActor = actor.getString("name").indexOf("&#039;");
        					if (i == 0) {
        						if (indexVirguleActor != -1) {
        							casting.add(
        									actor.getString("name").substring(0, indexVirguleActor) + "'" +
        											actor.getString("name").substring(indexVirguleActor + 6));
        						} else {
        							casting.add(actor.getString("name"));
        						}
        					} else {
        						if (indexVirguleActor != -1) {
        							casting.add(", " +
        									actor.getString("name").substring(0, indexVirguleActor) + "'" +
        									actor.getString("name").substring(indexVirguleActor + 6));
        						} else {
        							casting.add(actor.getString("name"));
        						}
        					}
        				}
        				movie.setCasting(casting);
        			}
        		}
        		if(movieObject.has("aggregateRating")) {
        			JSONObject aggregateRating = movieObject.getJSONObject("aggregateRating");
        			int indexVirgule = aggregateRating.getString("ratingValue").indexOf(',');
        			String note = aggregateRating.getString("ratingValue").substring(0, indexVirgule) + "." + aggregateRating.getString("ratingValue").substring(indexVirgule + 1);
        			movie.setAlloGrade(Float.parseFloat(note));
        		}
        		return movie;
        	}
        }
    }
    
    private MovieEntity fillPegi(MovieEntity movie, Element content) {
		int indexInterdit = content.html().indexOf("Interdit");
        movie.setPegi(content.html().substring(indexInterdit+11, indexInterdit+13)+"+");
    	return movie;
    }

}
