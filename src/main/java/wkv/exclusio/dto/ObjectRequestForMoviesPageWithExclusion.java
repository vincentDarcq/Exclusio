package wkv.exclusio.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import wkv.exclusio.entities.Genres;

@NoArgsConstructor
@AllArgsConstructor
public class ObjectRequestForMoviesPageWithExclusion {

	List<Genres> genres;
	
	List<String> casting;
	
	List<String> realisateurs;

	public List<Genres> getGenres() {
		return genres;
	}

	public void setGenres(List<Genres> genres) {
		this.genres = genres;
	}

	public List<String> getCasting() {
		return casting;
	}

	public void setCasting(List<String> casting) {
		this.casting = casting;
	}

	public List<String> getRealisateurs() {
		return realisateurs;
	}

	public void setRealisateurs(List<String> realisateurs) {
		this.realisateurs = realisateurs;
	}
	
	
}
