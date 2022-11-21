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
	
	public FileCompressor(String sourceFile, String destinationFile) 
			throws IOException {
		tree = new HuffmanTree();
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
		prepareData();
	}
	
	private void prepareData() throws IOException {
		PriorityQueue<Node> heap = FrequencyCalculator
				.getHeapOfKeysRepetitions(FrequencyCalculator
						.countRepetitionsOfKeys(sourceFile));
		
		tree.buildTree(heap);
		codes = tree.getMappingOfCodesToKeys();
	}
	
	public void writeMetadataToDestinationFile() throws IOException {
		
		checkOfDestinationFile();
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(destinationFile))) {
			
			writeOfWordsAmount(bos);
			tree.createAllSequences();
			writeOfWords(bos);
			writeAmountOfParents(bos);
			
		}
	}
	
	private void checkOfDestinationFile() throws IOException {
		
		File df = new File(destinationFile);
		
		if (!df.isFile()) {
			if (!df.createNewFile()) {
				throw new IOException("File with this name cannot be created");
			}
		}
	}
	
	private void writeOfWordsAmount(BufferedOutputStream bos) 
			throws IOException {
		
		if (tree.getLeafsAmount() == 65536) { 
			bos.write(0);
			bos.write(0);
			return;
		}
		bos.write(tree.getLeafsAmount() >>> 8);
		bos.write(tree.getLeafsAmount());
	}
	
	private void writeOfWords(BufferedOutputStream bos) throws IOException {
		
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
	/*
	private void writeParents(BufferedOutputStream bos) throws IOException {
		
		boolean[] sequenceOfParents = tree.nodesSequence
				.getSequenceOfParents();
		
	}*/
}
