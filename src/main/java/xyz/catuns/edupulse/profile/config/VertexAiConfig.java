package xyz.catuns.edupulse.profile.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.catuns.edupulse.profile.advisor.TokenUsageAuditAdvisor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class VertexAiConfig {


    @Bean
    public ChatClient chatClient(VertexAiGeminiChatModel chatModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(memoryAdvisor, loggerAdvisor, tokenUsageAdvisor))
                .build();
    }

}
