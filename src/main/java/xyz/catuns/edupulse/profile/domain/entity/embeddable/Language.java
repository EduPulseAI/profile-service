package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Language {

    @Column(name = "language_name")
    private String name;

    @Column(name = "language_proficiency")
    private String proficiency;

    @Column(name = "language_level")
    private int level;

    @Column(name = "language_flag")
    private String flag;
}
