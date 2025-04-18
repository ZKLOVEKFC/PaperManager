package com.Kz.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import Jackson annotation
import com.fasterxml.jackson.annotation.JsonProperty; // Import Jackson annotation
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Kz.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekServiceImpl.class);

    @Value("${deepseek.api.key}") // 从 application.yml 读取 API Key
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    // Jackson ObjectMapper 用于处理 JSON 序列化和反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String DEEPSEEK_MODEL = "deepseek-chat";

    @Override
    public String generateInnovationPoints(String abstractText) {
        // 检查输入是否为空
        if (abstractText == null || abstractText.trim().isEmpty()) {
            log.warn("Abstract text is empty, skipping innovation point generation.");
            return null;
        }

        //设置 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 设置认证信息，使用 Bearer Token
        headers.setBearerAuth(apiKey);

        // 构造请求体Request Body，定义系统角色和用户请求的 Prompt
        String systemPrompt = "你是一个专门分析学术论文摘要并提取核心创新点的AI助手。";
        String userPrompt = String.format(
                "请仔细阅读以下论文摘要，分析并列出该研究相对于现有工作的主要创新之处或核心贡献点。" +
                        "请用简洁的语言分点说明（例如使用数字编号 1. 2. 3.）：\n\n%s",
                abstractText
        );

        // 使用 DTO 构建请求体，更清晰安全
        DeepSeekRequest requestPayload = new DeepSeekRequest(
                DEEPSEEK_MODEL,
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userPrompt)
                ),
                false // stream = false， 获取完整响应而非流式
        );

        String requestJson;
        try {
            // 将请求对象序列化为 JSON 字符串
            requestJson = objectMapper.writeValueAsString(requestPayload);
        } catch (Exception e) {
            log.error("Error serializing DeepSeek request payload: {}", e.getMessage(), e);
            return "Error: Failed to create request JSON.";
        }

        //创建 HTTP 请求实体
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        try {
            log.info("Sending request to DeepSeek API. URL: {}", DEEPSEEK_API_URL);
            ResponseEntity<String> response = restTemplate.postForEntity(DEEPSEEK_API_URL, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                log.info("Received successful response from DeepSeek API.");

                // 解析 JSON 响应以提取创新点内容
                DeepSeekResponse deepSeekResponse = objectMapper.readValue(responseBody, DeepSeekResponse.class);

                // 从响应结构中安全地提取内容
                if (deepSeekResponse != null &&
                        deepSeekResponse.getChoices() != null &&
                        !deepSeekResponse.getChoices().isEmpty() &&
                        deepSeekResponse.getChoices().get(0) != null &&
                        deepSeekResponse.getChoices().get(0).getMessage() != null &&
                        deepSeekResponse.getChoices().get(0).getMessage().getContent() != null)
                {
                    String innovationPointsResult = deepSeekResponse.getChoices().get(0).getMessage().getContent().trim();
                    log.info("Successfully extracted innovation points.");
                    return innovationPointsResult;
                } else {
                    log.error("Could not extract content from DeepSeek response structure. Response Body: {}", responseBody);
                    return "Error: Could not parse response content.";
                }
            } else {
                // 处理非 响应码200 的情况
                log.error("Error from DeepSeek API: Status Code {}, Body: {}", response.getStatusCode(), response.getBody());
                return "Error: API returned status " + response.getStatusCode();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 处理特定的 HTTP 客户端/服务器错误
            log.error("HTTP Error calling DeepSeek API: Status Code {}, Body: {}, Message: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage(), e);
            return "Error: HTTP " + e.getStatusCode();
        } catch (Exception e) {
            // 处理其他所有异常
            log.error("Exception occurred while calling DeepSeek API: {}", e.getMessage(), e);
            return "Error: Exception during API call.";
        }
    }

    private static class DeepSeekRequest {
        private String model;
        private List<Message> messages;
        private Boolean stream;
        // 构造函数
        public DeepSeekRequest(String model, List<Message> messages, Boolean stream) {
            this.model = model;
            this.messages = messages;
            this.stream = stream;
        }

        //
        public String getModel() { return model; }
        public List<Message> getMessages() { return messages; }
        public Boolean getStream() { return stream; }
    }

    private static class Message {
        private String role;
        private String content;

        // 构造函数
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        // Getters
        public String getRole() { return role; }
        public String getContent() { return content; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeepSeekResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() { return choices; }
        public void setChoices(List<Choice> choices) { this.choices = choices; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Choice {
        private Integer index;
        private ResponseMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;

        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }
        public ResponseMessage getMessage() { return message; }
        public void setMessage(ResponseMessage message) { this.message = message; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseMessage {
        private String role;
        private String content;

        // Getters 和 Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

}