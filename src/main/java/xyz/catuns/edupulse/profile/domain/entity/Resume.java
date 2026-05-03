package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resumes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uc_profile_original_file_name",
                columnNames = {"profile_id", "original_file_name"})
})
public class Resume {

    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = {PERSIST, DETACH, REFRESH, MERGE})
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "original_file_name")
    private String originalFileName;

}
