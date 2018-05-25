package com.abysmal.slae.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.abysmal.slae.message.Message;

public class Server implements System {

	public static ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	ServerSocket socket;
	public int port;

	public Server() {
	}

	private void startServer(int port) {
		try {
			messageBus.postMessage(new Message("Print", "Starting server at localhost:" + port));
			socket = new ServerSocket(port);
		} catch (IOException e) {
			messageBus.postMessage(new Message("Server", e));
			messageBus.postMessage(new Message("Print", "Server failed to bind to port: " + e.getMessage()));
		}

		Runnable connectionListener = () -> {
			while (true) {
				try {
					ServerClient client = new ServerClient(socket.accept());
					clients.add(client);
				} catch (IOException e) {
					messageBus.postMessage(new Message("Server", e.getMessage()));
				}
			}
		};

		new Thread(connectionListener, "SLAE Server ConnectionListener").start();
	}

	public void handleMessage(Message message) {
		switch (message.getMessage().toLowerCase()) {
		case "start server":
			startServer((int) message.getData());
		case "send":
			if (message.getData() instanceof Object[]) {
				clients.forEach((c) -> {
					if (c.name.equals(((Object[])(message.getData()))[0]))
					c.toClient.println(((Object[])(message.getData()))[1]);
				});
			}
		default:
			break;
		}
	}

	class ServerClient {

		private PrintStream toClient;
		private BufferedReader fromClient;
		private String name = "";

		public ServerClient(Socket socket) {
			try {
				toClient = new PrintStream(socket.getOutputStream());
				fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				toClient.println("Connected");
			} catch (IOException e) {
				messageBus.postMessage(new Message("Server", e.getMessage()));
			}

			try {
				String login = fromClient.readLine();

				if (login.startsWith("%l")) {
					name = login.substring(2, login.indexOf(' '));
					messageBus.postMessage(new Message("Server", name + " connected"));
				}
			} catch (IOException e) {
				messageBus.postMessage(new Message("Server", e.getMessage()));
			}

			Runnable output = () -> {
				while (true) {
					try {
						String cmd = fromClient.readLine();
						messageBus.postMessage(new Message("Server", name + ": " + cmd));
					} catch (IOException e) {
						messageBus.postMessage(new Message("Server", name + " disconnected: " + e.getMessage()));
						return;
					}
				}
			};

			new Thread(output, "SLAE " + name + " output").start();
		}
	}
}