package wkv.exclusio.services;

import java.util.*;

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
import wkv.exclusio.repositories.Specifications;


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
	
	public List<MovieEntity> findMovieBySubTitre(String titre) {
		return this.movieRepository.findByTitreContainingIgnoreCase(titre);
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
		Pageable pageable = PageRequest.of(page, 24);
		return movieRepository.findAll(
				Specifications.movieWithExclusions(
					requestBody.getGenres(),
					requestBody.getRealisateurs(),
					requestBody.getCasting()),
					pageable
		);
	}

	public Page<MovieEntity> getPageOfBestAlloGradeMoviesWithInclusions(
			int page,
			ObjectRequestForMoviesPageWithExclusion requestBody
	) {
		log.info("getting page {} of best allo grade movies with inclusion", page);
		Pageable pageable = PageRequest.of(page, 24);
		return movieRepository.findAll(
				Specifications.movieWithInclusions(
						requestBody.getGenres(),
						requestBody.getRealisateurs(),
						requestBody.getCasting()),
						pageable
		);
	}
	
	public MovieEntity getBestAlloMovie() {
		return this.movieRepository.findFirstByOrderByAlloGradeDesc();
	}

	public List<String> getRealisateursByOccurences(){
		return this.movieRepository.findAllDirectorsWithOccurrences();
	}

	public List<String> getActorsByOccurences(){
		return this.movieRepository.findAllActorsWithOccurrences();
	}

	public void deleteMovie(MovieEntity movie) {
		System.out.println("delete " + movie.getTitre());
		this.movieRepository.delete(movie);
	}

	public void putGenre(String genre, List<Genres> genresToSave) {
		switch(genre) {
			case "Romance":
			case "ROMANCE":
				genresToSave.add(Genres.ROMANCE);
				break;
			case "Drame":
			case "DRAME":
				genresToSave.add(Genres.DRAME);
				break;
			case "Comédie":
			case "COMEDIE":
				genresToSave.add(Genres.COMEDIE);
				break;
			case "Thriller":
			case "THRILLER":
				genresToSave.add(Genres.THRILLER);
				break;
			case "Science Fiction":
			case "SCIENCE_FICTION":
				genresToSave.add(Genres.SCIENCE_FICTION);
				break;
			case "Animation":
			case "ANIMATION":
				genresToSave.add(Genres.ANIMATION);
				break;
			case "JEUNESSE":
				genresToSave.add(Genres.JEUNESSE);
				break;
			case "Famille":
				genresToSave.add(Genres.FAMILLE);
				break;
			case "Aventure":
			case "AVENTURE":
				genresToSave.add(Genres.AVENTURE);
				break;
			case "Historique":
			case "HISTOIRE":
				genresToSave.add(Genres.HISTOIRE);
				break;
			case "Action":
			case "ACTION":
				genresToSave.add(Genres.ACTION);
				break;
			case "Fantastique":
			case "FANTASY":
				genresToSave.add(Genres.FANTASY);
				break;
			case "Epouvante-horreur":
			case "EPOUVANTE_HORREUR":
				genresToSave.add(Genres.EPOUVANTE_HORREUR);
				break;
			case "Policier":
			case "POLICIER":
				genresToSave.add(Genres.POLICIER);
				break;
			case "Biopic":
			case "BIOPIC":
				genresToSave.add(Genres.BIOPIC);
				break;
			case "Guerre":
			case "GUERRE":
				genresToSave.add(Genres.GUERRE);
				break;
			case "Documentaire":
			case "DOCUMENTAIRE":
				genresToSave.add(Genres.DOCUMENTAIRE);
				break;
			case "Musical":
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
			case "Bollywood":
				genresToSave.add(Genres.BOLLYWOOD);
				break;
			case "Divers":
				genresToSave.add(Genres.DIVERS);
				break;
			case "Expérimental":
				genresToSave.add(Genres.EXPERIMENTAL);
				break;
			case "Péplum":
				genresToSave.add(Genres.PEPLUM);
				break;
			case "Évènement Sportif":
				genresToSave.add(Genres.EVENEMENT_SPORTIF);
				break;
			case "Concert":
				genresToSave.add(Genres.CONCERT);
				break;
			default:
				log.info("Unknown genre {}", genre);
		}
	}
}
