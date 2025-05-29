package wkv.exclusio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wkv.exclusio.entities.SerieEntity;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<SerieEntity, Long>, JpaSpecificationExecutor<SerieEntity> {
    List<SerieEntity> findByTitre(String titre);

    SerieEntity findFirstByOrderByAlloGradeDesc();

    List<SerieEntity> findByTitreContainingIgnoreCase(String titre);

    @Query(value = """
        SELECT casting
        FROM serie_entity_casting
        GROUP BY casting
        ORDER BY COUNT(*) DESC, casting ASC
        """, nativeQuery = true)
    List<String> findAllActorsWithOccurrences();

    @Query(value = """
        SELECT realisateur
        FROM serie_entity_realisateur
        GROUP BY realisateur
        ORDER BY COUNT(*) DESC, realisateur ASC
        """, nativeQuery = true)
    List<String> findAllDirectorsWithOccurrences();
}
