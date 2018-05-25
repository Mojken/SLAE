package com.abysmal.slae.framework;

import java.util.ArrayList;
import java.util.List;

import com.abysmal.slae.object.GUIObject;
import com.abysmal.slae.object.GameObject;

public class Scene {

	private List<GUIObject> guiObjects = new ArrayList<GUIObject>();
	private List<GameObject> gameObjects = new ArrayList<GameObject>();

	public void addGameObject(GameObject object) {
		gameObjects.add(object);
	}

	public void addGUIObject(GUIObject object) {
		guiObjects.add(object);
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public List<GUIObject> getGUIObjects() {
		return guiObjects;
	}
}