import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class TextToGraph {
    private Map<String, Map<String, Integer>> graph = new HashMap<>();
    private String rootWord = null;
    private Random random = new Random();
    private boolean stopRandomWalk = false;

    // 读取文本文件并构建有向图
    public void readTxt(String txtFile) {
        try {
            Scanner scanner = new Scanner(new File(txtFile));
            String lastWord = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                String[] words = line.split("[^a-zA-Z]+");
                String[] filteredWords = Arrays.stream(words)
                        .filter(word -> !word.isEmpty())
                        .toArray(String[]::new);

                if (filteredWords.length == 0) continue;
                if (rootWord == null) {
                    rootWord = filteredWords[0];
                }

                if (lastWord != null) {
                    addEdge(lastWord, filteredWords[0], 1);
                }

                for (int i = 0; i < filteredWords.length - 1; i++) {
                    addEdge(filteredWords[i], filteredWords[i + 1], 1);
                }
                lastWord = filteredWords[filteredWords.length - 1];
                graph.putIfAbsent(lastWord, new HashMap<>());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 向图中添加边
    private void addEdge(String from, String to, int weight) {
        graph.putIfAbsent(from, new HashMap<>());
        Map<String, Integer> edges = graph.get(from);
        edges.put(to, edges.getOrDefault(to, 0) + weight);
    }

    // 将图保存为DOT语言文件
    public void saveToDotFile(String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("digraph G {");
            if (rootWord != null) {
                writer.printf("    \"%s\" [root=true];\n", rootWord);
            }
            for (String from : graph.keySet()) {
                for (String to : graph.get(from).keySet()) {
                    int weight = graph.get(from).get(to);
                    writer.printf("    \"%s\" -> \"%s\" [label=\"%d\"];\n", from, to, weight);
                }
            }
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 展示生成的有向图
    public void showDirectedGraph(String dotFilePath, String outputImagePath) {
        try {
            String[] cmd = {"dot", "-Tpng", dotFilePath, "-o", outputImagePath};
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                Runtime.getRuntime().exec("cmd /c start " + outputImagePath);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + outputImagePath);
            } else {
                Runtime.getRuntime().exec("xdg-open " + outputImagePath);
            }

            System.out.println("DOT file successfully converted to image.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 查询桥接词
    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (!graph.containsKey(word1) && !graph.containsKey(word2)) {
            return String.format("No \"%s\" and \"%s\" in the graph!", word1, word2);
        } else if (!graph.containsKey(word1)) {
            return String.format("No \"%s\" in the graph!", word1);
        } else if (!graph.containsKey(word2)) {
            return String.format("No \"%s\" in the graph!", word2);
        }
        Set<String> bridgeWords = new HashSet<>();
        Map<String, Integer> word1Edges = graph.get(word1);

        for (String word3 : word1Edges.keySet()) {
            Map<String, Integer> word3Edges = graph.get(word3);
            if (word3Edges.containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else if (bridgeWords.size() == 1) {
            StringBuilder result = new StringBuilder("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: ");
            for (String word : bridgeWords) {
                result.append(word);
            }
            result.append(".");
            return result.toString();
        } else {
            StringBuilder result = new StringBuilder("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: ");
            int i = 0;
            for (String word : bridgeWords) {
                if (i > 0) result.append(", ");
                result.append(word);
                i++;
            }
            result.append(".");
            return result.toString();
        }
    }

    // 生成新的文本
    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("[^a-zA-Z]+");
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String bridgeWord = getBridgeWord(words[i], words[i + 1]);
            if (bridgeWord != null) {
                newText.append(bridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]);

        return newText.toString();
    }

    // 获取桥接词
    private String getBridgeWord(String word1, String word2) {
        if (!graph.containsKey(word1)) {
            return null;
        }
        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> word1Edges = graph.get(word1);
        for (String word3 : word1Edges.keySet()) {
            if (graph.get(word3) != null && graph.get(word3).containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }

        if (bridgeWords.isEmpty()) {
            return null;
        }
        return bridgeWords.get(random.nextInt(bridgeWords.size()));
    }


    // 将图保存为带有标记路径的DOT文件
    public void saveToDotFile_color(String outputFile, List<List<String>> shortestPaths, int pathLength) {
        List<String> dotLines = new ArrayList<>();
        List<String> color = new ArrayList<>(Arrays.asList("green", "orange", "pink", "yellow"));

        int num_shortPath = shortestPaths.size();
        if (rootWord != null) {
            dotLines.add(String.format("    \"%s\" [root=true];", rootWord));
        }

        for (String from : graph.keySet()) {
            for (String to : graph.get(from).keySet()) {
                int weight = graph.get(from).get(to);
                int flag = -1;
                int index1, index2;
                for (int i = 0; i < num_shortPath; i++) {
                    List<String> shortestPath = shortestPaths.get(i);
                    if ((index1 = shortestPath.indexOf(from)) != -1 && (index2 = shortestPath.indexOf(to)) != -1) {
                        if (index1 + 1 == index2) {
                            if (flag != -1) {
                                flag = -2;
                            } else {
                                flag = i;
                            }
                        }
                    }
                }
                if (flag == -2) {
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\", color=\"yellow\"];", from, to, weight));
                } else if (flag == -1) {
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];", from, to, weight));
                } else {
                    String colorValue = color.get(flag % color.size());
                    dotLines.add(String.format("    \"%s\" -> \"%s\" [label=\"%d\", color=\"%s\"];", from, to, weight, colorValue));
                }
            }
        }

        dotLines.add(String.format("    \"Path length = %d\" [label=\"Path length = %d\", color=\"black\", shape=none];", pathLength, pathLength));

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
    public List<List<String>> shortestPaths(String word1, String word2, int[] pathLength) {
        List<List<String>> resultPaths = new ArrayList<>();

        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return resultPaths;
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<String>> predecessors = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, new ArrayList<>());
        }
        distances.put(word1, 0);
        priorityQueue.add(new AbstractMap.SimpleEntry<>(word1, 0));

        while (!priorityQueue.isEmpty()) {
            Map.Entry<String, Integer> current = priorityQueue.poll();
            String currentNode = current.getKey();
            int currentDistance = current.getValue();

            if (currentDistance > distances.get(currentNode)) {
                continue;
            }

            Map<String, Integer> edges = graph.get(currentNode);
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String neighbor = edge.getKey();
                int weight = edge.getValue();
                int distance = currentDistance + weight;

                if (distance < distances.get(neighbor)) {
                    distances.put(neighbor, distance);
                    predecessors.get(neighbor).clear();
                    predecessors.get(neighbor).add(currentNode);
                    priorityQueue.add(new AbstractMap.SimpleEntry<>(neighbor, distance));
                } else if (distance == distances.get(neighbor)) {
                    predecessors.get(neighbor).add(currentNode);
                }
            }
        }

        pathLength[0] = distances.get(word2);
        if (pathLength[0] == Integer.MAX_VALUE) {
            return resultPaths;
        }

        LinkedList<String> path = new LinkedList<>();
        findPaths(predecessors, word2, word1, path, resultPaths);

        for (List<String> resultPath : resultPaths) {
            Collections.reverse(resultPath);
        }
        return resultPaths;
    }

    private void findPaths(Map<String, List<String>> predecessors, String current, String start, LinkedList<String> path, List<List<String>> resultPaths) {
        path.add(current);
        if (current.equals(start)) {
            resultPaths.add(new ArrayList<>(path));
        } else {
            for (String predecessor : predecessors.get(current)) {
                findPaths(predecessors, predecessor, start, path, resultPaths);
            }
        }
        path.removeLast();
    }

    // 随机漫步
    public String randomWalk(String outputFile) {
        StringBuilder result = new StringBuilder();
        if (graph.isEmpty()) {
            return "The graph is empty.";
        }

        stopRandomWalk = false;
        List<String> words = new ArrayList<>(graph.keySet());
        String currentWord = words.get(random.nextInt(words.size()));
        result.append(currentWord);

        List<String> walkPath = new ArrayList<>();
        walkPath.add(currentWord);

        while (!stopRandomWalk) {
            Map<String, Integer> neighbors = graph.get(currentWord);
            if (neighbors.isEmpty()) {
                break;
            }

            int totalWeight = neighbors.values().stream().mapToInt(Integer::intValue).sum();
            int randomWeight = random.nextInt(totalWeight);

            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                randomWeight -= neighbor.getValue();
                if (randomWeight < 0) {
                    currentWord = neighbor.getKey();
                    break;
                }
            }

            walkPath.add(currentWord);
            result.append(" -> ").append(currentWord);

            if (new Random().nextDouble() < 0.15) {
                break;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (String word : walkPath) {
                writer.println(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public Map<String, Map<String, Integer>> getGraph() {
        return graph;
    }
}
