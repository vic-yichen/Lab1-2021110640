import java.io.IOException; // 导入处理输入输出异常的类
import java.util.List;
import java.util.Map; // 导入映射类
import java.util.Scanner;

/**
    *这是一个主函数，用于用户使用和程序集成.
 */
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

                    graph.calcShortestPath(word1,word2);
                    // 仅输入一个词
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

