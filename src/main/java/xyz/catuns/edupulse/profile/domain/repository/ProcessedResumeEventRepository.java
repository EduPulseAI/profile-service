package xyz.catuns.edupulse.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.catuns.edupulse.profile.domain.entity.ProcessedResumeEvent;

public interface ProcessedResumeEventRepository extends JpaRepository<ProcessedResumeEvent, String> {
}
