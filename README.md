# Description

This project provides functionality for compressing and restoring files using<br> 
Huffman coding. 

### Usage features

The program has the following command line interface:<br>
```
java Huffman <compress | extract> <source file> <destination file>
```
### Compression mechanism

At the first stage, information is read on one byte from source file. These<br>
bytes are added to the map where the keys are short-values of bytes, and values<br>
are long-values of repetitions of those bytes.<br>
The efficiency of inserting into the map is O(1), so the total efficiency of<br>
this stage is O(n).<br>
Further, based on this map, nodes are created and placed in a priority queue,<br>
where the node with the minimum number of repetitions lies at the top.<br>
The efficiency of inserting into the priority queue are O(logn), so the total<br>
efficiency this stage O(nlogn).<br>
The next stage, using the operation of extracting the top node from the queue<br> 
with priority, nodes with minimum values of repetitions ​​are join with the help<br> 
of new parent nodes and subtrees are obtained. The number of repetitions new<br>
nodes is the sum of repetitions of child nodes. As a result, each root of such<br>
a subtrees the number of repetitions is the sum of repetitions of all its<br>
leaves.<br> 
These subtrees are added back to the priority queue for further merging with<br>
other subtrees. This process continues until only one tree remains in the<br>
priority queue. In this way a Huffman tree is construct . <br>
The overall efficiency of the stage is O(nlogn).<br>
After building the Huffman tree, the binary Huffman code is computed for each<br>
leaf, which is the path from the root to the leaf.<br>
Then the word of each sheet is added to a new map in as the key, and the value<br>
will be the corresponding Huffman code.<br>
After that, the metadata is written to the compressed file.<br>
At the last stage, each byte is read again from the source file with<br>
simultaneous recording of the binary Huffman code corresponding to the current<br>
byte to a compressed file. The overall efficiency of this stage is O(n).<br><br>
The overall efficiency of the file compression process will be equal to:<br>
O(n) + O(nlogn) + O(nlogn) + O(n) = O(nlogn).<br>

### Metadata structure

#### Explanation

- Let's call the variants of bytes that occur in the source file words.
- The nodes in the Huffman tree that are not leaves let's call ancestors.<br>

#### Structure

1. The first two bytes contain the number of bytes of words to read.
2. Word bytes in order of addition when restoring the Huffman tree.
3. Bytes with pairs of bits encoding ancestors. If the number of words n, the<br>
number of ancestors is n-1.
    + The left bit of the pair encodes the connection with the left son, and the<br>
right one with the right one. If a bit was set to zero, then the current node<br>
is not associated with a leaf, otherwise it is associated with a leaf.
    + These bit pairs are in forward tree traversal order.<br>
(root - left subtree - right subtree).

    The first three points are the information that is needed for recovery Huffman<br>
tree when decompressing a file.

4. One byte with the number of bits required for reading in the last byte of the<br>
code Huffman sequences.
5. Bytes with Huffman code sequence.

### Unpacking mechanism

The first step is reading the metadata from the compressed file and restore<br>
Huffman tree. O(n) efficiency.<br>
Next, the Huffman code is read byte by byte. Each byte is parsed by bits.<br>
The value of each bit is used to navigate the Huffman tree. At when the leaf is<br>
reached (O(logn)) the byte stored in it is written to recoverable file. With<br>
number words equals n, the complexity is O(nlog).<br><br>
The total complexity of the file unpacking process will be equal to:<br>
O(n) + O(nlogn) = O(nlogn).

### Author

Author: Ivan Eremin, email: ivaneremin1979@gmail.com
