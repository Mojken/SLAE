package com.abysmal.slae.util;

import org.joml.Vector2d;

public class MouseAction {
	public long window;
	public int button, action, mods;
	public Vector2d pos;
	
	public MouseAction(long window, int button, int action, int mods, Vector2d pos) {
		this.window = window;
		this.button = button;
		this.action = action;
		this.mods = mods;
		this.pos = pos;
	}
}