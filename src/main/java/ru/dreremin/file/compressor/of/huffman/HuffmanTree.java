package ru.dreremin.file.compressor.of.huffman;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HuffmanTree {
	
	private Node root;
	private int sizeTree;
	private int leafsAmount;
	
	public HuffmanTree() {
		root = null;
		sizeTree = 0;
		leafsAmount = 0;
	}
	
	public void buildTree(PriorityQueue<Node> heap) {
		
		if (heap == null) { return; }
		
		Node nodeLeft, nodeRight;
		
		leafsAmount = heap.size();
		while (heap.size() > 1) {
			nodeLeft = heap.poll();
			nodeRight = heap.poll();
			heap.add(new Node(nodeLeft, nodeRight));
			sizeTree += 2;
		}
		root = heap.poll();
		sizeTree++;
	}
	
	public HashMap<Short, String> getMappingOfCodesToKeys() {
		
		HashMap<Short, String> map = new HashMap<>();
		int lim = leafsAmount;
		LinkedList<Node> nodes = new LinkedList<>();
		Node cur, del = null;
		StringBuilder prefix = new StringBuilder();
		
		nodes.addLast(root);
		while (lim > 0) {
			cur = nodes.peekLast();
			prefix.append(cur.getBitValue() ? "1" : "0");
			if (cur.getRightSon() != null && cur.getRightSon() != del) {
				nodes.addLast(cur.getRightSon());
				nodes.addLast(cur.getLeftSon());
			} else {
				del = nodes.pollLast();
				if (cur.getRightSon() == null) { 
					lim--;
					map.put(del.getKey(), prefix.substring(1));
				} else {
					prefix.deleteCharAt(prefix.length() - 1);
				}
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}
		return map;
	}
	
	@Override
	public String toString() {
		
		int lim = sizeTree;
		LinkedList<Node> nodes = new LinkedList<>();
		Node cur, del = null;
		StringBuilder builder = new StringBuilder("{");
		
		nodes.addLast(root);
		while (lim > 0) {
			cur = nodes.peekLast();
			if (cur.getRightSon() != null && cur.getRightSon() != del) {
				nodes.addLast(cur.getRightSon());
				nodes.addLast(cur.getLeftSon());
			} else {
				del = nodes.pollLast();
				lim--;
				builder.append(del);
				builder.append(" ");
			}
		}
		return builder.deleteCharAt(builder.length() - 1)
				.append("}")
				.toString();
	}
}
