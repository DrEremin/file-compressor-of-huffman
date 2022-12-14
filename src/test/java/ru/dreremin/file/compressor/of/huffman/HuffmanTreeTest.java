package ru.dreremin.file.compressor.of.huffman;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
public class HuffmanTreeTest {
	
	private String sourcePath;
	private HuffmanTree tree;
	private PriorityQueue<Node> heap;
	
	@BeforeAll
	void beforeAll() { sourcePath = System.getenv().get("SOURCE_PATH"); }
	
	@BeforeEach
	void beforeEach() throws IOException {
		heap = FrequencyCalculator.getHeapOfKeysRepetitions(
				FrequencyCalculator.countRepetitionsOfKeys(sourcePath));
		tree = new HuffmanTree();
	}
	
	@Test
	void buildTree_Success() {
		Assertions.assertDoesNotThrow(() -> tree.buildTree(heap));
		Assertions.assertEquals(9, tree.getLeafsAmount());
		Assertions.assertEquals(17, tree.getSizeTree());
	}
	
	@Test
	void getMappingOfCodesToKeys_Success() {
		tree.buildTree(heap);
		
		HashMap<Short, String> result = tree.getMappingOfCodesToKeys();
		HashSet<String> stringsSet = new HashSet<>();
		boolean isUnique = true;
		
		for (String str : result.values()) {
			if (!(isUnique = stringsSet.add(str))) { break; }
		}
		Assertions.assertTrue(isUnique);
	}
	
	@Test
	void createAllSequences_Success() {
		tree.buildTree(heap);
		Node node = tree.getRoot();
		short firstKey, lastKey;
		
		while (node.getLeftSon() != null) {
			node = node.getLeftSon();
		}
		firstKey = node.getKey();
		node = tree.getRoot();
		while (node.getRightSon() != null) {
			node = node.getRightSon();
		}
		lastKey = node.getKey();
		tree.createAllSequences();
		
		Assertions.assertEquals(9, 
				tree.nodesSequence.getWordsSequence().length);
		Assertions.assertEquals(firstKey, 
				tree.nodesSequence.getWordsSequence()[0]);
		Assertions.assertEquals(lastKey, 
				tree.nodesSequence.getWordsSequence()[5]);
		
		boolean[] expectedArray = {
				false, false,
				true, false,
				false, false,
				true, true,
				true, true,
				false, true,
				false, true, 
				true, true };
		
		boolean[] actualArray = tree.nodesSequence.getSequenceOfParents();
		
		for (int i = 0; i < expectedArray.length; i++) {
			Assertions.assertEquals(expectedArray[i], actualArray[i]);
		}
	}
	
	@AfterAll
	void afterAll() {
		sourcePath = null;
		heap = null;
		tree = null;
	}
}
