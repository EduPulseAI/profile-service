package xyz.catuns.edupulse.profile.domain.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Certification {

    @Column(name = "cert_name")
    private String name;

    @Column(name = "cert_issuer")
    private String issuer;

    @Column(name = "cert_date")
    private String date;

    @Column(name = "cert_logo")
    private String logo;
}
