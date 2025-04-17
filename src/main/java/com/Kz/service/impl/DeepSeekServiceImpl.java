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

    // 建议将 RestTemplate 配置为 Bean 以便更好地管理（例如设置超时）
    // @Autowired // 如果配置为 Bean
    private final RestTemplate restTemplate = new RestTemplate();

    // Jackson ObjectMapper 用于处理 JSON 序列化和反序列化
    private final ObjectMapper objectMapper = new ObjectMapper();

    // !!关键!!: 请务必从 DeepSeek 官方 API 文档确认此 URL
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    // !!关键!!: 请务必从 DeepSeek 官方 API 文档确认适合此任务的模型名称
    private static final String DEEPSEEK_MODEL = "deepseek-chat"; // 或者可能是 "deepseek-coder" 等

    @Override
    public String generateInnovationPoints(String abstractText) {
        // 检查输入是否为空
        if (abstractText == null || abstractText.trim().isEmpty()) {
            log.warn("Abstract text is empty, skipping innovation point generation.");
            return null;
        }

        // 1. 设置 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 设置认证信息，使用 Bearer Token
        headers.setBearerAuth(apiKey);

        // 2. 构造请求体 (Request Body)
        // 定义系统角色和用户请求的 Prompt
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
                // 可以根据需要添加其他参数，如 temperature: 0.5
        );

        String requestJson;
        try {
            // 将请求对象序列化为 JSON 字符串
            requestJson = objectMapper.writeValueAsString(requestPayload);
        } catch (Exception e) {
            log.error("Error serializing DeepSeek request payload: {}", e.getMessage(), e);
            return "Error: Failed to create request JSON.";
        }

        // 3. 创建 HTTP 请求实体
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // 4. 发送 HTTP POST 请求并处理响应
        try {
            log.info("Sending request to DeepSeek API. URL: {}", DEEPSEEK_API_URL);
            // 如果需要调试，可以取消下面这行的注释来打印请求体，但注意可能包含敏感信息
            // log.debug("DeepSeek Request Payload: {}", requestJson);

            ResponseEntity<String> response = restTemplate.postForEntity(DEEPSEEK_API_URL, entity, String.class);

            // 5. 处理响应
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                log.info("Received successful response from DeepSeek API.");
                // 如果需要调试，可以取消下面这行的注释来打印完整的响应体
                // log.debug("DeepSeek Response Body: {}", responseBody);

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
                // 处理非 200 OK 的情况
                log.error("Error from DeepSeek API: Status Code {}, Body: {}", response.getStatusCode(), response.getBody());
                return "Error: API returned status " + response.getStatusCode();
            }
            // 6. 异常处理
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 处理特定的 HTTP 客户端/服务器错误
            log.error("HTTP Error calling DeepSeek API: Status Code {}, Body: {}, Message: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage(), e);
            return "Error: HTTP " + e.getStatusCode();
        } catch (Exception e) {
            // 处理其他所有异常 (例如网络问题, JSON 解析失败等)
            log.error("Exception occurred while calling DeepSeek API: {}", e.getMessage(), e);
            return "Error: Exception during API call.";
        }
    }

    // --- 内部 DTO 类，用于构造请求体 JSON ---
    // 使用 static 内部类，或者将它们移到单独的文件中
    private static class DeepSeekRequest {
        private String model;
        private List<Message> messages;
        private Boolean stream;
        // 可根据需要添加其他参数, e.g., private Double temperature;

        // 构造函数
        public DeepSeekRequest(String model, List<Message> messages, Boolean stream) {
            this.model = model;
            this.messages = messages;
            this.stream = stream;
        }

        // Getters (必须有，Jackson序列化需要)
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
        // Getters (必须有，Jackson序列化需要)
        public String getRole() { return role; }
        public String getContent() { return content; }
    }

    // --- 内部 DTO 类，用于解析响应体 JSON ---
    // !!关键!!: 这个结构是基于常见 OpenAI 兼容 API 的猜测，请务必根据 DeepSeek 的实际响应调整
    @JsonIgnoreProperties(ignoreUnknown = true) // 忽略 JSON 中有但 DTO 中没有的字段
    private static class DeepSeekResponse {
        private List<Choice> choices;
        // 可能还有 id, object, created, model, usage 等字段，根据需要添加

        // Getter 和 Setter (或者使用 Lombok @Data)
        public List<Choice> getChoices() { return choices; }
        public void setChoices(List<Choice> choices) { this.choices = choices; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Choice {
        private Integer index;
        private ResponseMessage message;
        @JsonProperty("finish_reason") // 明确指定 JSON 字段名（如果和 Java 命名不同）
        private String finishReason;

        // Getters 和 Setters
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

    // 如果需要获取 token 使用量，可以添加 Usage DTO
    // @JsonIgnoreProperties(ignoreUnknown = true)
    // private static class Usage {
    //    @JsonProperty("prompt_tokens")
    //    private Integer promptTokens;
    //    @JsonProperty("completion_tokens")
    //    private Integer completionTokens;
    //    @JsonProperty("total_tokens")
    //    private Integer totalTokens;
    //    // Getters and Setters...
    // }
}