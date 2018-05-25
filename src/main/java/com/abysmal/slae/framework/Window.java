package com.abysmal.slae.framework;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import com.abysmal.slae.Configuration;
import com.abysmal.slae.Version;
import com.abysmal.slae.exception.AlreadyInitialisedException;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;

public class Window {

	private long windowID = NULL;
	private int height, width;
	private double aspectRatio;
	private String title = "SLAE " + Version.MAJOR + "." + Version.MINOR + "." + Version.SUPER_MINOR;
	private boolean fullscreen;
	private RenderCallbackI renderCallback = null;

	private static Window window = new Window();

	private Window() {
		width = Configuration.DEFAULT_WIDTH;
		aspectRatio = Configuration.DEFAULT_ASPECT_RATIO;
		fullscreen = Configuration.DEFAULT_FULLSCREEN;
		height = (int) (width / aspectRatio);
	}

	public static void createWindow() {
		if (window.windowID != NULL) {
			throw new AlreadyInitialisedException("Cannot create multiple windows");
		}

		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("Failed to initialise GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, Version.getOpenGLMajor());
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, Version.getOpenGLMinor());
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		long monitor = glfwGetPrimaryMonitor();

		if (window.fullscreen) {
			GLFWVidMode vidmode = glfwGetVideoMode(monitor);
			window.width = vidmode.width();
			window.height = vidmode.height();
			window.aspectRatio = window.width / window.height;
		}

		window.windowID = glfwCreateWindow(window.width, window.height, window.title,
				window.fullscreen ? monitor : NULL, NULL);
		
		if (window.windowID == NULL) {
			throw new RuntimeException("Failed to create GLFW window");
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pwidth = stack.mallocInt(1);
			IntBuffer pheight = stack.mallocInt(1);

			glfwGetWindowSize(window.windowID, pwidth, pheight);

			GLFWVidMode vidmode = glfwGetVideoMode(monitor);

			glfwSetWindowPos(window.windowID, (vidmode.width() - pwidth.get(0)) / 2,
					(vidmode.height() - pheight.get(0)) / 2);
		}

		glfwMakeContextCurrent(window.windowID);
		GL.createCapabilities();
	}

	public static void showWindow() {
		glfwShowWindow(window.windowID);
		glfwSwapInterval(Configuration.VSYNC ? 1 : 0);

		while (!glfwWindowShouldClose(window.windowID)) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			window.renderCallback.render(window.windowID);

			glfwSwapBuffers(window.windowID);
			glfwPollEvents();
		}

		glfwDestroyWindow(window.windowID);
		glfwTerminate();
	}

	public static void setWidth(int width) {
		window.width = width;
	}

	public static void setAspectRatio(double aspectRatio) {
		window.aspectRatio = aspectRatio;
	}

	public static void setFullscreen(boolean fullscreen) {
		window.fullscreen = fullscreen;
	}

	public static void setTitle(String title) {
		window.title = title;
		try {
			Checks.check(window.windowID);
			glfwSetWindowTitle(window.windowID, title);
		} catch (NullPointerException e) {
		}
	}

	public static RenderCallbackI setRenderCallback(RenderCallbackI callback) {
		RenderCallbackI old = window.renderCallback;
		window.renderCallback = callback;
		return old;
	}

	@FunctionalInterface
	public interface RenderCallbackI {
		public void render(long windowID);
	}
	
	public static long getWindowID() {
		return window.windowID;
	}
	
	public static int getWidth() {
		return window.width;
	}
	
	public static int getHeight() {
		return window.height;
	}
	
	public static Vector2d getSize() {
		return new Vector2d(getWidth(), getHeight());
	}
}