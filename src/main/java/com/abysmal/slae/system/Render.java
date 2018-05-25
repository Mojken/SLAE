package com.abysmal.slae.system;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.lang.model.type.UnknownTypeException;

import com.abysmal.slae.framework.Scene;
import com.abysmal.slae.message.Message;
import com.abysmal.slae.object.GUIObject;
import com.abysmal.slae.object.GameObject;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Render implements System {

	public static Render renderer = new Render();
	private HashMap<Integer, Scene> scenes = new HashMap<Integer, Scene>();

	public static final int MAIN_MENU = 0;
	private static int current_scene = 0;

	public static void init() {
		messageBus.addSystem(renderer);
	}

	@Override
	public void handleMessage(Message message) {
		Object[] o;
		switch (message.getMessage().toLowerCase()) {
		case "add guiobject":
			o = (Object[]) message.getData();
			addSceneObject((int) o[0], o[1]);
			break;
		case "add gameobject":
			o = (Object[]) message.getData();
			addSceneObject((int) o[0], o[1]);
			break;
		case "switch scene":
			current_scene = (int) message.getData();
			break;
		}
	}

	private void addSceneObject(int sceneID, Object object) {
		Scene scene = scenes.get(sceneID);

		if (null == scene) {
			scenes.put(sceneID, new Scene());
			scene = scenes.get(sceneID);
		}

		if (object instanceof GameObject) {
			scene.addGameObject((GameObject) object);
		} else if (object instanceof GUIObject) {
			scene.addGUIObject((GUIObject) object);
		}

	}

	public static void render() {
		Scene scene = renderer.scenes.get(current_scene);
		if (null == scene)
			return;

		scene.getGameObjects().forEach((x) -> x.render());
		scene.getGUIObjects().forEach((x) -> x.render());
	}

	public static class VertexBuffer {
		private float[] vertexBuffer;
		private int bufferID;

		public VertexBuffer(float[] data) {
			vertexBuffer = data;
			bufferID = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, bufferID);
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		}

		public void bind() {
			glBindBuffer(GL_ARRAY_BUFFER, bufferID);
		}

		public void unBind() {
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}

	public static class IndexBuffer {
		private IntBuffer indexBuffer;
		private int bufferID;

		public IndexBuffer(int[] data) {
			indexBuffer = IntBuffer.wrap(data);
			bufferID = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferID);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		}

		public void bind() {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferID);
		}

		public void unBind() {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		public int getDrawCount() {
			return indexBuffer.capacity();
		}
	}

	public static class VertexArray {
		private int vertexArrayID;

		public VertexArray() {
			vertexArrayID = glGenVertexArrays();
			bind();
		}

		public void addBuffer(VertexBuffer buffer, VertexBufferLayout layout) {
			bind();
			buffer.bind();
			int offset = 0;
			for (int i = 0; i < layout.getElements().size(); i++) {
				VertexBufferElement e = layout.getElements().get(i);
				glEnableVertexAttribArray(i);
				glVertexAttribPointer(i, e.count, e.type, e.normalised, layout.getStride(), offset);
				offset += e.count * sizeOf(e.type);
			}
		}

		public void bind() {
			glBindVertexArray(vertexArrayID);
		}

		public void unBind() {
			glBindVertexArray(0);
		}

	}

	public static class VertexBufferLayout {
		private List<VertexBufferElement> vertexBufferLayout = new ArrayList<VertexBufferElement>();
		private int stride = 0;

		public void push(int type, int count) {
			vertexBufferLayout.add(new VertexBufferElement(type, count, (type == GL_FLOAT))); // TODO: add more datatypes
			stride += count * sizeOf(type);
		}

		public int getStride() {
			return stride;
		}

		public List<VertexBufferElement> getElements() {
			return vertexBufferLayout;
		}
	}

	private static class VertexBufferElement {
		private int type;
		private int count;
		private boolean normalised;

		public VertexBufferElement(int type, int count, boolean normalised) {
			this.type = type;
			this.count = count;
			this.normalised = normalised;
		}
	}

	public static class Shader {
		private int shader;

		public Shader(String filepath) {
			String[] s = readShadersFromFile(filepath);
			shader = createShader(s);
		}

		public void bind() {
			glUseProgram(shader);
		}

		public void unBind() {
			glUseProgram(0);
		}

		private int createShader(String[] shaders) {
			int shader = glCreateProgram();

			int vs = compileShader(shaders[0], GL_VERTEX_SHADER);
			int fs = compileShader(shaders[1], GL_FRAGMENT_SHADER);

			glAttachShader(shader, vs);
			glAttachShader(shader, fs);

			glLinkProgram(shader);
			glValidateProgram(shader);

			glDeleteShader(vs);
			glDeleteShader(fs);

			return shader;
		}

		private int compileShader(String source, int type) {
			int shader = glCreateShader(type);
			glShaderSource(shader, source);
			glCompileShader(shader);

			IntBuffer result = MemoryUtil.memAllocInt(1);
			glGetShaderiv(shader, GL_COMPILE_STATUS, result);
			if (result.get(0) == GL_FALSE) {
				Logger.getLogger(Render.class.getName()).log(Level.SEVERE, glGetShaderInfoLog(shader));
				return 0;
			}
			return shader;
		}

		private String[] readShadersFromFile(String file) {
			StringBuilder vs = new StringBuilder();
			StringBuilder fs = new StringBuilder();
			int type = 0;
			try {
				FileInputStream in = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

				String line;
				while ((line = reader.readLine()) != null) {
					if (line.matches("^#[\t ]*shader[\t ]+vertex[\t ]*$"))
						type = 0;
					else if (line.matches("^#[\t ]*shader[\t ]+fragment[\t ]*$"))
						type = 1;

					else if (type == 0)
						vs.append(line).append("\n");
					else if (type == 1)
						fs.append(line).append("\n");
				}
				reader.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new String[] { vs.toString(), fs.toString() };
		}
		
		public void setUniform1i(String name, int val) {
			glUniform1i(glGetUniformLocation(shader, name), val);
		}
		
		public void setUniform4f(String name, float val1, float val2, float val3, float val4){
			glUniform4f(glGetUniformLocation(shader, name), val1, val2, val3, val4);
		}
	}

	private static int sizeOf(int type) {
		switch (type) {
		case GL_FLOAT:
		case GL_INT:
		case GL_UNSIGNED_INT:
			return 4;

		case GL_SHORT:
		case GL_UNSIGNED_SHORT:
			return 2;

		case GL_BYTE:
		case GL_UNSIGNED_BYTE:
			return 1;
		}
		throw new UnknownTypeException(null, null);
	}

	public static class Texture {

		int id;

		public Texture(String imagePath) {
			BufferedImage bi;
			int width, height;
			try {
				bi = ImageIO.read(new File(imagePath));
				width = bi.getWidth();
				height = bi.getHeight();

				int[] pixels_raw = new int[width * height];
				pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);

				ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pixel = pixels_raw[y * width + x];
						pixels.put((byte) (pixel >> 16 & 0xFF)); // Red
						pixels.put((byte) (pixel >> 8 & 0xFF)); // Green
						pixels.put((byte) (pixel & 0xFF)); // Blue
						pixels.put((byte) (pixel >> 24 & 0xFF)); // Alpha
					}
				}

				pixels.flip();

				id = glGenTextures();
				glBindTexture(GL_TEXTURE_2D, id);

				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void bind(int slot) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
			glBindTexture(GL_TEXTURE_2D, id);
		}
	}
}