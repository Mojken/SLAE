package com.abysmal.slae.system;

import com.abysmal.slae.message.Message;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class ISO implements System {

	public static Vector2d isoToOrtho(Vector2d p) {
		return new Vector2d(p.x - p.y, (int) ((p.x + p.y) / 2f) + 1);
	}

	public static Vector2d isoToOrtho(Vector3d p) {
		return new Vector2d(p.x - p.y, (p.x + p.y) / 2 - p.z);
	}

	public static Vector2d orthoToIso(Vector2d p) {
		return new Vector2d((2 * p.y + p.x) / 2, (2 * p.y - p.x) / 2);
	}

	public void handleMessage(Message message) {
		switch (message.getMessage().toLowerCase()) {
		default:
		break;
		}
	}

}