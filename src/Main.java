import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        TextToGraph graph = new TextToGraph();

        // 读取用户的命令行输入
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // 用于清空buff
            try {
                while(System.in.available() > 0) {
                    scanner.nextLine();
                }
            } catch (IOException ignored) {}

            System.out.println("Select an option:");
            System.out.println("1. Display the directed graph");
            System.out.println("2. Query bridge words");
            System.out.println("3. Generate new text with bridge words");
            System.out.println("4. Calculate the shortest path");
            System.out.println("5. Perform random walk");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter the text file path: ");
                    String txtFile = scanner.nextLine();
                    graph.readTxt(txtFile);
                    graph.saveToDotFile("./out/text/output.dot");
                    graph.showDirectedGraph("./out/text/output.dot", "./out/png/graph.png");
                    break;

                case "2":
                    System.out.print("Enter the first word: ");
                    String word1 = scanner.nextLine();
                    System.out.print("Enter the second word: ");
                    String word2 = scanner.nextLine();
                    String result = graph.queryBridgeWords(word1, word2);
                    System.out.println(result);
                    break;

                case "3":
                    System.out.print("Enter the input text: ");
                    String inputText = scanner.nextLine();
                    String newText = graph.generateNewText(inputText);
                    System.out.println("Generated new text: " + newText);
                    break;

                case "4":
                    System.out.print("Enter the first word: ");
                    word1 = scanner.nextLine();
                    System.out.print("Enter the second word (leave empty to calculate shortest paths to all nodes): ");
                    word2 = scanner.nextLine();

                    int[] pathLength = new int[1];

                    if (word2.isEmpty()) {
                        Map<String, Map<String, Integer>> graphData = graph.getGraph();
                        for (String node : graphData.keySet()) {
                            if (!node.equals(word1)) {
                                List<List<String>> paths = graph.shortestPaths(word1, node, pathLength);
                                if (!paths.isEmpty()) {
                                    for (List<String> path : paths) {
                                        System.out.println("Shortest path: " + String.join("->", path));
                                    }
                                    String outputFile = String.format("./out/text/shortest_path_%s_to_%s.dot", word1, node);
                                    graph.saveToDotFile_color(outputFile, paths, pathLength[0]);
                                    graph.showDirectedGraph(outputFile, String.format("./out/png/shortest_paths_%s_to_%s.png", word1, node));
                                } else {
                                    System.out.println("No shortest path found from " + word1 + " to " + node);
                                }
                            }
                        }
                    } else {
                        List<List<String>> shortestPaths = graph.shortestPaths(word1, word2, pathLength);
                        if (!shortestPaths.isEmpty()) {
                            for (List<String> path : shortestPaths) {
                                System.out.println("Shortest path: " + String.join("->", path));
                            }
                            String outputFile = "./out/text/shortest_path.dot";
                            graph.saveToDotFile_color(outputFile, shortestPaths, pathLength[0]);
                            graph.showDirectedGraph(outputFile, "./out/png/shortest_paths.png");
                        } else {
                            System.out.println("No shortest path found from " + word1 + " to " + word2);
                        }
                    }
                    break;

                case "5":
                    System.out.print("Enter the output file path for random walk: ");
                    String outputFile = scanner.nextLine();
                    String result2 = graph.randomWalk(outputFile);
                    System.out.println(result2);
                    break;

                case "6":
                    scanner.close();
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
