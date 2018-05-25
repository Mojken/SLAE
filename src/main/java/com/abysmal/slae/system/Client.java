package com.abysmal.slae.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.abysmal.slae.message.Message;
import com.abysmal.slae.util.User;

public class Client implements System {

	private BufferedReader fromServer;
	private PrintStream toServer;
	private Socket connection;

	public Client() {
	}

	private void connect(String IP, User user) {
		int port = Integer.parseInt(IP.substring(IP.indexOf(':') + 1));
		IP = IP.substring(0, IP.indexOf(':'));
		IP = null;
		try {
			connection = new Socket(IP, port);
			fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			toServer = new PrintStream(connection.getOutputStream());

			toServer.println("%l" + user.name + " password");

		} catch (IOException e) {
			messageBus.postMessage(new Message("Client", e.getMessage()));
		}

		Runnable outputListener = () -> {
			while (true) {
				try {
					messageBus.postMessage(new Message("Client", fromServer.readLine()));
				} catch (Exception e) {
					messageBus.postMessage(new Message("Client", e.getMessage()));
					connection = null;
				}
			}
		};

		new Thread(outputListener, "SLAE Client OutputListener").start();
	}

	public void handleMessage(Message message) {
		switch (message.getMessage().toLowerCase()) {
		case "send":
			if (message.getData() instanceof String)
				toServer.print(message.getData() + "\n");
			break;
		case "connect":
			Object[] data = (Object[]) message.getData();
			connect((String) data[0], (User) data[1]);
			break;
		}
	}
}