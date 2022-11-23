package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.math.BigInteger;

public class FileCompressor {

	private HuffmanTree tree;
	private String sourceFile;
	private String destinationFile;
	private HashMap<Short, Integer> repetitionsMap;
	private HashMap<Short, String> codesMap;
	
	public FileCompressor(String sourceFile, String destinationFile) 
			throws IOException {
		tree = new HuffmanTree();
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
		prepareData();
	}
	
	private void prepareData() throws IOException {
		
		repetitionsMap = FrequencyCalculator
				.countRepetitionsOfKeys(sourceFile);
		PriorityQueue<Node> heap = FrequencyCalculator
				.getHeapOfKeysRepetitions(repetitionsMap);
		
		tree.buildTree(heap);
		codesMap = tree.getMappingOfCodesToKeys();
	}
	/*
	public void writeAllСompressedDataToDestinationFile() throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(destinationFile))) {
			writeMetadataToDestinationFile(bos);
		}
	}*/
	
	public void writeMetadataToDestinationFile(BufferedOutputStream bos) 
			throws IOException {
		
		checkOfDestinationFile();
		writeAmountOfWords(bos);
		tree.createAllSequences();
		writeWords(bos);
		writeAmountOfParents(bos);
		writeParents(bos);
		
		BigInteger big = getAmountBitsOfData();
		
		writeAmountBytesOfData(bos, big);
		writeRemainderOfBitsForReading(bos, big);
	}
	
	private void checkOfDestinationFile() throws IOException {
		
		File df = new File(destinationFile);
		
		if (!df.isFile()) {
			if (!df.createNewFile()) {
				throw new IOException("File with this name cannot be created");
			}
		}
	}
	
	private void writeAmountOfWords(BufferedOutputStream bos) 
			throws IOException {
		
		if (tree.getLeafsAmount() == 65536) { 
			bos.write(0);
			bos.write(0);
		} else {
			bos.write(tree.getLeafsAmount() >>> 8);
			bos.write(tree.getLeafsAmount());
		}
	}
	
	private void writeWords(BufferedOutputStream bos) throws IOException {
		
		short[] wordsSequence = tree.nodesSequence.getWordsSequence();
		
		for (int i = 0; i < wordsSequence.length; i++) {
			bos.write(wordsSequence[i] >>> 8);
			bos.write(wordsSequence[i]);
		}
	}
	
	private void writeAmountOfParents(BufferedOutputStream bos) 
			throws IOException {
		
		int amount = tree.nodesSequence.getSequenceOfParents().length / 2;
	
		bos.write(amount >> 8);
		bos.write(amount);
	}
	
	private void writeParents(BufferedOutputStream bos) throws IOException {
		
		boolean[] sequenceOfParents = 
				tree.nodesSequence.getSequenceOfParents();
		int length = (sequenceOfParents.length % 8 == 0) 
				? (sequenceOfParents.length / 8) 
				: (sequenceOfParents.length / 8 + 1);			
		int[] bytesForWrite = new int[length];
		int j = 128;
		
		for (int i = 0, k = -1; i < sequenceOfParents.length; i++) {
			if (i % 8 == 0) { 
				j = 128; 
				k++;
			}
			if (sequenceOfParents[i]) { bytesForWrite[k] |= j; }
			j >>>= 1;
		}
		for (int i = 0; i < bytesForWrite.length; i++) {
			bos.write(bytesForWrite[i]);
		}
	}
	
	private BigInteger getAmountBitsOfData() {
		
		BigInteger big = BigInteger.ZERO;
		
		for (Map.Entry<Short, Integer> entry : repetitionsMap.entrySet()) {
			big = big.add(BigInteger.valueOf(
					entry.getValue() * codesMap.get(entry.getKey()).length()));
		}
		return big;
	}
	
	public void writeAmountBytesOfData(BufferedOutputStream bos, 
			BigInteger big) throws IOException {
		
		long amount = big.divide(BigInteger.valueOf(8)).longValue();
		
		amount = (big.remainder(BigInteger.valueOf(8)).intValue() == 0) 
				? amount : amount + 1;
		for (int shift = 56; shift >= 0; shift -= 8) {
			bos.write((int)(amount >> shift));
		}
	}
	
	private void writeRemainderOfBitsForReading(BufferedOutputStream bos, 
			BigInteger big) throws IOException {
		bos.write(big.remainder(BigInteger.valueOf(8)).intValue());
	}
	/*
	public void writeUsefulDataToDestinationFile(BufferedOutputStream bos) 
			throws IOException {
		
		String code;
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(sourceFile))) {
			
			code = codesMap.get(FrequencyCalculator
					.getShortFromTwoBytes(bis.read(), bis.read()));
			
		}
	}*/
}
