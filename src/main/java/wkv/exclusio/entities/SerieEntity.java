package wkv.exclusio.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="series")
@Data
public class SerieEntity extends AbstractBaseEntity{
    private String titre;

    @Column(length = 3000)
    private String synopsis;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Genres> genre;

    @ElementCollection
    private List<String> casting;

    @ElementCollection
    private List<String> realisateur;

    @Column(name="cov_portrait")
    private String covPortrait;

    @Column(name="allo_grade")
    private Float alloGrade;

    private String year;

    private int seasons;

    private int episodes;

    @Column(name="format_episode")
    private String formatEpisode;

    @Column(name="code_html_allocine")
    private String codeHtmlAllocine;
}
