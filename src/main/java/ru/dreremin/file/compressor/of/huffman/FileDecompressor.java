package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FileDecompressor {
	
	public HuffmanTree tree;
	private String compressedFile;
	private String recoveredFile;
	private HashMap<Short, String> codesMap;
	
	public FileDecompressor(String compressedFile, String recoveredFile) 
			throws IOException {
		tree = new HuffmanTree();
		this.compressedFile = compressedFile;
		this.recoveredFile = recoveredFile;
		this.codesMap = new HashMap<>();
	}
	
	public void recoverFile() throws IOException {
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(compressedFile))) {
			
			checkOfCompressedFile();
			
			int amountOfWords = readAmountOfWords(bis);
			short[] words = readWords(bis, amountOfWords);
			int[] parentsBytes = readParentsBytes(bis, amountOfWords);
			
			tree.buildTree(words, parentsBytes);
		}
	}
	
	private void checkOfCompressedFile() throws IOException {
		
		File df = new File(compressedFile);
		
		if (!df.isFile()) {
			if (!df.createNewFile()) {
				throw new IOException("File with this name cannot be created");
			}
		}
	}
	
	private int readAmountOfWords(BufferedInputStream bis) throws IOException {
		
		int result = (bis.read() << 8) | bis.read();
		if (result < 0) { 
			throw new EOFException("Amount of words was not read"); 
		}
		return result;
	}
	
	private short[] readWords(BufferedInputStream bis, int length) 
			throws IOException {
		
		short[] words = new short[length];
		for (int i = 0; i < words.length; i++) {
			words[i] = (short)bis.read();
			if (words[i] < 0) { 
				throw new EOFException("Word " + i + " was not read"); 
			}
		}
		return words;
	}
	
	private int[] readParentsBytes(BufferedInputStream bis, int amountOfWords) 
			throws IOException {
		
		amountOfWords = (amountOfWords - 1) * 2;
		
		int bytesOfAmount = ((amountOfWords) % 8 == 0) 
				? (amountOfWords) / 8 
				: (amountOfWords) / 8 + 1;
		int[] parentsBytes = new int[bytesOfAmount];
		
		for (int i = 0; i < bytesOfAmount; i++) {
			parentsBytes[i] = bis.read();
			if (parentsBytes[i] < 0) {
				throw new EOFException(
						"Byte of parents" + i + " was not read");
			}
		}
		return parentsBytes;
	}
	
}
