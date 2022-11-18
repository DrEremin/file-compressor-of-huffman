package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class FileCompressor {

	private HuffmanTree tree;
	private String sourceFile;
	private String destinationFile;
	private HashMap<Short, String> codes;
	
	public FileCompressor(String sourceFile, String DestinationFile) {
		tree = new HuffmanTree();
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
	}
	
	public void writeMetadataToDestinationFile() throws IOException {
		
		HashMap<Short, Integer> map = FrequencyCalculator
				.countRepetitionsOfKeys(sourceFile);
		File df = new File(destinationFile);
		
		if (!df.isFile()) {
			if (!df.createNewFile()) {
				throw new IOException("File with this name cannot be created");
			}
		}
		
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(destinationFile))) {
			
			bos.write(map.size() >>> 8);
			bos.write(map.size());
			for (Map.Entry<Short, Integer> entry : map.entrySet()) {
				bos.write((int)(entry.getKey() >>> 8));
				bos.write((int)entry.getKey());
				int[] bytes = getBytesOfAmountOfWordRepetitions(getBytesAmount(
						getBitsAmount(entry.getValue())), entry.getValue());
				for (int i = 0; i < bytes.length; i++) {
					bos.write(bytes[i]);
				}
			}
		}
	}
	
	/*
	public void prepareData() throws IOException {
		PriorityQueue<Node> heap = FrequencyCalculator
				.getHeapOfKeysRepetitions(FrequencyCalculator
						.countRepetitionsOfKeys(sourceFile));
		
		tree.buildTree(heap);
		codes = tree.getMappingOfCodesToKeys();
		System.out.println(codes);
	}*/
	
	private int[] getBytesOfAmountOfWordRepetitions(
			int bytesAmount, 
			int repetitionsAmount) {
		
		int[] bytes = new int[bytesAmount];
		int shiftsCounter = 0;
		
		bytes[0] = repetitionsAmount;
		while (bytes[0] > 31) {
			bytes[0] >>>= 1;
			shiftsCounter++;
		}
		bytes[0] |= (bytesAmount << 5);
		bytesAmount--; 
		for (int i = 1; bytesAmount > 0; i++, bytesAmount--) {
			bytes[i] = repetitionsAmount << (32 - shiftsCounter);
			shiftsCounter -= 8;
			bytes[i] >>>= 24;
		}
		return bytes;
	}
	
	
	private int getBitsAmount(int number) {
		
		int bitCounter = 0;
		
		while (number != 0) {
			number >>>= 1;
			bitCounter++;
		}
		return bitCounter;
	}
	
	private int getBytesAmount(int bitsAmount) {
		return (((bitsAmount % 8) - 5) > 0) 
				? bitsAmount / 8 + 2 : bitsAmount / 8 + 1;
	}
}
