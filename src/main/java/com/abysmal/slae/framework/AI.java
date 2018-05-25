package com.abysmal.slae.framework;

import java.util.ArrayList;

import com.abysmal.slae.util.Path;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class AI {

	ArrayList<Node> open = new ArrayList<Node>(), closed = new ArrayList<Node>(), pathNodes = new ArrayList<Node>();

	public Path Pathfind(Vector3i pos, Vector3i goal) { // Figure out how to store the world
		Node end = new Node(1, goal);
		Node start = new Node(1, pos, end);
		open.add(start);

		while (true) {
			Node current = open.get(0);
			for (Node s : open) {
				if (s.tCost < current.tCost || (s.tCost == current.tCost && s.hCost < current.hCost)) { //Optimize for redundancy
					current = s;
				} else {
				}
			}
			open.remove(current);
			closed.add(current);

			if (current.pos.distance(goal) < 1) {
				while (!current.equals(start)) {
					pathNodes.add(current);
					current = current.parent;
				}
				break;
			}

			Node[] neighbors = new Node[8];

			for (int i = 0; i < neighbors.length + 1; i++) {
				if (i == 5)
					i++;
				neighbors[i] = new Node(1, new Vector3i(i % 3 - 1, (i / 3) - 1, 0).add(current.pos), end);
				System.out.println(i / 3 - 1); //TODO: syso
			}

			for (Node n : neighbors) {
				if (null == n)
					continue;
				if (!n.traversable) {
					n = new Node(1, n.pos.add(0, 1, 0), end);
					if (!n.traversable) {
						n = new Node(1, n.pos.sub(0, 1, 0), end);
						if (!n.traversable)
							continue;
					}
				}

				boolean isin = false;

				for (Node c : closed)
					if (c.pos.distance(c.pos) > 1) {
						isin = true;
						break;
					}

				if (isin)
					continue; //Change to label?

				double[] costs = n.calculateCosts(current, end);
				int i = -1;
				for (Node c : open)
					if (c.pos.distance(n.pos) < 1) {
						i = open.indexOf(c);
						break;
					}

				if (!open.contains(n) && i == -1) {
					n.parent = current;
					open.add(n);
					n.gCost = costs[0];
					n.hCost = costs[1];
					n.tCost = costs[2];
				} else if (open.get(i).tCost > costs[2]) {
					n.parent = current;
				}
			}
		}

		Vector3f[] points = new Vector3f[pathNodes.size()];
		pathNodes.forEach((i) -> {
			points[pathNodes.size() - pathNodes.indexOf(i) - 1] = new Vector3f(i.pos);
		});
		return new Path(points);
	}

	private class Node {

		Node parent;
		double hCost, weight, tCost, gCost;
		boolean traversable;
		Vector3i pos;

		public Node(int weight, Vector3i pos) {
			this(weight, pos, null);
		}

		public Node(int weight, Vector3i pos, Node end) {
			this(weight, pos, true, end);
			gCost = 1;
		}

		public Node(double weight, Vector3i pos, boolean traversable, Node end) {
			this.weight = weight;
			this.pos = pos;
			this.traversable = traversable;
			double[] costs = calculateCosts(parent, end);
			gCost = costs[0];
			hCost = costs[1];
			tCost = costs[2];
		}

		double[] calculateCosts(Node p, Node end) {
			double gc = weight;
			if (p != null)
				gc = (p.gCost + weight * p.pos.distance(pos));
			double hc = calculateHeuristicCost(end);
			double tc = gc + hc;

			double[] costs = { gc, hc, tc };
			return costs;
		}

		double calculateHeuristicCost(Node end) {
			if (end == null || end.pos == null)
				return 0;
			return end.pos.distance(pos);
		}
	}
}