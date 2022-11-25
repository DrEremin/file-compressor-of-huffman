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

	public HuffmanTree tree;
	private String sourceFile;
	private String destinationFile;
	private HashMap<Short, Long> repetitionsMap;
	private HashMap<Short, String> codesMap;
	boolean isPartialByte;
	
	public FileCompressor(String sourceFile, String destinationFile) 
			throws IOException {
		tree = new HuffmanTree();
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
		isPartialByte = false;
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
	
	public void writeAllСompressedDataToDestinationFile() throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(destinationFile))) {
			writeMetadataToDestinationFile(bos);
			writeUsefulDataToDestinationFile(bos);
		}
	}
	
	public void writeMetadataToDestinationFile(BufferedOutputStream bos) 
			throws IOException {
		
		checkOfDestinationFile();
		writeAmountOfWords(bos);
		tree.createAllSequences();
		writeWords(bos);
		writeParents(bos);
		
		BigInteger big = getAmountBitsOfData();
		
		isPartialByte = writeRemainderOfBitsForReading(bos, big);
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
		bos.write(tree.getLeafsAmount() >>> 8);
		bos.write(tree.getLeafsAmount());
	}
	
	private void writeWords(BufferedOutputStream bos) throws IOException {
		
		short[] wordsSequence = tree.nodesSequence.getWordsSequence();
		
		for (int i = 0; i < wordsSequence.length; i++) {
			bos.write(wordsSequence[i]);
		}
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
		
		for (Map.Entry<Short, Long> entry : repetitionsMap.entrySet()) {
			big = big.add(BigInteger.valueOf(
					entry.getValue() * codesMap.get(entry.getKey()).length()));
		}
		return big;
	}
	
	private boolean writeRemainderOfBitsForReading(BufferedOutputStream bos, 
			BigInteger big) throws IOException {
		
		int remainder = big.remainder(BigInteger.valueOf(8)).intValue();
		bos.write(remainder);
		return remainder != 0;
	}
	
	public void writeUsefulDataToDestinationFile(BufferedOutputStream bos) 
			throws IOException {
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(sourceFile))) {
			
			int bitIdx = 0;
			int curByte = 0;
			
			while (bis.available() > 0) {
				String	code = codesMap.get((short)bis.read());
				
				int charIdx = 0;
				
				while (charIdx < code.length()) {
					if (bitIdx == 8) {
						bos.write(curByte);
						curByte = 0;
						bitIdx = 0;
					}
					if (code.charAt(charIdx) == '1') {
						curByte |= (128 >> bitIdx);
					}
					bitIdx++;
					charIdx++;
				}
			}
			if (isPartialByte) { bos.write(curByte); }
		}
	}
}
