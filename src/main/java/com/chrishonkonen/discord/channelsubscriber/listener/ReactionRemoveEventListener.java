package com.chrishonkonen.discord.channelsubscriber.listener;

import com.chrishonkonen.discord.channelsubscriber.common.BotSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ReactionRemoveEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReactionRemoveEventListener.class);

	private final BotSettings botSettings;

	//	TODO: move this to a repository of some sort.
	private static final Map<String, Long> emojiRoleMap = Collections.synchronizedMap(new HashMap<>());

	static {
		emojiRoleMap.put("👍", 982449009278996491L);
	}

	public ReactionRemoveEventListener(BotSettings botSettings) {
		this.botSettings = botSettings;
	}


	public Mono<Void> handle(ReactionRemoveEvent reactionRemoveEvent) {
		return
			Mono.just(reactionRemoveEvent)
				.filter(this::isCorrectMessage)
				.flatMap(ReactionRemoveEvent::getGuild)
				.flatMapMany(Guild::getMembers)
				.zipWith(Flux.just(reactionRemoveEvent.getUserId()))
				.filter(this::isUserIdMatch)
				.flatMap(objects -> Flux.just(objects.getT1()))
				.zipWith(Mono.just(Objects.requireNonNull(reactionRemoveEvent.getEmoji().asEmojiData().name().orElse(null))))
				.flatMap(objects -> {
					final Member t1 = objects.getT1();
					final String emojiName = objects.getT2();
					Long roleId = emojiRoleMap.get(emojiName);
					if (roleId != null) {
						LOG.info("{} was removed by {}", emojiName, t1.getUsername());
						return t1.removeRole(Snowflake.of(roleId));
					}
					return Mono.empty();
				})
				.then();
	}

	private boolean isUserIdMatch(Tuple2<Member, Snowflake> objects) {
		return objects.getT1().getId().asLong() == objects.getT2().asLong();
	}

	private boolean isCorrectMessage(ReactionRemoveEvent reactionRemoveEvent) {
		return reactionRemoveEvent.getMessageId().asLong() == this.botSettings.subscriptionMessageId();
	}
}
