package com.Kz.service;

/**
 * Interface for interacting with the DeepSeek AI service.
 */
public interface DeepSeekService {

    /**ds借助摘要生成创新点
     *
     * @param abstractText 摘要
     */
    String generateInnovationPoints(String abstractText);
}