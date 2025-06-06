package wkv.exclusio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.MovieEntity;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long>, JpaSpecificationExecutor<MovieEntity> {

	Optional<MovieEntity> findById(Long id);

    List<MovieEntity> findByTitre(String titre);
	
	List<MovieEntity> findByTitreContainingIgnoreCase(String titre);

    Page<MovieEntity> findByOrderByAlloGradeDesc(Pageable page);

    @Query(value = """
        SELECT casting
        FROM movie_entity_casting
        GROUP BY casting
        ORDER BY COUNT(*) DESC, casting ASC
        """, nativeQuery = true)
    List<String> findAllActorsWithOccurrences();

    @Query(value = """
        SELECT realisateur
        FROM movie_entity_realisateur
        GROUP BY realisateur
        ORDER BY COUNT(*) DESC, realisateur ASC
        """, nativeQuery = true)
    List<String> findAllDirectorsWithOccurrences();
    
    MovieEntity findFirstByOrderByAlloGradeDesc();

    @Query("SELECT m FROM MovieEntity m WHERE m.covPortrait LIKE %:keyword%")
    List<MovieEntity> findAllWithCovPortraitContaining(@Param("keyword") String keyword);

}
