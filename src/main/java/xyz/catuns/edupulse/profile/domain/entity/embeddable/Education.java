package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Education {

    @Column(name = "edu_degree")
    private String degree;

    @Column(name = "edu_institution")
    private String institution;

    @Column(name = "edu_year")
    private String year;

    @Column(name = "edu_logo")
    private String logo;
}
