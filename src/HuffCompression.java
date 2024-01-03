import java.io.*;
import java.util.*;

public class HuffCompression {
    private static StringBuilder sb = new StringBuilder();
    private static Map<Byte, String> huffmap = new HashMap<>();

    public static void compress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            byte[] b = new byte[inStream.available()];
            inStream.read(b);
            byte[] huffmanBytes = createZip(b);
            OutputStream outStream = new FileOutputStream(dst);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(huffmanBytes);
            objectOutStream.writeObject(huffmap);
            inStream.close();
            objectOutStream.close();
            outStream.close();
        } catch (Exception e) {
            System.out.println("You have entered invalid path or the file is unsupported");
//            e.printStackTrace();
        }
    }

    private static byte[] createZip(byte[] bytes) {
        MinPriorityQueue<ByteNode> nodes = getByteNodes(bytes);
        ByteNode root = createHuffmanTree(nodes);
        Map<Byte, String> huffmanCodes = getHuffCodes(root);
        byte[] huffmanCodeBytes = zipBytesWithCodes(bytes, huffmanCodes);
        return huffmanCodeBytes;
    }

    private static MinPriorityQueue<ByteNode> getByteNodes(byte[] bytes) {
        MinPriorityQueue<ByteNode> nodes = new MinPriorityQueue<ByteNode>();
        Map<Byte, Integer> tempMap = new HashMap<>();
        for (byte b : bytes) {
            Integer value = tempMap.get(b);
            if (value == null)
                tempMap.put(b, 1);
            else
                tempMap.put(b, value + 1);
        }
        for (Map.Entry<Byte, Integer> entry : tempMap.entrySet())
            nodes.add(new ByteNode(entry.getKey(), entry.getValue()));
        return nodes;
    }

    private static ByteNode createHuffmanTree(MinPriorityQueue<ByteNode> nodes) {
        while (nodes.len() > 1) {
            ByteNode left = nodes.poll();
            ByteNode right = nodes.poll();
            ByteNode parent = new ByteNode(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            nodes.add(parent);
        }
        return nodes.poll();
    }

    private static Map<Byte, String> getHuffCodes(ByteNode root) {
        if (root == null) return null;
        getHuffCodes(root.left, "0", sb);
        getHuffCodes(root.right, "1", sb);
        return huffmap;
    }

    private static void getHuffCodes(ByteNode node, String code, StringBuilder sb1) {
        StringBuilder sb2 = new StringBuilder(sb1);
        sb2.append(code);
        if (node != null) {
            if (node.data == null) {
                getHuffCodes(node.left, "0", sb2);
                getHuffCodes(node.right, "1", sb2);
            } else
                huffmap.put(node.data, sb2.toString());
        }
    }

    private static byte[] zipBytesWithCodes(byte[] bytes, Map<Byte, String> huffCodes) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes)
            strBuilder.append(huffCodes.get(b));

        int length = (strBuilder.length() + 7) / 8;
        byte[] huffCodeBytes = new byte[length];
        int idx = 0;
        for (int i = 0; i < strBuilder.length(); i += 8) {
            String strByte;
            if (i + 8 > strBuilder.length())
                strByte = strBuilder.substring(i);
            else strByte = strBuilder.substring(i, i + 8);
            huffCodeBytes[idx] = (byte) Integer.parseInt(strByte, 2);
            idx++;
        }
        return huffCodeBytes;
    }

    public static void decompress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            byte[] huffmanBytes = (byte[]) objectInStream.readObject();
            Map<Byte, String> huffmanCodes =
                    (Map<Byte, String>) objectInStream.readObject();

            byte[] bytes = decomp(huffmanCodes, huffmanBytes);
            OutputStream outStream = new FileOutputStream(dst);
            outStream.write(bytes);
            inStream.close();
            objectInStream.close();
            outStream.close();
        } catch (Exception e) {
            System.out.println("You have entered invalid path or the file is unsupported");
//            e.printStackTrace();
        }
    }

    public static byte[] decomp(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            sb1.append(convertbyteInBit(!flag, b));
        }
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < sb1.length(); ) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = sb1.substring(i, i + count);
                b = map.get(key);
                if (b == null) count++;
                else flag = false;
            }
            list.add(b);
            i += count;
        }
        byte b[] = new byte[list.size()];
        for (int i = 0; i < b.length; i++)
            b[i] = list.get(i);
        return b;
    }

    private static String convertbyteInBit(boolean flag, byte b) {
        int byte0 = b;
        if (flag) byte0 |= 256;
        String str0 = Integer.toBinaryString(byte0);
        if (flag || byte0 < 0)
            return str0.substring(str0.length() - 8);
        else return str0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Enter 1 to compress OR Enter 2 to decompress (Enter any other number to exit):");
                int option = scanner.nextInt();

                if (option != 1 && option != 2) {
                    System.out.println("Exiting the program. Thank you!");
                    break;
                }

                System.out.println("Enter the file path:");
                String filePath = scanner.next();
                System.out.println("Enter the destination path to save the processed file:");
                String desPath = scanner.next();

                switch (option) {
                    case 1 -> compress(filePath, desPath);
                    case 2 -> decompress(filePath, desPath);
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input");
//                System.out.println(e.getMessage());
                scanner.nextLine(); // Consume the invalid input to avoid an infinite loop
            }
        }

        scanner.close();

    }
}