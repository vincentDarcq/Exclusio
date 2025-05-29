package wkv.exclusio.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wkv.exclusio.dto.ObjectRequestForMoviesPageWithExclusion;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.services.SerieService;

import java.util.List;

@Controller
@RequestMapping("series")
public class SerieController {
    private static final Logger log = LoggerFactory.getLogger(SerieController.class);

    @Autowired
    private SerieService serieService;

    @GetMapping("/findSubTitle/{titre}")
    @ResponseBody
    public List<SerieEntity> findSubTitle(@PathVariable String titre) {
        log.info("titre cherché : {}", titre);
        try {
            return this.serieService.findMovieBySubTitre(titre);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/exclusions/{page}")
    @ResponseBody
    public Page<SerieEntity> listMoviesWithExclusions(
            @PathVariable int page,
            @RequestBody ObjectRequestForMoviesPageWithExclusion requestBody
    ) {
        return this.serieService.getPageOfBestAlloGradeMoviesWithExclusions(page, requestBody);
    }

    @PostMapping("/inclusions/{page}")
    @ResponseBody
    public Page<SerieEntity> listMoviesWithInclusions(
            @PathVariable int page,
            @RequestBody ObjectRequestForMoviesPageWithExclusion requestBody
    ) {
        return this.serieService.getPageOfBestAlloGradeMoviesWithInclusions(page, requestBody);
    }

    @GetMapping("/acteurs")
    @ResponseBody
    public List<String> listActors() {
        return this.serieService.getActorsByOccurences();
    }

    @GetMapping("/realisateurs")
    @ResponseBody
    public List<String> listReals() {
        return this.serieService.getRealisateursByOccurences();
    }

    @GetMapping("/number")
    @ResponseBody
    public int getMoviesNumber() {
        return this.serieService.getAll().size();
    }
}
