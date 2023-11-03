package wkv.exclusio.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.repositories.MovieRepository;
import wkv.exclusio.dto.ObjectRequestForMoviesPageWithExclusion;
import wkv.exclusio.entities.Genres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class MovieService {
	
	private static final Logger log = LoggerFactory.getLogger(MovieService.class);
	
	@Autowired
	private MovieRepository movieRepository;

	public List<MovieEntity> getAll(){
		return this.movieRepository.findAll();
	}

	public MovieEntity read(Long id) throws Exception {
		Optional<MovieEntity> movie = this.movieRepository.findById(id);
		if(movie.isPresent()) {
			return movie.get();
		}else {
			throw new Exception("There is no movie for the id : "+id);
		}
	}

	public MovieEntity create(MovieEntity movie) {
		return this.movieRepository.save(movie);
	}

	public MovieEntity readMovie(Long id) throws Exception {
		Optional<MovieEntity> movie = this.movieRepository.findById(id);
		if(movie.isPresent()) {
			return movie.get();
		}else {
			throw new Exception("There is no movie for the id "+id);
		}
	}
	
	public MovieEntity findMovieBySubTitre(String titre) throws Exception {
		Optional<MovieEntity> movie = this.movieRepository.findByTitreContainingIgnoreCase(titre);
		if(movie.isPresent()) {
			return movie.get();
		}else {
			throw new Exception("There is no movie for the sub titre "+titre);
		}
	}

	public Page<MovieEntity> getPageOfBestAlloGradeMovies(int page) {
		log.info("getting page {} of best allo grade movies", page);
        Pageable pageable = PageRequest.of(page, 8);
        return this.movieRepository.findByOrderByAlloGradeDesc(pageable);
    }
	
	public Page<MovieEntity> getPageOfBestAlloGradeMoviesWithExclusions(
			int page, 
			ObjectRequestForMoviesPageWithExclusion requestBody
			) {
		log.info("getting page {} of best allo grade movies with exlusion", page);
		Pageable pageable = PageRequest.of(page, 8);
		return this.movieRepository.findByGenreNotInAndCastingNotInAndRealisateurNotInOrderByAlloGradeDesc(
				requestBody.getGenres(), requestBody.getRealisateurs(), requestBody.getCasting(), pageable);
	}
	
	public MovieEntity getBestAlloMovie() {
		return this.movieRepository.findFirstByOrderByAlloGradeDesc();
	}

	public List<MovieEntity> getMoviesByGenre(Genres genre){
		return this.movieRepository.findByGenre(genre);
	}

	public Page<MovieEntity> getMoviesCaroussel(List<String> titre, Pageable page){
		return this.movieRepository.findByTitreNotInOrderByAlloGradeDesc(page, titre);
	}

	public Page<MovieEntity> getMoviesByListCaroussel(
			List<String> genresS,
			List<String> genresA,
			List<String> reals,
			List<String> actors,
			List<String> titre,
			Pageable page){
		List<Genres> genres = new ArrayList<Genres>();
		genresS.forEach(genre -> { this.putGenre(genre, genres);});
		genresA.forEach(genre -> { this.putGenre(genre, genres);});
		return this.movieRepository.findByTitreNotInAndGenreNotInAndCastingNotInAndRealisateurNotInOrderByAlloGradeDesc(
				titre, genres, reals, actors, page);
	}

	public List<String> getRealisateurs(){
		List<MovieEntity> movies = this.getAll();
		List<String> realisateurs = new ArrayList<String>();
		List<String> realisateur;
		for(MovieEntity movie : movies) {
			realisateur = movie.getRealisateur();
			for(int i = 0; i < realisateur.size(); i++) {
				if(!realisateurs.contains(realisateur.get(i))) {
					realisateurs.add(realisateur.get(i));
				}
			}
		}
		Collections.sort(realisateurs);
		return realisateurs;
	}

	public List<Integer> loadYears(){
		List<MovieEntity> movies = this.getAll();
		List<Integer> years = new ArrayList<Integer>();
		for(MovieEntity movie : movies) {
			if(!years.contains(movie.getYear())) {
				years.add(movie.getYear());
			}
		}
		Collections.sort(years, Collections.reverseOrder());
		return years;
	}

	public List<String> getActors(){
		List<MovieEntity> movies = this.getAll();
		List<String> actors = new ArrayList<String>();
		List<String> casting;
		for(MovieEntity movie : movies) {
			casting = movie.getCasting();
			for(int i = 0; i < casting.size(); i++) {
				if(!actors.contains(casting.get(i))) {
					actors.add(casting.get(i));
				}
			}
		}
		return actors;
	}

	public List<String> getActorsByOccurences(){
		List<String> actors = this.getActors();
		List<String> sortActors = new ArrayList<String>();
		Map<String, Integer> ActorsWithOccurs = new HashMap<String, Integer>();
		List<String> actorsKnow = new ArrayList<String>();
		for(String actor : actors) {
			if(actorsKnow.indexOf(actor) == -1) {
				int occurrences = Collections.frequency(actors, actor);
				ActorsWithOccurs.put(actor, occurrences);
				actorsKnow.add(actor);
			}
		}
		ActorsWithOccurs = this.sortHashMapByValues(ActorsWithOccurs);
		for(String actor : ActorsWithOccurs.keySet()) {
			sortActors.add(actor);
		}
		return sortActors;
	}

	public List<String> getRealsByOccurences(){
		List<MovieEntity> movies = this.getAll();
		List<String> reals = this.getRealisateurs();
		List<String> sortReals = new ArrayList<String>();
		Map<String, Integer> RealsWithOccurs = new HashMap<String, Integer>();
		for(String real : reals) {
			int cpt = 0;
			for(MovieEntity movie : movies) {
				if(movie.getCasting() != null && movie.getCasting().indexOf(real) != -1) {
					cpt++;
				}
			}
			RealsWithOccurs.put(real, cpt);
		}
		RealsWithOccurs = this.sortHashMapByValues(RealsWithOccurs);
		for(String real : RealsWithOccurs.keySet()) {
			sortReals.add(real);
		}
		return sortReals;
	}

	public LinkedHashMap<String, Integer> sortHashMapByValues(
			Map<String, Integer> passedMap) {
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
		Collections.sort(mapValues, Collections.reverseOrder());
		Collections.sort(mapKeys);

		LinkedHashMap<String, Integer> sortedMap =
				new LinkedHashMap<String, Integer>();

		Iterator<Integer> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Integer val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				Integer comp1 = passedMap.get(key);
				Integer comp2 = val;

				if (comp1 == comp2) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

	public void putGenre(String genre, List<Genres> genresToSave) {
		switch(genre) {
			case "ROMANCE":
				genresToSave.add(Genres.ROMANCE);
				break;
			case "DRAME":
				genresToSave.add(Genres.DRAME);
				break;
			case "COMEDIE":
				genresToSave.add(Genres.COMEDIE);
				break;
			case "THRILLER":
				genresToSave.add(Genres.THRILLER);
				break;
			case "SCIENCE_FICTION":
				genresToSave.add(Genres.SCIENCE_FICTION);
				break;
			case "ANIMATION":
				genresToSave.add(Genres.ANIMATION);
				break;
			case "JEUNESSE":
				genresToSave.add(Genres.JEUNESSE);
				break;
			case "AVENTURE":
				genresToSave.add(Genres.AVENTURE);
				break;
			case "HISTOIRE":
				genresToSave.add(Genres.HISTOIRE);
				break;
			case "ACTION":
				genresToSave.add(Genres.ACTION);
				break;
			case "FANTASY":
				genresToSave.add(Genres.FANTASY);
				break;
			case "EPOUVANTE_HORREUR":
				genresToSave.add(Genres.EPOUVANTE_HORREUR);
				break;
			case "POLICIER":
				genresToSave.add(Genres.POLICIER);
				break;
			case "BIOPIC":
				genresToSave.add(Genres.BIOPIC);
				break;
			case "GUERRE":
				genresToSave.add(Genres.GUERRE);
				break;
			case "DOCUMENTAIRE":
				genresToSave.add(Genres.DOCUMENTAIRE);
				break;
			case "MUSICAL":
				genresToSave.add(Genres.MUSICAL);
				break;
			case "Comédie dramatique":
				genresToSave.add(Genres.COMEDIE_DRAMATIQUE);
				break;
			case "Erotique":
				genresToSave.add(Genres.EROTIQUE);
				break;
			case "Espionnage":
				genresToSave.add(Genres.ESPIONNAGE);
				break;
			case "Western":
				genresToSave.add(Genres.WESTERN);
				break;
			case "Comédie musicale":
				genresToSave.add(Genres.COMEDIE_MUSICALE);
				break;
			case "Judiciaire":
				genresToSave.add(Genres.JUDICIAIRE);
				break;
			case "Arts Martiaux":
				genresToSave.add(Genres.ARTS_MARTIAUX);
				break;
		}
	}
}
