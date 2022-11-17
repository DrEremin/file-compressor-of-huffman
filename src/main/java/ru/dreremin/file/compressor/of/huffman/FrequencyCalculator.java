package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class FrequencyCalculator {
	
	private static short getShortFromTwoBytes(int firstByte, int secondByte) {
		return (secondByte > 0) 
				? (short) ((firstByte << 8) | secondByte) 
						: (short) ((firstByte << 8));
	}
	
	public static HashMap<Short, Integer> countRepetitionsOfKeys(
			String fileSource) throws IOException{
		
		HashMap<Short, Integer> result = new HashMap<>();	
		
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileSource))) {
			
			short key;
			
			while (bis.available() > 0) {
				key = getShortFromTwoBytes(bis.read(), bis.read());
				Integer newValue = (result.containsKey(key)) 
						? result.get(key).intValue() + 1 : 1;
				result.put(key, newValue);
			}
		}
		return result;
	}
	
	public static PriorityQueue<Node> getHeapOfKeysRepetitions(
			HashMap<Short, Integer> map) {
		
		PriorityQueue<Node> heap = new PriorityQueue<>();
		
		for (Map.Entry<Short, Integer> entry : map.entrySet()) {
			heap.add(new Node(entry.getKey(), entry.getValue()));
		}
		return heap;
	}
}
