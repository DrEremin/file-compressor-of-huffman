package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class FileCompressorTest {

	private String sourcePath;
	private String destinationPath;
	private FileCompressor fc;
	
	@BeforeAll
	void beforeAll() throws IOException {
		sourcePath = System.getenv().get("SOURCE_PATH");
		destinationPath = System.getenv().get("DESTINATION_PATH");
		fc = new FileCompressor(sourcePath, destinationPath);
		fc.writeMetadataToDestinationFile();
	}
	
	@Test
	void wordsWereWrittenSuccessfully() throws IOException {
		
		HashMap<Short, Integer> map = 
				FrequencyCalculator.countRepetitionsOfKeys(sourcePath);
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(destinationPath))) {
			int temp = (bis.read() << 8) | bis.read();
			Assertions.assertTrue(temp == 8);
			for (int i = 0; i < temp; i++) {
				Assertions.assertTrue(map.containsKey(
						(short)((bis.read() << 8) | bis.read())));
			}
			Assertions.assertEquals(temp, map.values().size());
		}
	}
	
	@Test
	void parentsWereWrittenSuccesfully() throws IOException {
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(destinationPath))) {
			bis.skip(18);
			Assertions.assertTrue(((bis.read() << 8) | bis.read()) == 7);
			Assertions.assertTrue(bis.read() == 15);
			Assertions.assertTrue(bis.read() == 60);
		}
	}
	
	@AfterAll
	void afterAll() throws IOException {
		new FileOutputStream(destinationPath).close();
		sourcePath = null;
		destinationPath = null;
		fc = null;
	}
}
