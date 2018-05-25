package com.abysmal.slae.util.datastructure;

import java.util.ArrayList;

public class Queue<T> {

	private ArrayList<T> queue = new ArrayList<T>();

	public T next() {
		if (queue.isEmpty())
			throw new IndexOutOfBoundsException("Queue is empty");
		T result = queue.remove(0);
		return result;
	}
	
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	public void add(T data) {
		queue.add(data);
	}

}