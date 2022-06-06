package com.chrishonkonen.discord.chonkbot.listener;

import com.chrishonkonen.discord.chonkbot.common.Function2;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageCreateEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(MessageCreateEventListener.class);
	private final Map<String, Function2<Message, String[], Mono<Message>>> mapOfCommands;

	public MessageCreateEventListener() {
		this.mapOfCommands = Collections.synchronizedMap(new HashMap<>());
		this.mapOfCommands.put("addMsg", this::addMessage);
	}


	public Mono<Void> handle(MessageCreateEvent messageCreateEvent) {
		LOG.info("Handling MessageCreateEvent");
		return Mono.just(messageCreateEvent.getMessage())
			.filter(message -> !message.getContent().isBlank() && this.hasPrefix(message.getContent()) && !this.isBotAuthor(message))
			.flatMap(message -> {
				String[] parts = message.getContent().split(" ");
				if (parts.length < 3) {
					return this.invalidMessage(message);
				}
				final String command = parts[1];
				Function2<Message, String[], Mono<Message>> fn = this.mapOfCommands.get(command);
				if (fn == null) {
					return this.commandInvalid(message, command);
				} else {
					return fn.apply(message, parts);
				}
			})
			.then();
	}

	private Mono<Message> invalidMessage(Message message) {
		return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("The command is not valid."));
	}

	private Mono<Message> commandInvalid(Message message, String invalidCommand) {
		LOG.info("Not a valid command: {}", invalidCommand);
		return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("Not a valid command."));
	}

	private Mono<Message> addMessage(Message message, String[] args) {
		LOG.info("Adding message {} to be watched.", args[2]);
		return message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("Message has been added"));
	}

	private boolean hasPrefix(String content) {
		return content.startsWith("!br ");
	}

	private boolean isBotAuthor(Message message) {
		final User user = message.getAuthor().orElse(null);
		return user == null || user.isBot();
	}
}
