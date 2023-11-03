package wkv.exclusio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.MovieEntity;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long>{

	public Optional<MovieEntity> findById(Long id);
	
	public Optional<MovieEntity> findByTitreContainingIgnoreCase(String titre);

    public List<MovieEntity> findByGenre(Genres genre);

    public Page<MovieEntity> findByOrderByAlloGradeDesc(Pageable page);
    
    public Page<MovieEntity> findByGenreNotInAndCastingNotInAndRealisateurNotInOrderByAlloGradeDesc(
    		List<Genres> genres, List<String> realisateurs,List<String> casting, Pageable page);
    
    public MovieEntity findFirstByOrderByAlloGradeDesc();    

    public MovieEntity findFirstByGenreNotInAndCastingNotInAndRealisateurNotInOrderByAlloGradeDesc(
            List<Genres> genres, List<String> realisateurs,List<String> casting);

    public Page<MovieEntity> findByTitreNotInOrderByAlloGradeDesc(Pageable page, List<String> titre);

    public Page<MovieEntity> findByTitreNotInAndGenreNotInAndCastingNotInAndRealisateurNotInOrderByAlloGradeDesc(
            List<String> titre, List<Genres> genres, List<String> realisateurs,List<String> casting, Pageable page);
}
