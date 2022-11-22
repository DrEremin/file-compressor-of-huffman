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

@TestInstance(Lifecycle.PER_CLASS)
public class FrequencyCalculatorTest {
	
	private String sourcePath;
	private HashMap<Short, Integer> control;
	
	@BeforeAll
	void beforeAll() {
		sourcePath = System.getenv().get("SOURCE_PATH");
		control = new HashMap<>();
		control.put((short) 18533, 1);
		control.put((short) 27756, 1);
		control.put((short) 28448, 1);
		control.put((short) 22383, 1);
		control.put((short) 29292, 1);
		control.put((short) 25633, 1);
		control.put((short) 8481, 1);
		control.put((short) 8448, 1);
		
	}

	@Test
	void countRepetitionsOfKeys_Success() throws IOException {
		
		HashMap<Short, Integer> map = 
				FrequencyCalculator.countRepetitionsOfKeys(sourcePath);
		
		Assertions.assertEquals(control.size(), map.size());
		for (Map.Entry<Short, Integer> entry : control.entrySet()) {
			Assertions.assertEquals(entry.getValue(), map.get(entry.getKey()));
		}
	}
	
	@Test
	void getHeapOfKeysRepetitions_Success() {
		
		short[] values = { 8448, 8481, 18533 };
		PriorityQueue<Node> heap = 
				FrequencyCalculator.getHeapOfKeysRepetitions(control);
		
		Assertions.assertEquals(control.size(), heap.size());
		for (int i = 0; i < values.length; i++) {
			Assertions.assertEquals(values[i], heap.poll().getKey());
		}
	}
}
