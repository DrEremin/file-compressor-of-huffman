package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

/**
 * The absolute paths to the files "source_file" and "destination_file" 
 * were writ to the environment variables SOURCE_PATH and DESTINATION_PATH. 
 * These files are in folder "src/test/resources".
 * These environment variables must be configured for the tests to correct 
 * work. All testing relies on the data in the file "source_file". 
 * This file contains: 
 * Hello World!!!!
 */

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
		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(destinationPath))) {
			fc.writeMetadataToDestinationFile(bos);
		}
	}
	
	@Test
	void wordsWereWrittenSuccessfully() throws IOException {
		
		HashMap<Short, Long> map = 
				FrequencyCalculator.countRepetitionsOfKeys(sourcePath);
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(destinationPath))) {
			int temp = (bis.read() << 8) | bis.read();
			Assertions.assertTrue(temp == 9);
			for (int i = 0; i < temp; i++) {
				Assertions.assertTrue(map.containsKey(
						(short)bis.read()));
			}
			Assertions.assertEquals(temp, map.values().size());
		}
	}
	
	@Test
	void parentsWereWrittenSuccesfully() throws IOException {
		
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(destinationPath))) {
			bis.skip(11);
			//Assertions.assertTrue(((bis.read() << 8) | bis.read()) == 8);
			Assertions.assertTrue(bis.read() == 35);
			Assertions.assertTrue(bis.read() == 215);
		}
	}
	
	@Test
	void informationAboutAmountOfUsefulDataWasWrittenSuccessfully () 
			throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(destinationPath))) {
			bis.skip(13);
			/*
			for (int i = 0; i < 7; i++) {
				Assertions.assertTrue(bis.read() == 0);
			}
			Assertions.assertTrue(bis.read() == 6);*/
			Assertions.assertTrue(bis.read() == 4);
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
