package com.chrishonkonen.discord.channelsubscriber.listener;

import discord4j.core.event.domain.message.ReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactionRemoveEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReactionRemoveEventListener.class);

	public Mono<Void> handle(ReactionRemoveEvent reactionRemoveEvent) {
		return
			Mono.just(reactionRemoveEvent)
				.filter(this::isCorrectMessage)
				.flatMap(this::logMessage)
				.then();
	}

	private Mono<Void> logMessage(ReactionRemoveEvent reactionRemoveEvent) {
		return Mono.just(reactionRemoveEvent)
			.flatMap(ReactionRemoveEvent::getUser)
			.flatMap(user -> {
				LOG.info("{} was removed by {}", reactionRemoveEvent.getEmoji().asEmojiData().name(), user.getUsername());
				return Mono.empty();
			});
	}

	private boolean isCorrectMessage(ReactionRemoveEvent reactionRemoveEvent) {
		return reactionRemoveEvent.getMessageId().asLong() == 982393192764829757L;
	}
}
