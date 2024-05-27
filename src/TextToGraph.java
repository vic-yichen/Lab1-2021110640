import java.io.BufferedReader; // 导入用于读取字符流的缓冲输入流类
import java.io.File;
import java.io.FileWriter; // 导入用于写入字符流的便捷类
import java.io.IOException; // 导入处理输入输出异常的类
import java.io.InputStreamReader; // 导入用于从字节流中读取字符的输入流
import java.io.PrintWriter; // 导入用于打印格式化的文本输出的类
import java.util.*; // 导入包含集合框架类和相关接口的类

public class TextToGraph { // 定义名为 TextToGraph 的类
    private Map<String, Map<String, Integer>> graph = new HashMap<>(); // 创建一个图的邻接表
    private String rootWord = null; // 保存文本中的第一个单词作为根单词
    private Random random = new Random(); // 创建一个随机数生成器
    private boolean stopRandomWalk = false; // 指示是否停止随机漫步的标志

    // 读取文本文件并构建有向图 n+m:单词+边数
    public void readTxt(String txtFile) {
        try {
            Scanner scanner = new Scanner(new File(txtFile)); // 创建一个文件扫描器以读取文件
            String lastWord = null; // 保存上一个单词的变量

            while (scanner.hasNextLine()) { // 循环直到文件的末尾
                String line = scanner.nextLine().toLowerCase(); // 读取文件的下一行并将其转换为小写
                String[] words = line.split("[^a-zA-Z]+"); // 使用正则表达式将行拆分为单词，将非字母字符作为分隔符
                String[] filteredWords = Arrays.stream(words) // 使用流过滤空单词
                        .filter(word -> !word.isEmpty())
                        .toArray(String[]::new); // 将过滤后的单词转换为数组

                if (filteredWords.length == 0) continue; // 当前行没有有效单词
                if (rootWord == null) { // 如果根单词为空
                    rootWord = filteredWords[0]; // 将当前行的第一个有效单词设为根单词
                }

                if (lastWord != null) { // 如果上一个单词不为空
                    addEdge(lastWord, filteredWords[0], 1); // 将上一个单词与当前行的第一个单词之间建立一条边，权重为1
                }

                for (int i = 0; i < filteredWords.length - 1; i++) { // 循环直到倒数第二个单词
                    addEdge(filteredWords[i], filteredWords[i + 1], 1); // 将当前单词与下一个单词之间建立一条边
                }
                lastWord = filteredWords[filteredWords.length - 1]; // 将当前单词设为上一个单词
                graph.putIfAbsent(lastWord, new HashMap<>()); // 如果图中不存在当前单词，则将其加入图中，因为其为last单词
            }
            scanner.close(); // 关闭文件扫描器
        } catch (IOException e) { // 捕获输入输出异常
            e.printStackTrace(); // 打印异常信息
        }
    }

    // 向图中添加边 1 哈希表
    private void addEdge(String from, String to, int weight) {
        graph.putIfAbsent(from, new HashMap<>()); // 如果图中不存在起始单词，则将其加入图中，邻居列表为空
        Map<String, Integer> edges = graph.get(from); // 获取起始单词的邻居列表
        edges.put(to, edges.getOrDefault(to, 0) + weight); // 将目标单词添加到邻居列表中，如果已经存在则累加权重
    }


