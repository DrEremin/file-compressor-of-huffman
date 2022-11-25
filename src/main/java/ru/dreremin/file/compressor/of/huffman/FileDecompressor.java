package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDecompressor {
	
	public HuffmanTree tree;
	private String compressedFile;
	private String recoveredFile;
	
	public FileDecompressor(String compressedFile, String recoveredFile) 
			throws IOException {
		tree = new HuffmanTree();
		this.compressedFile = compressedFile;
		this.recoveredFile = recoveredFile;
	}
	
	public void recoverFile() throws IOException {
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(compressedFile))) {
			
			checkOfCompressedFile();
			
			int amountOfWords = readAmountOfWords(bis);
			short[] words = readWords(bis, amountOfWords);
			int[] parentsBytes = readParentsBytes(bis, amountOfWords);
			
			tree.buildTree(words, parentsBytes);
			decodingAndWriteUsefulData(bis, 
					readRemainderOfBitsForReading(bis));
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
	
	private int readRemainderOfBitsForReading(BufferedInputStream bis) 
			throws IOException {
		
		int remainder = bis.read();
		if (remainder < 0) {
			throw new EOFException(
					"Remainder of bits for reading was not read"); 
		}
		return remainder;
	}
	
	private void decodingAndWriteUsefulData(BufferedInputStream bis, 
			int remainder)  throws IOException {
		
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(recoveredFile))) {
			
			int curByte;
			
			remainder = (remainder != 0) ? 8 - remainder : 8;
			tree.resetCurrentNode();
			while (bis.available() > 1) {
				curByte = bis.read();
				if (curByte < 0) {
					throw new EOFException(
							"Byte with code of Huffman was not read"); 
				}
				for (int shift = 7; shift >= 0; shift--) {
					if (tree.moveCurrentNode(((curByte >> shift) & 1) == 1)) {
						bos.write(tree.getByte());
						tree.resetCurrentNode();
					}
				}
			}
			curByte = bis.read();
			if (curByte < 0) {
				throw new EOFException(
						"Byte with code of Huffman was not read"); 
			}
			for (int shift = 7; shift >= remainder; shift--) {
				if (tree.moveCurrentNode(((curByte >> shift) & 1) == 1)) {
					bos.write(tree.getByte());
					tree.resetCurrentNode();
				}
			}
		}
	}
}
