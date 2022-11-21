package ru.dreremin.file.compressor.of.huffman;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HuffmanTree {
	
	private Node root;
	private int sizeTree;
	private int leafsAmount;
	public NodesSequence nodesSequence;
	
	public HuffmanTree() {
		root = null;
		sizeTree = 0;
		leafsAmount = 0;
		nodesSequence = null;
	}
	
	public Node getRoot() { return root; }
	
	public int getSizeTree() { return sizeTree; }
	
	public int getLeafsAmount() { return leafsAmount; }
	
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
	
	public class NodesSequence {
		
		private short[] wordsSequence;
		private boolean[] sequenceOfParents;
		
		public NodesSequence() { 
			wordsSequence = new short[(sizeTree != 1) ? leafsAmount : 1];
			sequenceOfParents = (sizeTree != 1) 
					? new boolean[(sizeTree - leafsAmount) * 2] : null;
		}
		
		public short[] getWordsSequence() { return wordsSequence; }
		
		public boolean[] getSequenceOfParents() { return sequenceOfParents; }
	}
	
	public void createNodesSequence() { nodesSequence = new NodesSequence(); }
	
	public void deleteNodesSequence() { nodesSequence = null; }
	
	public void createAllSequences() {
		
		createNodesSequence();
		LinkedList<Node> nodes = new LinkedList<>();
		Node cur;
		int lim = leafsAmount, nwCounter = 0, wCounter = 0;
		
		nodes.add(root);
		while (lim > 0) {
			
			cur = nodes.pollLast();
			if (cur.getKey() != null) {
				nodesSequence.wordsSequence[wCounter++] = cur.getKey();
				lim--;
			} else {
				nodesSequence.sequenceOfParents[nwCounter++] = 
						(cur.getLeftSon().getKey() == null) ? false : true;
				nodesSequence.sequenceOfParents[nwCounter++] = 
						(cur.getRightSon().getKey() == null) ? false : true;
				nodes.add(cur.getRightSon());
				nodes.add(cur.getLeftSon());
			}
		}
		
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
