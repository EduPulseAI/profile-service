package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "processed_resume_events")
public class ProcessedResumeEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedResumeEvent(String eventId) {
        this.eventId = eventId;
    }

    @PrePersist
    private void onPrePersist() {
        this.processedAt = Instant.now();
    }
}
