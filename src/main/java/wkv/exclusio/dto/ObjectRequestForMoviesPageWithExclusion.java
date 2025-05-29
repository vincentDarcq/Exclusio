package wkv.exclusio.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wkv.exclusio.entities.Genres;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRequestForMoviesPageWithExclusion {
	List<Genres> genres;
	List<String> casting;
	List<String> realisateurs;
}
