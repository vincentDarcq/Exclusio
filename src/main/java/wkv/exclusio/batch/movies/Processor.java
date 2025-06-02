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

        		log.info("processing movie : {}", movie.getTitre());
        		List<Genres> genresToSave = new ArrayList<Genres>();
				var genreObj = movieObject.get("genre");

				if (genreObj instanceof String genre) {
					movieService.putGenre(genre, genresToSave);
				} else if (genreObj instanceof JSONArray arrJson) {
					for (int i = 0; i < arrJson.length(); i++) {
						movieService.putGenre(arrJson.getString(i), genresToSave);
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
				String imdbTitle = movie.getTitre().replace("%", "%25").replace(" ", "%20").replace("?", "3F").replace(",", "%2C").replace("#", "%23").replace("$", "%24").replace("&", "%26").replace("è", "%C3%A8");
				movie = imdbService.findImdbInfos(movie, imdbTitle, !movie.getCovPortrait().contains("empty"));
        		if(movieObject.has("director")){
					var realisateurs = new ArrayList<String>();
					var directorObj = movieObject.get("director");

					if (directorObj instanceof JSONObject jsonDirector) {
						var name = jsonDirector.getString("name");
						var index = name.indexOf("&#039;");

						if (index != -1) {
							var part1 = name.substring(0, index);
							var part2 = name.substring(index + 6); // &#039; → 6 caractères
							realisateurs.add(part1);
							realisateurs.add(part2);
							realisateurs.add(part1 + "'" + part2);
						} else {
							realisateurs.add(name);
						}

					} else if (directorObj instanceof JSONArray arrDirectors) {
						for (int i = 0; i < arrDirectors.length(); i++) {
							realisateurs.add(arrDirectors.getJSONObject(i).getString("name"));
						}
					}

					movie.setRealisateur(realisateurs);
        		}
        		if(movieObject.has("actor")) {
					var casting = new ArrayList<String>();
					var actorObj = movieObject.get("actor");

					if (actorObj instanceof JSONObject jsonActor) {
						casting.add(jsonActor.getString("name"));
					} else if (actorObj instanceof JSONArray actors) {
						for (int i = 0; i < actors.length(); i++) {
							var name = actors.getJSONObject(i).getString("name");
							var index = name.indexOf("&#039;");

							var formattedName = (index != -1)
									? name.substring(0, index) + "'" + name.substring(index + 6)
									: name;

							// Préfixe uniquement à partir du deuxième acteur
							if (i > 0) {
								formattedName = ", " + formattedName;
							}

							casting.add(formattedName);
						}
					}

					movie.setCasting(casting);
        		}
				if (movieObject.has("aggregateRating")) {
					var aggregateRating = movieObject.getJSONObject("aggregateRating");
					var ratingStr = aggregateRating.getString("ratingValue").replace(',', '.');
					movie.setAlloGrade(Float.parseFloat(ratingStr));
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
