package wkv.exclusio.repositories;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import wkv.exclusio.entities.Genres;
import wkv.exclusio.entities.MovieEntity;

import java.util.ArrayList;
import java.util.List;
import jakarta. persistence. criteria. Predicate;
import wkv.exclusio.entities.SerieEntity;

public class Specifications {
    public static Specification<MovieEntity> movieWithFilters(
            List<Genres> genresToInclude, List<String> realisateursToInclude, List<String> castingToInclude,
            List<Genres> genresNotToInclude, List<String> realisateursNotToInclude, List<String> castingNotToInclude
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);
            boolean hasAtLeastOneConditionToInclude = false;

            if (genresNotToInclude != null && !genresNotToInclude.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, Genres> genreJoin = subRoot.join("genre");

                subquery.select(subRoot.get("id"))
                        .where(genreJoin.in(genresNotToInclude));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (realisateursNotToInclude != null && !realisateursNotToInclude.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> realJoin = subRoot.join("realisateur");

                subquery.select(subRoot.get("id"))
                        .where(realJoin.in(realisateursNotToInclude));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (castingNotToInclude != null && !castingNotToInclude.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> castJoin = subRoot.join("casting");

                subquery.select(subRoot.get("id"))
                        .where(castJoin.in(castingNotToInclude));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (genresToInclude != null && !genresToInclude.isEmpty()) {
                Join<MovieEntity, Genres> genreJoin = root.join("genre");
                predicates.add(genreJoin.in(genresToInclude));
                hasAtLeastOneConditionToInclude = true;
            }

            if (realisateursToInclude != null && !realisateursToInclude.isEmpty()) {
                Join<MovieEntity, String> realJoin = root.join("realisateur");
                predicates.add(realJoin.in(realisateursToInclude));
                hasAtLeastOneConditionToInclude = true;
            }

            if (castingToInclude != null && !castingToInclude.isEmpty()) {
                Join<MovieEntity, String> castJoin = root.join("casting");
                predicates.add(castJoin.in(castingToInclude));
                hasAtLeastOneConditionToInclude = true;
            }

            predicates.add(cb.isNotNull(root.get("alloGrade")));
            query.orderBy(cb.desc(root.get("alloGrade")));

            if (!hasAtLeastOneConditionToInclude) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<MovieEntity> movieWithExclusions(List<Genres> genres, List<String> realisateurs, List<String> castings) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            if (genres != null && !genres.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, Genres> genreJoin = subRoot.join("genre");

                subquery.select(subRoot.get("id"))
                        .where(genreJoin.in(genres));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (realisateurs != null && !realisateurs.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> realJoin = subRoot.join("realisateur");

                subquery.select(subRoot.get("id"))
                        .where(realJoin.in(realisateurs));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (castings != null && !castings.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> castJoin = subRoot.join("casting");

                subquery.select(subRoot.get("id"))
                        .where(castJoin.in(castings));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            predicates.add(cb.isNotNull(root.get("alloGrade")));
            query.orderBy(cb.desc(root.get("alloGrade")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<MovieEntity> movieWithInclusions(List<Genres> genres, List<String> realisateurs, List<String> castings) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            boolean hasAtLeastOneCondition = false;

            if (genres != null && !genres.isEmpty()) {
                Join<MovieEntity, Genres> genreJoin = root.join("genre");
                predicates.add(genreJoin.in(genres));
                hasAtLeastOneCondition = true;
            }

            if (realisateurs != null && !realisateurs.isEmpty()) {
                Join<MovieEntity, String> realJoin = root.join("realisateur");
                predicates.add(realJoin.in(realisateurs));
                hasAtLeastOneCondition = true;
            }

            if (castings != null && !castings.isEmpty()) {
                Join<MovieEntity, String> castJoin = root.join("casting");
                predicates.add(castJoin.in(castings));
                hasAtLeastOneCondition = true;
            }

            predicates.add(cb.isNotNull(root.get("alloGrade")));
            query.orderBy(cb.desc(root.get("alloGrade")));

            if (!hasAtLeastOneCondition) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SerieEntity> serieWithExclusions(List<Genres> genres, List<String> realisateurs, List<String> castings) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            if (genres != null && !genres.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, Genres> genreJoin = subRoot.join("genre");

                subquery.select(subRoot.get("id"))
                        .where(genreJoin.in(genres));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (realisateurs != null && !realisateurs.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> realJoin = subRoot.join("realisateur");

                subquery.select(subRoot.get("id"))
                        .where(realJoin.in(realisateurs));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            if (castings != null && !castings.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
                Join<MovieEntity, String> castJoin = subRoot.join("casting");

                subquery.select(subRoot.get("id"))
                        .where(castJoin.in(castings));

                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            predicates.add(cb.isNotNull(root.get("alloGrade")));
            query.orderBy(cb.desc(root.get("alloGrade")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SerieEntity> serieWithInclusions(List<Genres> genres, List<String> realisateurs, List<String> castings) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            boolean hasAtLeastOneCondition = false;

            if (genres != null && !genres.isEmpty()) {
                Join<MovieEntity, Genres> genreJoin = root.join("genre");
                predicates.add(genreJoin.in(genres));
                hasAtLeastOneCondition = true;
            }

            if (realisateurs != null && !realisateurs.isEmpty()) {
                Join<MovieEntity, String> realJoin = root.join("realisateur");
                predicates.add(realJoin.in(realisateurs));
                hasAtLeastOneCondition = true;
            }

            if (castings != null && !castings.isEmpty()) {
                Join<MovieEntity, String> castJoin = root.join("casting");
                predicates.add(castJoin.in(castings));
                hasAtLeastOneCondition = true;
            }

            predicates.add(cb.isNotNull(root.get("alloGrade")));
            query.orderBy(cb.desc(root.get("alloGrade")));

            if (!hasAtLeastOneCondition) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
