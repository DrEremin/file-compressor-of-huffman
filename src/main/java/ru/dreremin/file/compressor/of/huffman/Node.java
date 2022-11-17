package ru.dreremin.file.compressor.of.huffman;

public class Node implements Comparable<Node>{

	private boolean bitValue;
	private Short key;
	private int amount;
	private Node leftSon;
	private Node rightSon;
	
	public Node(Short key, int amount) {
		bitValue = false;
		this.key = key;
		this.amount = amount;
		this.leftSon = null;
		this.rightSon = null;
	}
	
	public Node(Node leftSon, Node rightSon) {
		bitValue = false;
		this.key = null;
		this.amount = leftSon.getAmount() + rightSon.getAmount();
		this.leftSon = leftSon;
		this.rightSon = rightSon;
		leftSon.setBitValue(false);
		rightSon.setBitValue(true);
	}
	
	public void setBitValue(boolean bitValue) { this.bitValue = bitValue; }
	
	public boolean getBitValue() { return bitValue; }
	
	public Short getKey() { return key; }
	
	public int getAmount() { return amount; }
	
	public Node getLeftSon() { return leftSon; }
	
	public Node getRightSon() { return rightSon; }
	
	@Override
	public int compareTo(Node other) {
		
		int difference = this.amount - other.getAmount();
		
		if (difference != 0) { 
			return difference; 
		}
		if (this.key == null && other.getKey() != null) {
			return -1;
		} else if (this.key != null && other.getKey() == null) {
			return 1;
		} else if (this.key == null && other.getKey() == null) {
			return -1;
		} else {
			return this.key.compareTo(other.getKey());
		}
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[")
				.append(key)
				.append(", ")
				.append(amount)
				.append("]")
				.toString();
	}
}
