package ru.dreremin.file.compressor.of.huffman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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
public class FrequencyCalculatorTest {
	
	private String sourcePath;
	private HashMap<Short, Long> control;
	
	@BeforeAll
	void beforeAll() {
		sourcePath = System.getenv().get("SOURCE_PATH");
		control = new HashMap<>();
		control.put((short) 72, 1L);
		control.put((short) 101, 1L);
		control.put((short) 108, 3L);
		control.put((short) 111, 2L);
		control.put((short) 32, 1L);
		control.put((short) 87, 1L);
		control.put((short) 114, 1L);
		control.put((short) 100, 1L);
		control.put((short) 33, 4L);
	}

	@Test
	void countRepetitionsOfKeys_Success() throws IOException {
		
		HashMap<Short, Long> map = 
				FrequencyCalculator.countRepetitionsOfKeys(sourcePath);
		
		Assertions.assertEquals(control.size(), map.size());
		for (Map.Entry<Short, Long> entry : control.entrySet()) {
			Assertions.assertEquals(entry.getValue(), map.get(entry.getKey()));
		}
	}
	
	@Test
	void getHeapOfKeysRepetitions_Success() {
		
		short[] values = { 32, 72, 87, 100, 101, 114, 111, 108, 33 };
		PriorityQueue<Node> heap = 
				FrequencyCalculator.getHeapOfKeysRepetitions(control);
		
		Assertions.assertEquals(control.size(), heap.size());
		for (int i = 0; i < values.length; i++) {
			Assertions.assertEquals(values[i], heap.poll().getKey());
		}
	}
}
