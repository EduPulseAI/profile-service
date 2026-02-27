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
    @CollectionTable(name = "profile_languages", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_backend_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> backend = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_frontend_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> frontend = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_database_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> database = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_cloud_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> cloud = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_tools_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> tools = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_methodologies_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> methodologies = new ArrayList<>();

}
