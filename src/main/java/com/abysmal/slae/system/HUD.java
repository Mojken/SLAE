package com.abysmal.slae.system;

import java.util.ArrayList;
import java.util.HashMap;

import com.abysmal.slae.SLAE;
import com.abysmal.slae.framework.Input;
import com.abysmal.slae.framework.Window;
import com.abysmal.slae.message.Message;
import com.abysmal.slae.object.HUDObject;
import com.abysmal.slae.util.MouseAction;

public class HUD implements System {

	private int current_scene = 0;

	private HashMap<Integer, ArrayList<HUDObject>> scenes = new HashMap<Integer, ArrayList<HUDObject>>();
	private HUDObject clicked;

	public void init() {
		new Thread(() -> {
			while (SLAE.isRunning()) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
				}
				if (Input.inputQueue.hasNext()) {
					MouseAction click = Input.inputQueue.next();
					if (click.window == Window.getWindowID()) {
						if (clicked != null && click.button == 0 && click.action == 0) {
							clicked.click(0, 0, click.mods);
							clicked = null;
						}
						if (scenes.containsKey(current_scene)) {
							for (HUDObject HUD : scenes.get(current_scene))
								if (HUD.inside(click.pos)) {
									HUD.click(click.button, click.action, click.mods);
									if (click.action == 1)
										clicked = HUD;
									break;
								}
						}
					}
				}
			}
		}, "SLAE HUD").start();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.getMessage().toLowerCase()) {
		case "add hudobject":
			if (!scenes.containsKey((int) ((Object[]) message.getData())[0]))
				scenes.put((int) ((Object[]) message.getData())[0], new ArrayList<HUDObject>());
			scenes.get((int) ((Object[]) message.getData())[0]).add((HUDObject) ((Object[]) message.getData())[1]);
			break;
		case "slae init":
			init();
			break;
		case "switch scene":
			current_scene = (int) message.getData();
			break;
		}
	}
}