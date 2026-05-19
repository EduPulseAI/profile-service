package xyz.catuns.edupulse.profile.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.catuns.edupulse.profile.advisor.TokenUsageAuditAdvisor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class VertexAiConfig {

    @Bean
    public ChatClient chatClient(VertexAiGeminiChatModel chatModel, ChatMemory chatMemory, VectorStore vectorStore) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(memoryAdvisor, loggerAdvisor, tokenUsageAdvisor, questionAnswerAdvisor))
                .build();
    }

    @Bean
    @Qualifier("parsingChatClient")
    public ChatClient parsingChatClient(VertexAiGeminiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new TokenUsageAuditAdvisor(), new SimpleLoggerAdvisor()))
                .build();
    }
}
