import java.io.IOException; // 导入处理输入输出异常的类
import java.util.List;
import java.util.Map; // 导入映射类
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TextToGraph graph = new TextToGraph(); // 创建一个文本到图形的转换对象

        // 读取用户的命令行输入
        Scanner scanner = new Scanner(System.in); // 创建一个扫描器对象用于读取用户输入
        while (true) {
            // 用于清空buff，防止缓冲区未被完全读取
            try {
                while (System.in.available() > 0) { // 还有数据
                    scanner.nextLine(); // 读取+丢弃
                }
            } catch (IOException ignored) {} // 捕获输入输出异常并忽略

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
                    graph.readTxt(txtFile); // 从文本文件中读取数据并构建图形
                    graph.saveToDotFile("./out/text/output.dot"); // 将图形保存为DOT语言文件
                    graph.showDirectedGraph("./out/text/output.dot", "./out/png/graph.png"); // 展示有向图
                    break;

                case "2":
                    System.out.print("Enter the first word: ");
                    String word1 = scanner.nextLine();
                    System.out.print("Enter the second word: ");
                    String word2 = scanner.nextLine();
                    String result = graph.queryBridgeWords(word1, word2); // 查询桥接词
                    System.out.println(result); // 打印result
                    break;

                case "3":
                    System.out.print("Enter the input text: ");
                    String inputText = scanner.nextLine();
                    String newText = graph.generateNewText(inputText); // 生成包含桥接词的新文本
                    System.out.println("Generated new text: " + newText);
                    break;

                case "4":
                    System.out.print("Enter the first word: ");
                    word1 = scanner.nextLine();
                    System.out.print("Enter the second word (leave empty to calculate shortest paths to all nodes): ");
                    word2 = scanner.nextLine();
                    int[] pathLength = new int[1]; // 存储最短路径长度，数组

                    // 仅输入一个词
                    if (word2.isEmpty()) {
                        Map<String, Map<String, Integer>> graphData = graph.getGraph(); // 获取图的数据
                        for (String node : graphData.keySet()) { // 遍历图中的每个节点
                            if (!node.equals(word1)) { // 如果节点不是第一个单词
                                List<List<String>> paths = graph.shortestPaths(word1, node, pathLength); // 计算到该节点的最短路径
                                if (!paths.isEmpty()) { // 如果最短路径不为空
                                    for (List<String> path : paths) { // 遍历最短路径列表
                                        System.out.println("Shortest path: " + String.join("->", path)); // 打印最短路径
                                    }
                                    String outputFile = String.format("./out/text/shortest_path_%s_to_%s.dot", word1, node); // 定义输出文件路径
                                    graph.saveToDotFile_color(outputFile, paths, pathLength[0]); // 将带有标记路径的DOT文件保存到指定路径
                                    graph.showDirectedGraph(outputFile, String.format("./out/png/shortest_paths_%s_to_%s.png", word1, node)); // 展示最短路径的图形
                                }
                                else { // 如果最短路径为空
                                    System.out.println("No shortest path found from " + word1 + " to " + node); // 打印未找到最短路径的消息
                                }
                            }
                        }
                    }
                    //输入两个单词
                    else {
                        List<List<String>> shortestPaths = graph.shortestPaths(word1, word2, pathLength); // 计算两个单词之间的最短路径
                        if (!shortestPaths.isEmpty()) { // 如果最短路径不为空
                            for (List<String> path : shortestPaths) { // 遍历最短路径列表
                                System.out.println("Shortest path: " + String.join("->", path)); // 打印最短路径
                            }
                            String outputFile = "./out/text/shortest_path.dot"; // 定义输出文件路径
                            graph.saveToDotFile_color(outputFile, shortestPaths, pathLength[0]); // 将带有标记路径的DOT文件保存到指定路径
                            graph.showDirectedGraph(outputFile, "./out/png/shortest_paths.png"); // 展示最短路径的图形
                        } else { // 如果最短路径为空
                            System.out.println("No shortest path found from " + word1 + " to " + word2); // 打印未找到最短路径的消息
                        }
                    }
                    break;

                case "5":
                    System.out.print("Enter the output file path for random walk: ");
                    String outputFile = scanner.nextLine();
                    String result2 = graph.randomWalk(outputFile); // 执行随机漫步
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
