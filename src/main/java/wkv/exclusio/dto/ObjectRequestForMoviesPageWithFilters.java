package wkv.exclusio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wkv.exclusio.entities.Genres;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRequestForMoviesPageWithFilters {
    List<Genres> genresToExclude;
    List<String> castingToExclude;
    List<String> realisateursToExclude;
    List<Genres> genresToInclude;
    List<String> castingToInclude;
    List<String> realisateursToInclude;
}
