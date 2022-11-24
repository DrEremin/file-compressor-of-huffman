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
	
	public void buildTree(int [] words, int[] parentsBytes) {
		
		root = new Node();
		sizeTree++;
		
		Node cur = root;
		LinkedList<Node> list = new LinkedList<>();
		
		for (int i = 0, k = 0; k <= words.length - 1; i++) {
			for (int j = 6, val; j >= 0; j -= 2) {
				val = (parentsBytes[i] >>> j) & 3;
				sizeTree += 2;
				if (val == 0) {
					cur.setLeftSon(new Node());
					cur.setRightSon(new Node());
					list.add(cur.getRightSon());
					cur = cur.getLeftSon();
				} else if (val == 2) {
					cur.setLeftSon(new Node((short)words[k++], 1L));
					cur.setRightSon(new Node());
					cur = cur.getRightSon();
					leafsAmount++;
				} else if (val == 1) {
					cur.setLeftSon(new Node());
					cur.setRightSon(new Node((short)words[k++], 1L));
					cur = cur.getLeftSon();
					leafsAmount++;
				} else {
					cur.setLeftSon(new Node((short)words[k++], 1L));
					cur.setRightSon(new Node((short)words[k++], 1L));
					leafsAmount += 2;
					if (k > words.length - 1) { break; }
					cur = list.getLast();
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
