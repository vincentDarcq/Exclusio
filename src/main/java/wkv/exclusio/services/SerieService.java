package wkv.exclusio.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wkv.exclusio.dto.ObjectRequestForMoviesPageWithExclusion;
import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.SerieEntity;
import wkv.exclusio.repositories.SerieRepository;
import wkv.exclusio.repositories.Specifications;

import java.util.List;

@Service
public class SerieService {
    private static final Logger log = LoggerFactory.getLogger(SerieService.class);

    @Autowired
    private SerieRepository serieRepository;

    public List<SerieEntity> getAll(){
        return this.serieRepository.findAll();
    }

    public SerieEntity create(SerieEntity serie) {
        return this.serieRepository.save(serie);
    }

    public SerieEntity getBestAlloMovie() {
        return this.serieRepository.findFirstByOrderByAlloGradeDesc();
    }

    public List<SerieEntity> findMovieBySubTitre(String titre) {
        return this.serieRepository.findByTitreContainingIgnoreCase(titre);
    }

    public Page<SerieEntity> getPageOfBestAlloGradeMoviesWithExclusions(
            int page,
            ObjectRequestForMoviesPageWithExclusion requestBody
    ) {
        log.info("getting page {} of best allo grade series with exlusion", page);
        Pageable pageable = PageRequest.of(page, 24);
        return serieRepository.findAll(
                Specifications.serieWithExclusions(
                    requestBody.getGenres(),
                    requestBody.getRealisateurs(),
                    requestBody.getCasting()),
                    pageable
        );
    }

    public Page<SerieEntity> getPageOfBestAlloGradeMoviesWithInclusions(
            int page,
            ObjectRequestForMoviesPageWithExclusion requestBody
    ) {
        log.info("getting page {} of best allo grade movies with inclusion", page);
        Pageable pageable = PageRequest.of(page, 24);
        return serieRepository.findAll(
                Specifications.serieWithInclusions(
                    requestBody.getGenres(),
                    requestBody.getRealisateurs(),
                    requestBody.getCasting()),
                    pageable
        );
    }

    public List<String> getRealisateursByOccurences(){
        return this.serieRepository.findAllDirectorsWithOccurrences();
    }

    public List<String> getActorsByOccurences(){
        return this.serieRepository.findAllActorsWithOccurrences();
    }

    public void putGenre(String genre, List<Genres> genresToSave) {
        switch (genre) {
            case "Web Séries":
                genresToSave.add(Genres.WEB_SERIES);
                break;
            case "Dessin Animé":
                genresToSave.add(Genres.DESSIN_ANIME);
                break;
            case "Classique":
                genresToSave.add(Genres.CLASSIQUE);
                break;
            case "Feuilleton":
                genresToSave.add(Genres.FEUILLETON);
                break;
            case "Médical":
                genresToSave.add(Genres.MEDICAL);
                break;
            case "Romance":
            case "ROMANCE":
                genresToSave.add(Genres.ROMANCE);
                break;
            case "Drama":
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