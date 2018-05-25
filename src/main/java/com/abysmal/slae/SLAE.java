package com.abysmal.slae;

import static com.abysmal.slae.message.MessageBus.getBus;

import java.util.Set;

import com.abysmal.slae.exception.AlreadyInitialisedException;
import com.abysmal.slae.framework.Window;
import com.abysmal.slae.framework.Input;
import com.abysmal.slae.message.Message;
import com.abysmal.slae.system.Client;
import com.abysmal.slae.system.Console;
import com.abysmal.slae.system.HUD;
import com.abysmal.slae.system.Render;
import com.abysmal.slae.system.Server;
import com.abysmal.slae.system.System;

import org.lwjgl.opengl.GL11;

public class SLAE {

	private static boolean init = false;
	private static boolean running = false;

	public static void initialise(System game) {
		if (init)
			throw new AlreadyInitialisedException("SLAE has already been initialised!");
		init = true;

		java.lang.System.out.println("SLAE\t" + Version.getVersion());
		java.lang.System.out.println("LWJGL\t" + org.lwjgl.Version.getVersion());

		getBus().addSystem(game);
		getBus().addSystem(new Console());
		getBus().addSystem(new HUD());
		getBus().addSystem(new Server());
		getBus().addSystem(new Client());
		Render.init();
		new Thread(() -> {
			Window.createWindow();
			Window.setRenderCallback((id) -> {
				Render.render();
			});

			running = true;
			java.lang.System.out.println("OpenGL\t" + GL11.glGetString(GL11.GL_VERSION));
			getBus().postMessage(new Message("SLAE Init", null));

			new Thread(() -> {
				while (running) {
					getBus().pushMessage();
				}
			}, "SLAE Message Dispatcher").start();

			new Input();
			Window.showWindow();
			running = false;
		}, "SLAE OpenGL").start();
		
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
			if (t.getName().contains("SLAE"))
				java.lang.System.out.println(t.getName());
		}
		
		getBus().postMessage(new Message("Send", "Nibbay"));
	}

	public static boolean isRunning() {
		return running;
	}

	public static void execute(String message, Object data) {
		getBus().postMessage(new Message(message, data));
	}
}