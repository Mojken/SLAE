package com.abysmal.slae.object;

import java.awt.Polygon;
import java.util.ArrayList;

import com.abysmal.slae.framework.Window;
import com.abysmal.slae.system.Render.IndexBuffer;
import com.abysmal.slae.system.Render.Shader;
import com.abysmal.slae.system.Render.Texture;
import com.abysmal.slae.system.Render.VertexArray;
import com.abysmal.slae.system.Render.VertexBuffer;
import com.abysmal.slae.system.Render.VertexBufferLayout;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class GameObject {

	private VertexArray va;
	private VertexBuffer vb;
	private VertexBufferLayout vl;
	private IndexBuffer ib;
	private Shader sh;
	private boolean initialised = false;

	private float[] vertexBuffer;
	private String shader;
	private String texturePath;
	private Texture texture;

	private Vector3f position = new Vector3f();

	private ArrayList<GameObject> children;

	public GameObject(ArrayList<GameObject> children) {
		this.children = children;
	}

	public GameObject(Polygon p, String texture, String shader) {
		this.texturePath = texture;
		this.shader = shader;
		this.vertexBuffer = new float[p.npoints * 4];
		for (int i = 0; i < this.vertexBuffer.length; i += 4) {
			this.vertexBuffer[i + 0] = p.xpoints[i / 4] / ((float) Window.getWidth()) * 2f - 1;
			this.vertexBuffer[i + 1] = -p.ypoints[i / 4] / ((float) Window.getHeight()) * 2f + 1;
			this.vertexBuffer[i + 2] = (i == 0 || i == 4 ? 0 : 1);
			this.vertexBuffer[i + 3] = (i == 0 || i == 12 ? 0 : 1);
		}
	}

	public void move(Vector3f offset) {
		position.add(offset);
		children.forEach((c) -> c.position.add(offset));
	}

	public void addChild(GameObject child) {
		if (children == null)
			children = new ArrayList<GameObject>();
		children.add(child);
	}

	public void render() {
		if (null == children) {

			if (!initialised)
				init();
			sh.bind();
			va.bind();
			ib.bind();
			texture.bind(0);
			sh.setUniform4f("u_offset", position.x, position.y, 0, 0);
			GL11.glDrawElements(GL11.GL_TRIANGLES, ib.getDrawCount(), GL11.GL_UNSIGNED_INT, 0);
			// TODO: unbind

		} else {
			children.forEach((e) -> e.render());
		}
	}

	public void init() {
		va = new VertexArray();
		vb = new VertexBuffer(vertexBuffer);
		vl = new VertexBufferLayout();
		ib = new IndexBuffer(new int[] { 0, 1, 2, 3, 2, 0 });
		sh = new Shader(shader);

		vl.push(GL11.GL_FLOAT, 2);
		vl.push(GL11.GL_FLOAT, 2);
		va.addBuffer(vb, vl);

		texture = new Texture(texturePath);
		texture.bind(0);
		sh.bind();
		sh.setUniform1i("u_texture", 0);

		initialised = true;
	}
}