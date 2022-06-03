package com.chrishonkonen.discord.channelsubscriber.listener;

import discord4j.core.event.domain.message.ReactionAddEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactionAddEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReactionAddEventListener.class);

	public Mono<Void> handle(ReactionAddEvent reactionAddEvent) {
		return
			Mono.just(reactionAddEvent)
				.filter(this::isCorrectMessage)
				.flatMap(this::logMessage)
				.then();
	}

	private Mono<Void> logMessage(ReactionAddEvent reactionAddEvent) {
		return Mono.just(reactionAddEvent)
			.flatMap(ReactionAddEvent::getUser)
			.flatMap(user -> {
				LOG.info("{} was added by {}", reactionAddEvent.getEmoji().asEmojiData().name(), user.getUsername());
				return Mono.empty();
			});
	}

	private boolean isCorrectMessage(ReactionAddEvent reactionAddEvent) {
		return reactionAddEvent.getMessageId().asLong() == 982393192764829757L;
	}
}
