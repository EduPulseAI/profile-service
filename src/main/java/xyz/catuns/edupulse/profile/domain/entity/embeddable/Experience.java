package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class Experience {

    @Column(name = "exp_title")
    private String title;

    @Column(name = "exp_company")
    private String company;

    @Column(name = "exp_period")
    private String period;

    @Column(name = "exp_description", length = 2000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exp_achievements", columnDefinition = "jsonb")
    private List<String> achievements = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exp_technologies", columnDefinition = "jsonb")
    private List<String> technologies = new ArrayList<>();
}
