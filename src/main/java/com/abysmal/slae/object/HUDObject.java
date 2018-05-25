package com.abysmal.slae.object;

import org.joml.Vector2d;
import org.joml.Vector2i;

public class HUDObject {

	Vector2i a, b;
	ButtonCallback callback;

	public HUDObject(Vector2i a, Vector2i b, ButtonCallback callback) {
		this.a = a;
		this.b = b;
		this.callback = callback;
	}

	public void click(int button, int action, int mods) {
		callback.call(button, action, mods);
	}

	public boolean inside(Vector2d point) {
		if (point.x > a.x && point.y > a.y)
			if (point.x < b.x && point.y < b.y)
				return true;
		return false;
	}

	public interface ButtonCallback {
		public void call(int button, int action, int mods);
	}
}