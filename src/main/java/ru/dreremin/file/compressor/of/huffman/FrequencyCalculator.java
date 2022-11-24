package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class FrequencyCalculator {
	
	public static HashMap<Short, Long> countRepetitionsOfKeys(
			String fileSource) throws IOException{
		
		HashMap<Short, Long> result = new HashMap<>();	
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(fileSource))) {
			
			short key;
			
			while (bis.available() > 0) {
				key = (short)bis.read();
				long newValue = (result.containsKey(key)) 
						? result.get(key).intValue() + 1 : 1;
				result.put(key, newValue);
			}
		}
		return result;
	}
	
	public static PriorityQueue<Node> getHeapOfKeysRepetitions(
			HashMap<Short, Long> map) {
		
		PriorityQueue<Node> heap = new PriorityQueue<>();
		
		for (Map.Entry<Short, Long> entry : map.entrySet()) {
			heap.add(new Node(entry.getKey(), entry.getValue()));
		}
		return heap;
	}
}
