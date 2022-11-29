package ru.dreremin.file.compressor.of.huffman;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class FileDecompressorTest {

	private FileCompressor fc;
	private FileDecompressor fd;
	private String sourcePath;
	private String destinationPath;
	private String recoveredPath;
	
	@BeforeAll
	void beforeAll() throws IOException {
		sourcePath = System.getenv().get("SOURCE_PATH");
		destinationPath = System.getenv().get("DESTINATION_PATH");
		recoveredPath = System.getenv().get("RECOVERED_PATH");
		fc = new FileCompressor(sourcePath, destinationPath);
		fc.writeAllСompressedDataToDestinationFile();
	}
	
	@Test
	void recoverFile_Success() throws IOException {
		fd = new FileDecompressor(destinationPath, recoveredPath);
		fd.recoverFile();
		try (BufferedInputStream sourceStream = new BufferedInputStream(
				new FileInputStream(sourcePath));
				BufferedInputStream recoveredStream = new BufferedInputStream(
						new FileInputStream(recoveredPath));) {
			Assertions.assertEquals(sourceStream.available(), 
					recoveredStream.available());
			while (sourceStream.available() > 0) {
				Assertions.assertEquals(sourceStream.read(), 
						recoveredStream.read());
			}
		}
		
	}
	
	@AfterAll
	void afterAll() throws IOException {
		new FileOutputStream(recoveredPath).close();
		new FileOutputStream(destinationPath).close();
	}
}
