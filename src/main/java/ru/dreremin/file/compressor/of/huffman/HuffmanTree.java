package ru.dreremin.file.compressor.of.huffman;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HuffmanTree {
	
	private Node root;
	private Node currentNode;
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
	
	public void buildTree(short[] words, int[] parentsBytes) {
		
		root = new Node();
		leafsAmount = words.length;
		sizeTree = 2 * leafsAmount - 1;
		
		Node cur = root;
		LinkedList<Node> unvisitedNodes = new LinkedList<>();
		for (int i = 0, k = 0; k <= words.length - 1; i++) {
			for (int j = 6, val; j >= 0; j -= 2) {
				val = (parentsBytes[i] >>> j) & 3;
				if (val == 0) {
					cur.setLeftSon(new Node(false));
					cur.setRightSon(new Node(true));
					unvisitedNodes.addLast(cur.getRightSon());
					cur = cur.getLeftSon();
				} else if (val == 2) {
					cur.setLeftSon(new Node(words[k++], 0L, false));
					cur.setRightSon(new Node(true));
					cur = cur.getRightSon();
				} else if (val == 1) {
					cur.setLeftSon(new Node(false));
					cur.setRightSon(new Node(words[k++], 0L, true));
					cur = cur.getLeftSon();
				} else {
					cur.setLeftSon(new Node(words[k++], 0L, false));
					cur.setRightSon(new Node(words[k++], 0L, true));
					if (k > words.length - 1) { break; }
					cur = unvisitedNodes.pollLast();
				}
			}
		}
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
		
		LinkedList<Node> unvisitedNodes = new LinkedList<>();
		Node cur = root;
		int lim = leafsAmount, nwCounter = 0, wCounter = 0;
		
		if (leafsAmount == 1) {
			nodesSequence.wordsSequence[wCounter++] = cur.getKey();
		} else {
			while (lim > 0) {
					
				if (cur.getLeftSon().getKey() == null 
						&& cur.getRightSon().getKey() == null) {
					nodesSequence.sequenceOfParents[nwCounter++] = false;
					nodesSequence.sequenceOfParents[nwCounter++] = false;
					unvisitedNodes.addLast(cur.getRightSon());
					cur = cur.getLeftSon();
				} else if (cur.getLeftSon().getKey() == null 
						&& cur.getRightSon().getKey() != null) {
					nodesSequence.sequenceOfParents[nwCounter++] = false;
					nodesSequence.sequenceOfParents[nwCounter++] = true;
					nodesSequence.wordsSequence[wCounter++] = 
							cur.getRightSon().getKey();
					lim--;
					cur = cur.getLeftSon();
				} else if (cur.getLeftSon().getKey() != null 
						&& cur.getRightSon().getKey() == null) {
					nodesSequence.sequenceOfParents[nwCounter++] = true;
					nodesSequence.sequenceOfParents[nwCounter++] = false;
					nodesSequence.wordsSequence[wCounter++] = 
							cur.getLeftSon().getKey();
					lim--;
					cur = cur.getRightSon();
				} else {
					nodesSequence.sequenceOfParents[nwCounter++] = true;
					nodesSequence.sequenceOfParents[nwCounter++] = true;
					nodesSequence.wordsSequence[wCounter++] = 
							cur.getLeftSon().getKey();
					nodesSequence.wordsSequence[wCounter++] = 
							cur.getRightSon().getKey();
					lim -= 2;
					cur = unvisitedNodes.pollLast();
				}
			}
		}
	}
	
	public void resetCurrentNode() { currentNode = root; }
	
	public int getByte() { return currentNode.getKey(); }
	
	public boolean moveCurrentNode(boolean side) {
		currentNode = (side) 
				? currentNode.getRightSon() : currentNode.getLeftSon();
		return currentNode.getKey() != null;
	}
	
	@Override
	public String toString() {
		
		int lim = leafsAmount;
		LinkedList<Node> unvisitedNodes = new LinkedList<>();
		LinkedList<Node> visitedNodes = new LinkedList<>();
		Node cur = root;
		Node parent;
		StringBuilder builder = new StringBuilder();
		boolean flag = false;
		
		while (lim > 0) {
			builder.append("{");
			builder.append(cur);
			if (cur.getLeftSon() != null && cur.getLeftSon() != null) {
				visitedNodes.add(cur);
				unvisitedNodes.add(cur.getRightSon());
				cur = cur.getLeftSon();
				flag = false;
			} else {
				if (flag) {
					parent = visitedNodes.pollLast();
					while (parent != null && parent.getRightSon() == cur) {
						builder.append("}");
						cur = parent;
						parent = visitedNodes.pollLast();
					}
					visitedNodes.add(parent);
				} 
				builder.append("}");
				flag = true;
				cur = unvisitedNodes.pollLast();
				lim--;
			}
		}
		return builder.toString();
	}
}
