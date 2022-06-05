package com.chrishonkonen.discord.channelsubscriber;

import com.chrishonkonen.discord.channelsubscriber.common.BotSettings;
import com.chrishonkonen.discord.channelsubscriber.listener.MessageCreateEventListener;
import com.chrishonkonen.discord.channelsubscriber.listener.ReactionAddEventListener;
import com.chrishonkonen.discord.channelsubscriber.listener.ReactionRemoveEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!test")
public class ChannelSubscriberDiscordBot {
	private final Logger LOG = LoggerFactory.getLogger(ChannelSubscriberDiscordBot.class);

	public ChannelSubscriberDiscordBot(
		BotSettings botSettings,
		MessageCreateEventListener messageCreateEventListener,
		ReactionAddEventListener reactionAddEventListener,
		ReactionRemoveEventListener reactionRemoveEventListener
	) {
		try {
			DiscordClient
				.create(botSettings.getToken())
				.gateway()
				.setEnabledIntents(IntentSet.all())
				.withGateway(client -> {
					Mono<Void> onCreateMessage = client.on(MessageCreateEvent.class, messageCreateEventListener::handle).then();
					Mono<Void> onReactionAdd = client.on(ReactionAddEvent.class, reactionAddEventListener::handle).then();
					Mono<Void> onReactionRemove = client.on(ReactionRemoveEvent.class, reactionRemoveEventListener::handle).then();
					return Mono.when(onCreateMessage, onReactionAdd, onReactionRemove);
				}).block();
		} catch (Exception ex) {
			LOG.error("Unable to start discord bot", ex);
		}
	}

}
