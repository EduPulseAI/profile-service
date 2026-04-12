package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resumes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uc_username_original_file_name",
                columnNames = {"username", "original_file_name"})
})
public class Resume {

    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "original_file_name")
    private String originalFileName;

}
