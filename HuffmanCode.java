import java.util.*;

class HuffmanNode {
    double probability;
    List<HuffmanNode> children;
    boolean isLeaf;
    int index;

    HuffmanNode(double probability) {
        this.probability = probability;
        this.children = new ArrayList<>();
        this.isLeaf = false;
    }

    HuffmanNode(double probability, int index) {
        this.probability = probability;
        this.children = new ArrayList<>();
        this.isLeaf = true;
        this.index = index;
    }
}

public class HuffmanCode {

    private static HuffmanNode buildHuffmanTree(List<HuffmanNode> nodes, int r) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.probability));
        pq.addAll(nodes);

        // Pad with dummy nodes to make size suitable for r-ary tree
        while ((pq.size() - 1) % (r - 1) != 0) {
            pq.add(new HuffmanNode(0.0, -1));
        }

        // Merge nodes to build Huffman Tree
        while (pq.size() > 1) {
            HuffmanNode mergedNode = new HuffmanNode(0.0);
            double totalProb = 0.0;

            for (int i = 0; i < r && !pq.isEmpty(); i++) {
                HuffmanNode child = pq.poll();
                mergedNode.children.add(child);
                totalProb += child.probability;
            }
            mergedNode.probability = totalProb;
            pq.add(mergedNode);
        }

        return pq.poll(); // Return root of the Huffman tree
    }

    private static void generateCodes(HuffmanNode node, String code, Map<Integer, String> huffmanCode) {
        if (node == null)
            return;

        if (node.isLeaf && node.index >= 0) {
            huffmanCode.put(node.index, code);
        } else {
            for (int i = 0; i < node.children.size(); i++) {
                generateCodes(node.children.get(i), code + i, huffmanCode);
            }
        }
    }

    private static double calculateEntropy(double[] probabilities, int r) {
        double entropy = 0.0;
        for (double prob : probabilities) {
            if (prob > 0) {
                entropy += prob * (Math.log(1.0 / prob) / Math.log(r));
            }
        }
        return entropy;
    }

    private static double calculateAverageLength(Map<Integer, String> huffmanCode, double[] probabilities) {
        double averageLength = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            String code = huffmanCode.get(i);
            if (code != null) {
                averageLength += probabilities[i] * code.length();
            }
        }
        return averageLength;
    }

    private static double calculateEfficiency(double entropy, double averageLength) {
        return entropy / averageLength;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter r (base of the Huffman code): ");
        int r = scanner.nextInt();
        if (r < 2) {
            System.out.println("Error: r must be at least 2");
            return;
        }

        System.out.print("Enter the number of messages: ");
        int n = scanner.nextInt();
        System.out.println("Enter the probabilities of each message:");

        double[] probabilities = new double[n];
        double totalProb = 0.0;
        List<HuffmanNode> nodes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            probabilities[i] = scanner.nextDouble();
            if (probabilities[i] > 0) {
                totalProb += probabilities[i];
                nodes.add(new HuffmanNode(probabilities[i], i));
            }
        }

        if (Math.abs(totalProb - 1.0) > 1e-6) {
            System.out.println("Error: Probabilities must sum to 1");
            return;
        }

        HuffmanNode root = buildHuffmanTree(nodes, r);
        Map<Integer, String> huffmanCode = new HashMap<>();
        generateCodes(root, "", huffmanCode);

        double entropy = calculateEntropy(probabilities, r);
        double averageLength = calculateAverageLength(huffmanCode, probabilities);
        double efficiency = calculateEfficiency(entropy, averageLength);

        // Print Huffman Codes
        System.out.println("\nGenerated Huffman Codes:");
        for (int i = 0; i < n; i++) {
            if (probabilities[i] > 0) {
                System.out.println("Message " + i + " (p = " + probabilities[i] + "): " + huffmanCode.get(i));
            }
        }

        // Print Efficiency Details with rounded values
        System.out.println("\nEntropy: " + String.format("%.3f", entropy) + " bits");
        System.out.println("Average Code Length: " + String.format("%.3f", averageLength) + " bits");
        System.out.println("Efficiency: " + String.format("%.3f", efficiency));

        scanner.close();
    }
}
