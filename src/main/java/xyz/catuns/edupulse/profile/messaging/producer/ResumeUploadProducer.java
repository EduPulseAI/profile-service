package xyz.catuns.edupulse.profile.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeUploadProducer {

    private final KafkaTemplate<String, SpecificRecord> resumeKafkaTemplate;

    @Value("${app.kafka.topics.resume-upload}")
    private String resumeUploadTopic;

    public CompletableFuture<SendResult<String, SpecificRecord>> publish(ResumeUpload event) {
        var future = resumeKafkaTemplate.send(resumeUploadTopic, event.getUserId(), event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish ResumeUpload event for userId={} resumeId={}: {}",
                        event.getUserId(), event.getResumeId(), ex.getMessage());
            } else {
                log.debug("Published ResumeUpload event userId={} resumeId={} status={} offset={}",
                        event.getUserId(), event.getResumeId(), event.getStatus(),
                        result.getRecordMetadata().offset());
            }
        });

        return future;
    }
}