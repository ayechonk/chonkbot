package com.chrishonkonen.discord.channelsubscriber.listener;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MessageCreateEventListener {

	public Mono<Void> handle(MessageCreateEvent messageCreateEvent) {
		return Mono.just(messageCreateEvent.getMessage())
			.filter(message -> !message.getContent().isBlank())
			.filter(message -> !this.isBotAuthor(message))
			.flatMap(this::handleMessage)
			.then();
	}

	private Mono<Void> handleMessage(Message message) {
		String response;
		if ("!ping".equals(message.getContent())) {
			response = "pong";
		} else {
			response = null;
		}
		return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(response)).then();
	}

	private boolean isBotAuthor(Message message) {
		final User user = message.getAuthor().orElse(null);
		return user == null || user.isBot();
	}
}
