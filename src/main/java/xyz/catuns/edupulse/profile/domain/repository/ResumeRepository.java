package xyz.catuns.edupulse.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.catuns.edupulse.profile.domain.entity.Resume;

import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
}
