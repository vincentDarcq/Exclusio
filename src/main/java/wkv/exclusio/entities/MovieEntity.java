package wkv.exclusio.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="movies")
@Data
public class MovieEntity extends AbstractBaseEntity{
	
	@Column()
	private String titre;

    @Column(length = 3000)
    private String synopsis;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Genres> genre;

    @Column()
    @ElementCollection
    private List<String> casting;

    @Column()
    @ElementCollection
    private List<String> realisateur;

    @Column(name="cov_portrait")
    private String covPortrait;

    @Column(name="cov_paysage")
    private String covPaysage;

    private Float grade;

    @Column(name="allo_grade")
    private Float alloGrade;

    @Column(name="imdb_grade")
    private Float imdbGrade;

    private int year;

    private String pegi;

    private String avertissement;

    private String time;

    @Column(name="code_html_allocine")
    private String codeHtmlAllocine;

    @Column(name="code_html_imdb")
    private String codeHtmlImdb;

    @Override
    public String toString() {
        return "MovieEntity [\ntitre=" + titre + ", \nsynopsis=" + synopsis + ", \ngenre=" + genre
                + ", \ncasting=" + casting + ", \nrealisateur=" + realisateur + ", \ncovPortrait=" + covPortrait
                + ", \ncovPaysage=" + covPaysage + ", \ngrade=" + grade + ", \nalloGrade=" + alloGrade + ", \nimdbGrade="
                + imdbGrade + ", \nyear=" + year + ", \npegi=" + pegi + ", \navertissement=" + avertissement + ", \ntime="
                + time + "\n]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        MovieEntity that = (MovieEntity) o;
        return Objects.equals(titre, that.titre) &&
                year == that.year &&
                Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(codeHtmlAllocine, that.codeHtmlAllocine) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titre, year, synopsis, codeHtmlAllocine, time);
    }

}
