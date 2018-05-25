package com.abysmal.slae.message;

import java.util.ArrayList;

import com.abysmal.slae.system.System;
import com.abysmal.slae.util.datastructure.Queue;

public class MessageBus {

	private static final MessageBus bus = new MessageBus();
	private Queue<Message> queue = new Queue<Message>();
	private ArrayList<System> systems = new ArrayList<System>();

	private MessageBus() {
	}

	public void postMessage(Message message) {
		queue.add(message);
	}

	public void pushMessage() {
		try {
			Message message = queue.next();
			systems.forEach((E) -> E.handleMessage(message));
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void addSystem(System s) {
		systems.add(s);
	}

	public static MessageBus getBus() {
		return bus;
	}
}