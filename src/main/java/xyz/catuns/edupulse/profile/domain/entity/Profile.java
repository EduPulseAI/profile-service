package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_profiles")
public class Profile {

    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal information
    @Embedded
    private Personal personal;

    // About section
    @Embedded
    private About about;

    // Experience list
    @ElementCollection
    @CollectionTable(name = "profile_experiences", joinColumns = @JoinColumn(name = "profile_id"))
    @OrderColumn(name = "experience_order")
    private List<Experience> experiences = new ArrayList<>();

    // Credentials
    @Embedded
    private Credentials credentials;

    // Technical skills
    @Embedded
    private TechnicalSkills technicalSkills;
}
