package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class About {

    @Column(name = "bio", length = 2000)
    private String bio;

    @ElementCollection
    @CollectionTable(name = "profile_focus", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "focus_item")
    private List<String> focus = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_languages", joinColumns = @JoinColumn(name = "profile_id"))
    private List<Language> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_interests", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();
}
