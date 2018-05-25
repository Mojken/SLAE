package com.abysmal.slae.system;

import com.abysmal.slae.message.Message;
import com.abysmal.slae.message.MessageBus;

public interface System {

	public void handleMessage(Message message);

	MessageBus messageBus = MessageBus.getBus();

}