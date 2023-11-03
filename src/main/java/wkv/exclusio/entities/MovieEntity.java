package wkv.exclusio.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="movies")
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

    public String getCodeHtmlImdb() { return codeHtmlImdb;	}

    public void setCodeHtmlImdb(String codeHtmlImdb) { this.codeHtmlImdb = codeHtmlImdb; }

    public String getCodeHtmlAllocine() { return codeHtmlAllocine;	}

    public void setCodeHtmlAllocine(String codeHtmlAllocine) { this.codeHtmlAllocine = codeHtmlAllocine;}

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<Genres> getGenre() {
        return genre;
    }

    public void setGenre(List<Genres> genre) {
        this.genre = genre;
    }

    public List<String> getCasting() {
        return casting;
    }

    public void setCasting(List<String> casting) {
        this.casting = casting;
    }

    public List<String> getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(List<String> realisateur) {
        this.realisateur = realisateur;
    }

    public String getCovPortrait() {
        return covPortrait;
    }

    public void setCovPortrait(String covPortrait) {
        this.covPortrait = covPortrait;
    }

    public String getCovPaysage() {
        return covPaysage;
    }

    public void setCovPaysage(String covPaysage) {
        this.covPaysage = covPaysage;
    }

    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public Float getAlloGrade() {
        return alloGrade;
    }

    public void setAlloGrade(Float alloGrade) {
        this.alloGrade = alloGrade;
    }

    public Float getImdbGrade() {
        return imdbGrade;
    }

    public void setImdbGrade(Float imdbGrade) {
        this.imdbGrade = imdbGrade;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPegi() {
        return pegi;
    }

    public void setPegi(String pegi) {
        this.pegi = pegi;
    }

    public String getAvertissement() {
        return avertissement;
    }

    public void setAvertissement(String avertissement) {
        this.avertissement = avertissement;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
