package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SocialLink {

    @Column(name = "social_github")
    private String github;

    @Column(name = "social_linkedin")
    private String linkedin;

    @Column(name = "social_discord")
    private String discord;

    @Column(name = "social_twitter")
    private String twitter;

    @Column(name = "social_instagram")
    private String instagram;
}
