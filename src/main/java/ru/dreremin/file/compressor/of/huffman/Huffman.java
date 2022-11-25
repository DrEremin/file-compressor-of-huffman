package ru.dreremin.file.compressor.of.huffman;

import java.io.IOException;

public class Huffman {

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			System.out.println("No arguments");
			return;
		}
		if (args[0].equals("compress")) {
			FileCompressor fc = new FileCompressor(args[1], args[2]);
			fc.writeAllСompressedDataToDestinationFile();
			System.out.println("Compression operation completed successfully");
		} else if (args[0].equals("extract")) {
			FileDecompressor fdc = new FileDecompressor(args[1], args[2]);
			fdc.recoverFile();
			System.out.println("Decompression operation completed successfully");
		} else {
			System.out.println("The first argument was entered incorrectly");
		}
	}
}
