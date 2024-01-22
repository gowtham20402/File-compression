# Huffman Compression and Decompression

This Java program implements Huffman coding for file compression and decompression. The Huffman algorithm is a popular method for lossless data compression, often used in file archiving and transmission.

## Usage

### Compression

To compress a file, run the program and choose option 1. Enter the file path of the source file to be compressed and the destination path to save the compressed file.

### Decompression

To decompress a file, run the program and choose option 2. Enter the file path of the compressed file and the destination path to save the decompressed file.

## How it Works

1. **Compression:**
   - The program reads the content of the source file and creates a Huffman tree based on the frequency of each byte in the file.
   - Huffman codes are assigned to each byte, creating a mapping stored in a HashMap.
   - The original file content is replaced with its corresponding Huffman codes.
   - The compressed content and the Huffman code mapping are saved to the destination file using object serialization.

2. **Decompression:**
   - The program reads the compressed file and the Huffman code mapping from the serialized objects.
   - It uses the Huffman codes to reconstruct the original content.
   - The decompressed content is then saved to the destination file.

## Classes

### `HuffCompression`
- Main class handling user input and file processing.
- Contains methods for compression, decompression, and Huffman tree creation.

### `ByteNode`
- Represents a node in the Huffman tree.
- Implements `Comparable` for proper ordering in the priority queue.

### `MinPriorityQueue`
- A generic implementation of a minimum priority queue.
- Used to efficiently build the Huffman tree based on byte frequencies.

## Object Serialization

- The program uses Java's `ObjectInputStream` and `ObjectOutputStream` for object serialization.

## Notes

- The program prompts the user for input and provides error messages for invalid paths or unsupported file types.
- The Huffman codes are saved as binary data in the compressed file.

