package com.chrishonkonen.discord.chonkbot.listener;

import com.chrishonkonen.discord.chonkbot.common.BotSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ReactionAddEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReactionAddEventListener.class);

	private final BotSettings botSettings;

//	TODO: move this to a repository of some sort.
	private static final Map<String, Long> emojiRoleMap = Collections.synchronizedMap(new HashMap<>());

	static {
		emojiRoleMap.put("👍", 982449009278996491L);
		emojiRoleMap.put("😭", 982449064455073862L);
		emojiRoleMap.put("🙂", 982449132549570631L);
	}

	public ReactionAddEventListener(BotSettings botSettings) {
		this.botSettings = botSettings;
	}

	public Mono<Void> handle(ReactionAddEvent reactionAddEvent) {
		return Mono
			.just(reactionAddEvent)
			.filter(this::isCorrectMessage)
			.flatMap(r -> Mono.just(Objects.requireNonNull(r.getMember().orElse(null))))
			.zipWith(Mono.just(Objects.requireNonNull(reactionAddEvent.getEmoji().asEmojiData().name().orElse(null))))
			.flatMap(objects -> {
				final Member t1 = objects.getT1();
				final String emojiName = objects.getT2();
				Long roleId = emojiRoleMap.get(emojiName);
				if (roleId != null) {
					LOG.info("{} was added by {}", emojiName, t1.getUsername());
					return t1.addRole(Snowflake.of(roleId));
				}
				return Mono.empty();
			})
			.then();
	}

	private boolean isCorrectMessage(ReactionAddEvent reactionAddEvent) {
		return reactionAddEvent.getMessageId().asLong() == this.botSettings.getSubscriptionMessageId();
	}
}
