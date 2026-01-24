package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class Credentials {

    @ElementCollection
    @CollectionTable(name = "profile_certifications", joinColumns = @JoinColumn(name = "profile_id"))
    private List<Certification> certifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_education", joinColumns = @JoinColumn(name = "profile_id"))
    private List<Education> education = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();
}
