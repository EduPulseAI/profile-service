package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class Personal {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "available_for_work")
    private boolean availableForWork;

    @ElementCollection
    @CollectionTable(name = "profile_badges", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "badge")
    private List<String> badges = new ArrayList<>();

    @Embedded
    private SocialLink social;
}
