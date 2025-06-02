package wkv.exclusio.controllers;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wkv.exclusio.entities.MovieEntity;
import wkv.exclusio.services.ImdbService;
import wkv.exclusio.services.MovieService;

import java.util.List;


@Controller
@RequestMapping("batch")
public class BatchController {
	
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private MovieService movieService;
	@Autowired
	private ImdbService imdbService;
	
	@Autowired
	@Qualifier("alloJobMovies")
	private Job jobMovies;

	@Autowired
	@Qualifier("alloJobSeries")
	private Job jobSeries;
	
	@GetMapping("/movies")
	@ResponseBody
	public ResponseEntity<Object> launchMoviesJob() {
		try {
			jobLauncher.run(jobMovies, new JobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("batch launched");
	}

	@GetMapping("/series")
	@ResponseBody
	public ResponseEntity<Object> launchSeriesJob() {
		try {
			jobLauncher.run(jobSeries, new JobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("batch launched");
	}

	@GetMapping("/fillEmptyCovMovies")
	@ResponseBody
	public ResponseEntity<Object> fillEmptyCovMovies() {
		List<MovieEntity> movies = this.movieService.findMovieWithoutCov();
		for (MovieEntity movie : movies) {
			MovieEntity movieWithImdbCov;
			if (movie.getCodeHtmlImdb() == null) {
				movieWithImdbCov = this.imdbService.findImdbInfos(movie, movie.getTitre(), false);
			} else {
				movieWithImdbCov = this.imdbService.findImdbGradeAndCov(movie, false);
			}
			if(movieWithImdbCov != null) {
				this.movieService.save(movieWithImdbCov);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("batch launched");
	}

}
