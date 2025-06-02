package wkv.exclusio.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import wkv.exclusio.dto.ObjectRequestForMoviesPageWithFilters;
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
		return this.movieService.save(movie);
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
	public List<MovieEntity> findSubTitle(@PathVariable String titre) {
		log.info("titre cherché : {}", titre);
        try {
            return this.movieService.findMovieBySubTitre(titre);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@PostMapping("/filters/{page}")
	@ResponseBody
	public Page<MovieEntity> listMoviesWithFilters(
			@PathVariable int page,
			@RequestBody ObjectRequestForMoviesPageWithFilters requestBody
	) {
		return this.movieService.getPageOfBestAlloGradeMoviesWithFilters(page, requestBody);
	}
	
	@PostMapping("/exclusions/{page}")
	@ResponseBody
	public Page<MovieEntity> listMoviesWithExclusions(
			@PathVariable int page, 
			@RequestBody ObjectRequestForMoviesPageWithExclusion requestBody
			) {
		return this.movieService.getPageOfBestAlloGradeMoviesWithExclusions(page, requestBody);
	}

	@PostMapping("/inclusions/{page}")
	@ResponseBody
	public Page<MovieEntity> listMoviesWithInclusions(
			@PathVariable int page,
			@RequestBody ObjectRequestForMoviesPageWithExclusion requestBody
	) {
		return this.movieService.getPageOfBestAlloGradeMoviesWithInclusions(page, requestBody);
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
	public List<String> listActors() {
		return this.movieService.getActorsByOccurences();
	}
	
	@GetMapping("/realisateurs")
	@ResponseBody
	public List<String> listReals() {
		return this.movieService.getRealisateursByOccurences();
	}

	@GetMapping("/number")
	@ResponseBody
	public int getMoviesNumber() {
		return this.movieService.getAll().size();
	}

	@GetMapping("/deleteDoublons")
	public void deleteDoublons(){
		List<MovieEntity> list = this.movieService.getAll();
		Set<MovieEntity> seen = new HashSet<>();
		Set<MovieEntity> duplicates = list.stream()
				.filter(e -> !seen.add(e))
				.collect(Collectors.toSet());

		for (MovieEntity movie : duplicates) {
			this.movieService.deleteMovie(movie);
		}
	}
}
