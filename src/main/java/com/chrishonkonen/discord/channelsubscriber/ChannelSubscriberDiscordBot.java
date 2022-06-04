package com.chrishonkonen.discord.channelsubscriber;

import com.chrishonkonen.discord.channelsubscriber.listener.MessageCreateEventListener;
import com.chrishonkonen.discord.channelsubscriber.listener.ReactionAddEventListener;
import com.chrishonkonen.discord.channelsubscriber.listener.ReactionRemoveEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ChannelSubscriberDiscordBot {
	private final String token;

	public ChannelSubscriberDiscordBot(
		@Value("${token}") String token,
		MessageCreateEventListener messageCreateEventListener,
		ReactionAddEventListener reactionAddEventListener,
		ReactionRemoveEventListener reactionRemoveEventListener
	) {
		this.token = token;
		DiscordClient
			.create(token)
			.gateway()
			.setEnabledIntents(IntentSet.all())
			.withGateway(client -> {
				Mono<Void> onCreateMessage = client.on(MessageCreateEvent.class, messageCreateEventListener::handle).then();
				Mono<Void> onReactionAdd = client.on(ReactionAddEvent.class, reactionAddEventListener::handle).then();
				Mono<Void> onReactionRemove = client.on(ReactionRemoveEvent.class, reactionRemoveEventListener::handle).then();
				return Mono.when(onCreateMessage, onReactionAdd, onReactionRemove);
			}).block();
	}

}