    // 将图保存为DOT语言文件 n+m
    public void saveToDotFile(String outputFile) {
        // 初始化 PrintWriter
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("digraph G {"); // 输出 DOT 文件的起始部分
            if (rootWord != null) { // 如果根单词不为空
                writer.printf("    \"%s\" [root=true];\n", rootWord); // 输出根单词的标记
            }
            for (String from : graph.keySet()) { // 遍历图中的每个单词
                for (String to : graph.get(from).keySet()) { // 遍历当前单词的每个邻居
                    int weight = graph.get(from).get(to); // 获取边的权重
                    writer.printf("    \"%s\" -> \"%s\" [label=\"%d\"];\n", from, to, weight); // 输出边的信息
                }
            }
            writer.println("}"); // 输出 DOT 文件的结束部分
        } catch (IOException e) { // 捕获输入输出异常
            e.printStackTrace(); // 打印异常信息
        }
    }

    // 展示生成的有向图
    public void showDirectedGraph(String dotFilePath, String outputImagePath) {
        try {
            String[] cmd = {"dot", "-Tpng", dotFilePath, "-o", outputImagePath}; // 创建命令行参数数组
            Process process = Runtime.getRuntime().exec(cmd); // 在运行时执行命令行命令
            process.waitFor(); // 等待进程结束

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream())); // 读取进程的错误输出流
            String line; // 存储每行输出的字符串
            while ((line = errorReader.readLine()) != null) { // 循环读取错误输出流中的每一行
                System.err.println(line); // 打印每行错误信息
            }

            String os = System.getProperty("os.name").toLowerCase(); // 获取操作系统名称
            if (os.contains("windows")) { // 如果是 Windows 系统
                Runtime.getRuntime().exec("cmd /c start " + outputImagePath); // 使用默认程序打开图片文件
            } else if (os.contains("mac")) { // 如果是 macOS 系统
                Runtime.getRuntime().exec("open " + outputImagePath); // 使用默认程序打开图片文件
            } else { // 如果是其他系统
                Runtime.getRuntime().exec("xdg-open " + outputImagePath); // 使用默认程序打开图片文件
            }

            System.out.println("DOT file successfully converted to image."); // 打印转换成功的消息
        } catch (IOException | InterruptedException e) { // 捕获输入输出异常和中断异常
            e.printStackTrace(); // 打印异常信息
        }
    }

    // 查询桥接词 k
    //k 是 word1 的邻居数
    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase(); // 转换为小写
        word2 = word2.toLowerCase();

        if (!graph.containsKey(word1) && !graph.containsKey(word2)) { // 都不包含
            return String.format("No \"%s\" and \"%s\" in the graph!", word1, word2);
        } else if (!graph.containsKey(word1)) { // 不包含第一个
            return String.format("No \"%s\" in the graph!", word1);
        } else if (!graph.containsKey(word2)) { // 不包含第二个
            return String.format("No \"%s\" in the graph!", word2);
        }
        Set<String> bridgeWords = new HashSet<>(); // 创建一个存储桥接词的集合
        Map<String, Integer> word1Edges = graph.get(word1); // 获取第一个单词的邻居列表

        for (String word3 : word1Edges.keySet()) { // 遍历第一个单词的每个邻居，word1Edges 是一个 Map，存储了第一个单词的邻居列表，keySet() 方法返回了这个 Map 中所有键的集合，即第一个单词的所有邻居
            Map<String, Integer> word3Edges = graph.get(word3); // 获取当前邻居的邻居列表，键是邻居的名称，值是邻居之间的权重
            if (word3Edges.containsKey(word2)) { // 在当前邻居的邻居列表中检查是否包含第二个单词
                bridgeWords.add(word3); // 将当前邻居作为桥接词加入集合
            }
        }
        if (bridgeWords.isEmpty()) { // 如果没有找到桥接词
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!"; // 返回没有桥接词的提示消息
        } else if (bridgeWords.size() == 1) { // 如果只有一个桥接词
            StringBuilder result = new StringBuilder("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: "); // 创建结果字符串
            for (String word : bridgeWords) { // 遍历桥接词集合
                result.append(word); // 将每个桥接词添加到结果字符串中
            }
            result.append("."); // 添加句号
            return result.toString(); // 返回结果字符串
        } else { // 如果有多个桥接词
            StringBuilder result = new StringBuilder("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: "); // 创建结果字符串
            int i = 0; // 计数器
            for (String word : bridgeWords) { // 遍历桥接词集合
                if (i > 0) result.append(", "); // 如果不是第一个桥接词，则添加逗号和空格
                result.append(word); // 将当前桥接词添加到结果字符串中
                i++; // 计数器加一
            }
            result.append("."); // 添加句号
            return result.toString(); // 返回结果字符串
        }
    }

    // 生成新的文本 n+k 输入文本单词数+getbw
    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("[^a-zA-Z]+");
        StringBuilder newText = new StringBuilder(); // 创建一个用于构建新文本的字符串生成器

        for (int i = 0; i < words.length - 1; i++) { // 遍历输入文本中的每个单词（除了最后一个单词）
            newText.append(words[i]).append(" "); // 将当前单词添加到新文本中并添加空格
            String bridgeWord = getBridgeWord(words[i], words[i + 1]); // 获取当前单词与下一个单词之间的桥接词
            if (bridgeWord != null) { // 如果存在桥接词
                newText.append(bridgeWord).append(" "); // 将桥接词添加到新文本中并添加空格
            }
        }
        newText.append(words[words.length - 1]); // 将输入文本的最后一个单词添加到新文本中

        return newText.toString(); // 返回生成的新文本
    }

    // 获取桥接词 k 邻居数
    private String getBridgeWord(String word1, String word2) {
        if (!graph.containsKey(word1)) { // 图中不包含第一个单词
            return null;
        }
        List<String> bridgeWords = new ArrayList<>(); // 创建一个存储桥接词的列表
        Map<String, Integer> word1Edges = graph.get(word1); // 获取第一个单词的邻居列表
        for (String word3 : word1Edges.keySet()) { // 遍历第一个单词的每个邻居
            if (graph.get(word3) != null && graph.get(word3).containsKey(word2)) { // 如果当前邻居也是第二个单词的邻居
                bridgeWords.add(word3); // 将当前邻居作为桥接词加入列表
            }
        }

        if (bridgeWords.isEmpty()) {
            return null;
        }
        return bridgeWords.get(random.nextInt(bridgeWords.size())); // 返回随机选择的桥接词
    }

    // 将图保存为带有标记路径的DOT文件 n+m+p,最短路径中边的总数
    public void saveToDotFile_color(String outputFile, List<List<String>> shortestPaths, int pathLength) {
        List<String> dotLines = new ArrayList<>(); // 创建一个存储DOT文件行的列表
        List<String> color = new ArrayList<>(Arrays.asList("green", "orange", "pink", "yellow"));

        int num_shortPath = shortestPaths.size(); // 获取最短路径的数量
        if (rootWord != null) { // 如果根单词不为空
            dotLines.add(String.format("    \"%s\" [root=true];", rootWord)); // 将根单词添加到DOT文件行中
        }

        for (String from : graph.keySet()) { // 遍历图中的每个单词
            for (String to : graph.get(from).keySet()) { // 遍历当前单词的每个邻居
                int weight = graph.get(from).get(to); // 获取边的权重
                int flag = -1; // 创建一个标志变量，用于指示最短路径的颜色
                int index1, index2; // 创建变量以存储当前边的起始单词和目标单词在最短路径中的索引
                for (int i = 0; i < num_shortPath; i++) { // 遍历每条最短路径
                    List<String> shortestPath = shortestPaths.get(i); // 获取当前最短路径
                    if ((index1 = shortestPath.indexOf(from)) != -1 && (index2 = shortestPath.indexOf(to)) != -1) { // 如果当前边的起始单词和目标单词在最短路径中
                        if (index1 + 1 == index2) { // 如果当前边在最短路径中是相邻的
                            if (flag != -1) { // 如果已经有一条最短路径被标记
                                flag = -2; // 标记当前边为特殊颜色
                            } else { // 如果当前边是第一条最短路径
                                flag = i; // 记录最短路径的索引作为颜色索引
                            }
                        }
                    }
                }
                if (flag == -2) {
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\", color=\"yellow\"];", from, to, weight)); // 将当前边添加到DOT文件行中，并设置特殊颜色
                } else if (flag == -1) { // 如果当前边不在任何最短路径中
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];", from, to, weight)); // 将当前边添加到DOT文件行中
                } else { // 如果当前边在最短路径中，并且不是特殊颜色
                    String colorValue = color.get(flag % color.size()); // 根据索引获取颜色
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\", color=\"%s\"];", from, to, weight, colorValue)); // 将当前边添加到DOT文件行中，并设置颜色
                }
            }
        }

        dotLines.add(String.format("    \"Path length = %d\" [label=\"Path length = %d\", color=\"black\", shape=none];", pathLength, pathLength)); // 添加标记路径长度的行

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("digraph G {");
            for (String line : dotLines) {
                writer.println(line);
            }
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 计算最短路径
    // O(nlogn+m) dijkstra
    public List<List<String>> shortestPaths(String word1, String word2, int[] pathLength) {
        List<List<String>> resultPaths = new ArrayList<>(); // 创建一个存储最短路径的列表

        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (!graph.containsKey(word1) || !graph.containsKey(word2)) { // 不包含
            return resultPaths; // 返回空的最短路径列表
        }

        Map<String, Integer> distances = new HashMap<>(); // 创建一个存储距离的映射
        Map<String, List<String>> predecessors = new HashMap<>(); // 创建一个存储前驱节点的映射
        PriorityQueue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<>(Map.Entry.comparingByValue()); // 创建一个优先级队列用于按距离排序

        for (String node : graph.keySet()) { // 遍历图中的每个单词
            distances.put(node, Integer.MAX_VALUE); // 将单词与起始单词之间的距离初始化为最大值
            predecessors.put(node, new ArrayList<>()); // 初始化前驱节点列表为空列表
        }
        distances.put(word1, 0); // 将起始单词与自身之间的距离初始化为0
        priorityQueue.add(new AbstractMap.SimpleEntry<>(word1, 0)); // 将起始单词添加到优先级队列中

        while (!priorityQueue.isEmpty()) { // 直到优先级队列为空
            Map.Entry<String, Integer> current = priorityQueue.poll(); // 从优先级队列中取出距离最小的节点
            String currentNode = current.getKey(); // 获取当前节点
            int currentDistance = current.getValue(); // 获取当前节点与起始单词之间的距离

            if (currentDistance > distances.get(currentNode)) { // 如果当前距离大于图中存储的距离
                continue; // 跳过当前节点
            }

            Map<String, Integer> edges = graph.get(currentNode); // 获取当前节点的邻居列表
            for (Map.Entry<String, Integer> edge : edges.entrySet()) { // 遍历当前节点的每条边
                String neighbor = edge.getKey(); // 获取邻居节点
                int weight = edge.getValue(); // 获取边的权重
                int distance = currentDistance + weight; // 计算邻居节点与起始单词之间的距离

                if (distance < distances.get(neighbor)) { // 如果新距离小于图中存储的距离
                    distances.put(neighbor, distance); // 更新图中的距离
                    predecessors.get(neighbor).clear(); // 清空前驱节点列表
                    predecessors.get(neighbor).add(currentNode); // 将当前节点添加为邻居节点的前驱节点
                    priorityQueue.add(new AbstractMap.SimpleEntry<>(neighbor, distance)); // 将邻居节点添加到优先级队列中
                } else if (distance == distances.get(neighbor)) { // 如果新距离等于图中存储的距离
                    predecessors.get(neighbor).add(currentNode); // 将当前节点添加为邻居节点的前驱节点
                }
            }
        }

        pathLength[0] = distances.get(word2); // 获取最短路径的长度
        if (pathLength[0] == Integer.MAX_VALUE) { // 如果最短路径长度为最大值
            return resultPaths; // 返回空的最短路径列表
        }

        LinkedList<String> path = new LinkedList<>(); // 创建一个存储路径的链表
        findPaths(predecessors, word2, word1, path, resultPaths); // 查找所有最短路径

        for (List<String> resultPath : resultPaths) { // 遍历最短路径列表
            Collections.reverse(resultPath); // 反转路径顺序
        }
        return resultPaths; // 返回最短路径列表
    }

    //p 是所有最短路径中节点的总数
    private void findPaths(Map<String, List<String>> predecessors, String current, String start, LinkedList<String> path, List<List<String>> resultPaths) {
        path.add(current); // 将当前节点添加到路径中
        if (current.equals(start)) { // 如果当前节点等于起始节点
            resultPaths.add(new ArrayList<>(path)); // 将当前路径添加到结果路径列表中
        } else { // 如果当前节点不是起始节点
            for (String predecessor : predecessors.get(current)) { // 遍历当前节点的前驱节点列表
                findPaths(predecessors, predecessor, start, path, resultPaths); // 递归调用 findPaths 方法，探索前驱节点的路径
            }
        }
        path.removeLast(); // 将当前节点从路径中移除，以便在回溯时重新探索其他路径
    }


    // 随机游走 m all边
    public String randomWalk(String outputFile) {
        StringBuilder result = new StringBuilder();
        if (graph.isEmpty()) {
            return "The graph is empty.";
        }

        stopRandomWalk = false;
        List<String> walkPath = new ArrayList<>(); // 用于记录遍历过的节点
        List<String> visitedEdges = new ArrayList<>(); // 用于记录遍历过的边

        String currentWord = getRandomStartNode(); // 获取一个随机起始节点
        result.append(currentWord);

        while (!stopRandomWalk) {
            Map<String, Integer> neighbors = graph.get(currentWord);
            if (neighbors == null || neighbors.isEmpty()) {
                break; // 如果当前节点没有邻居，则退出循环
            }

            String nextWord = chooseNextNode(neighbors);
            if (nextWord == null) {
                break; // 如果没有下一个节点可选，则退出循环
            }

            String edge = currentWord + "->" + nextWord;
            if (visitedEdges.contains(edge)) {
                result.append(" -> ").append(nextWord);
                walkPath.add(currentWord);
                break; // 如果出现了重复的边，则终止遍历并输出路径
            }

            walkPath.add(currentWord); // 将当前节点添加到遍历路径中
            visitedEdges.add(edge); // 将当前节点到下一个节点的边添加到已访问边列表中
            result.append(" -> ").append(nextWord);
            currentWord = nextWord; // 将下一个节点作为当前节点
        }

        // 将遍历的节点和边写入文件
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String node : walkPath) {
                writer.println(node);
            }
            for (String edge : visitedEdges) {
                writer.println(edge);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    // 选择下一个节点 k 邻居数
    private String chooseNextNode(Map<String, Integer> neighbors) {
        int totalWeight = neighbors.values().stream().mapToInt(Integer::intValue).sum(); // 计算所有邻居节点的总权重
        int randomWeight = random.nextInt(totalWeight); // 生成一个随机权重

        for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) { // 遍历邻居节点及其权重
            randomWeight -= neighbor.getValue(); // 减去当前邻居节点的权重
            if (randomWeight < 0) { // 如果随机权重小于0
                return neighbor.getKey(); // 返回对应的邻居节点作为下一个节点
            }
        }

        return null; // 如果没有下一个节点可选，则返回null
    }


    // 获取一个随机的起始节点 n 节点数
    private String getRandomStartNode() {
        List<String> words = new ArrayList<>(graph.keySet());
        return words.get(random.nextInt(words.size()));
    }

    public Map<String, Map<String, Integer>> getGraph() { // 定义获取图的方法
        return graph; // 返回图
    }
}
