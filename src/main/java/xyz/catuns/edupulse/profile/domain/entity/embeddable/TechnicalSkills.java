package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class TechnicalSkills {

    @ElementCollection
    @CollectionTable(name = "profile_design_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> design = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_development_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> development = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_ux_methods", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "method")
    private List<String> uxMethods = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_soft_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> softSkills = new ArrayList<>();
}
