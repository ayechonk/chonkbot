package com.chrishonkonen.discord.chonkbot.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bot")
@EnableConfigurationProperties
public class BotSettings {
	private final String token;
	private final long subscriptionMessageId;

	public BotSettings(@Value("${bot.token}")	String token,	@Value("${bot.subscriptionMessageId}")	long subscriptionMessageId) {
		this.token = token;
		this.subscriptionMessageId = subscriptionMessageId;
	}

	public String getToken() {
		return token;
	}

	public long getSubscriptionMessageId() {
		return subscriptionMessageId;
	}
}



