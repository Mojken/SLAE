package com.abysmal.slae.framework;

import com.abysmal.slae.util.datastructure.Queue;
import com.abysmal.slae.util.MouseAction;

import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class Input {

	protected static double xpos = 0, ypos = 0;
	public static Queue<MouseAction> inputQueue = new Queue<MouseAction>();
	public static HashMap<Integer, Integer> keyboard = new HashMap<Integer, Integer>();
	private static FloatBuffer inputBuffer = FloatBuffer.allocate(20);

	public Input() {
		glfwSetCursorPosCallback(Window.getWindowID(), (w, x, y) -> { //Window, x position, y position
			xpos = x;
			ypos = y;
		});

		glfwSetMouseButtonCallback(Window.getWindowID(), (w, b, a, m) -> { //Window, button, action, mods
			inputQueue.add(new MouseAction(w, b, a, m, getMousePos()));
		});

		glfwSetKeyCallback(Window.getWindowID(), (w, k, s, a, m) -> { //Window, key, scancode, action, mods
			keyboard.put(s, a);
		});
	}

	public static Vector2d getMousePos() {
		return new Vector2d(xpos, ypos);
	}

	public static FloatBuffer getJoystickInput(int joystick) {
		glfwPollEvents();
		if (glfwGetJoystickName(joystick) != null) {
			inputBuffer.clear();
			inputBuffer.put(glfwGetJoystickAxes(joystick));
			ByteBuffer temp = glfwGetJoystickButtons(joystick);
			for (int i = 0; i < 16; i++) {
				inputBuffer.put(temp.get(i));
			}
			return inputBuffer;
		}
		return null;
	}
}