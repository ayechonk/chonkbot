package com.chrishonkonen.discord.channelsubscriber.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record BotSettings(@Value("${token}") String token, @Value("${subscriptionMessageId}") long subscriptionMessageId) {}



