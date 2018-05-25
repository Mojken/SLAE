package com.abysmal.slae.util;

import org.joml.Vector3f;

public class Path {

	Vector3f[] points;
	int index = 0;

	public Path(Vector3f[] points) {
		this.points = points;
	}

	public Vector3f next() {
		if (index == points.length)
			return null;
		index++;
		while (points[index - 1].angle(points[index]) > 0.01)
			index++;
		return points[index - 1];
	}
}