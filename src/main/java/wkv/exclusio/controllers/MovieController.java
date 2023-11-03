package wkv.exclusio.controllers;

import java.util.List;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import wkv.exclusio.dto.ObjectRequestForMoviesPageWithExclusion;
import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.services.MovieService;

@Controller
@RequestMapping("movies")
public class MovieController {
	
	private static final Logger log = LoggerFactory.getLogger(MovieController.class);

	@Autowired
	private MovieService movieService;
	
	@PostMapping
	public MovieEntity create(@RequestBody MovieEntity movie) {
		return this.movieService.create(movie);
	}

	@GetMapping("/{page}")
	@ResponseBody
	public Page<MovieEntity> listMovies(@PathVariable int page) {
		return this.movieService.getPageOfBestAlloGradeMovies(page);
	}	
	
	@GetMapping("/bestAlloMovie")
	@ResponseBody
	public MovieEntity bestAlloMovie() {
		return this.movieService.getBestAlloMovie();
	}
	
	@GetMapping("/findSubTitle/{titre}")
	@ResponseBody
	public MovieEntity findSubTitle(@PathVariable String titre) {
		log.info("titre cherché : {}", titre);
		return this.movieService.getBestAlloMovie();
	}
	
	@PostMapping("/exclusions/{page}")
	@ResponseBody
	public Page<MovieEntity> listMoviesWithExclusions(
			@PathVariable int page, 
			@RequestBody ObjectRequestForMoviesPageWithExclusion requestBody
			) {
		return this.movieService.getPageOfBestAlloGradeMoviesWithExclusions(page, requestBody);
	}	
	
	@GetMapping("/movie/{id}")
	@ResponseBody
	public MovieEntity read(@PathVariable Long id) throws Exception {
		MovieEntity movie = this.movieService.read(id);
		Hibernate.initialize(movie);
		return movie;
	}

	@GetMapping("/acteurs")
	@ResponseBody
	public List<String> listTenActors() {
		return this.movieService.getActorsByOccurences();
	}
	
	@GetMapping("/realisateurs")
	@ResponseBody
	public List<String> listTenReals() {
		return this.movieService.getRealsByOccurences();
	}

	@GetMapping("/byGenre/{genre}")
	@ResponseBody
	public List<MovieEntity> listMoviesByGenre(@PathVariable Genres genre){
		return this.movieService.getMoviesByGenre(genre);
	}
}
