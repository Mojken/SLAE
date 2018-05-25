package com.abysmal.slae.system;

import java.util.Arrays;

import com.abysmal.slae.message.Message;

public class Console implements System {

	boolean verbose = false;

	@Override
	public void handleMessage(Message message) {
		if (message.getMessage().equalsIgnoreCase("toggleverboseconsole"))
			verbose = !verbose;

		if (verbose) {
			if (message.getData() == null) {
				java.lang.System.out.println("[" + message.getMessage() + "]");
			} else if (message.getData().getClass().isArray()) {
				java.lang.System.out.println(
						(!message.getMessage().equalsIgnoreCase("print") ? "[" + message.getMessage() + "] " : "")
								+ Arrays.toString((Object[]) message.getData()).toString());
			} else {
				java.lang.System.out.println(
						(!message.getMessage().equalsIgnoreCase("print") ? "[" + message.getMessage() + "] " : "")
								+ message.getData().toString());
			}
		} else if (message.getMessage().equalsIgnoreCase("print"))
			java.lang.System.out.println(message.getData());
	}
}