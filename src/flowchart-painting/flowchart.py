from graphviz import Digraph

def draw_flowchart(edges, filename):
    dot = Digraph(comment=filename)

    # 设置图的方向为从左到右
    dot.attr(rankdir='LR')

    # 灰度色系设置
    node_style = {
        'shape': 'box',
        'style': 'rounded, filled',
        'fillcolor': '#FFCC99',  # 低饱和度橙色背景色
        'color': '#FF9966',  # 低饱和度橙色边框色
        'fontcolor': '#4d4d4d'  # 低饱和度橙色字体色
    }
    edge_style = {
        'color': '#FF9966',  # 低饱和度橙色边颜色
        'fontcolor': '#CC6600',  # 低饱和度橙色字体颜色
        'fontsize': '10'  # 字体大小
    }

    # 添加节点
    nodes = set()
    for edge in edges:
        nodes.update(edge)
    for node in nodes:
        dot.node(node, **node_style)

    # 添加边
    for edge in edges:
        dot.edge(edge[0], edge[1], **edge_style)

    # 设置文件路径和保存图像
    dot.render(f'/Users/Vic/Desktop/graph/{filename}.gv', format='png')

# 根据每个模块的设计思路和流程图描述，生成对应的边列表
edges_text_to_graph = [
    ("Start", "Read Input Text"),
    ("Read Input Text", "Split Text into Words"),
    ("Split Text into Words", "Initialize Graph"),
    ("Initialize Graph", "For Each Pair of Consecutive Words"),
    ("For Each Pair of Consecutive Words", "Add/Update Edge in Graph"),
    ("Add/Update Edge in Graph", "Graph Construction Completed"),
    ("Graph Construction Completed", "End")
]

edges_display_graph = [
    ("Start", "Convert Graph to DOT Format"),
    ("Convert Graph to DOT Format", "Save DOT File"),
    ("Save DOT File", "Execute Graphviz Command to Generate Image"),
    ("Execute Graphviz Command to Generate Image", "Open Image with Default Viewer"),
    ("Open Image with Default Viewer", "End")
]

edges_query_bridge_words = [
    ("Start", "Check if Words are in Graph"),
    ("Check if Words are in Graph", "Get Neighbors of First Word"),
    ("Get Neighbors of First Word", "For Each Neighbor"),
    ("For Each Neighbor", "Check if Neighbor Connects to Second Word"),
    ("Check if Neighbor Connects to Second Word", "If Yes, Add to Bridge Words"),
    ("If Yes, Add to Bridge Words", "Return Bridge Words"),
    ("Return Bridge Words", "End")
]

edges_generate_new_text = [
    ("Start", "Read Input Text"),
    ("Read Input Text", "Split Text into Words"),
    ("Split Text into Words", "Initialize New Text"),
    ("Initialize New Text", "For Each Pair of Consecutive Words"),
    ("For Each Pair of Consecutive Words", "Find Bridge Words"),
    ("Find Bridge Words", "Insert Bridge Words between Pair"),
    ("Insert Bridge Words between Pair", "New Text Construction Completed"),
    ("New Text Construction Completed", "End")
]

edges_shortest_path = [
    ("Start", "Initialize BFS/Dijkstra Structures"),
    ("Initialize BFS/Dijkstra Structures", "Start from First Word"),
    ("Start from First Word", "Explore Neighbors"),
    ("Explore Neighbors", "Update Distances and Paths"),
    ("Update Distances and Paths", "If Second Word Reached"),
    ("If Second Word Reached", "Return Shortest Path and Distance"),
    ("Return Shortest Path and Distance", "End")
]

edges_random_walk = [
    ("Start", "Choose Random Start Word"),
    ("Choose Random Start Word", "Initialize Walk Path"),
    ("Initialize Walk Path", "For Specified Number of Steps or Until Stuck"),
    ("For Specified Number of Steps or Until Stuck", "Choose Random Neighbor"),
    ("Choose Random Neighbor", "Add Neighbor to Path"),
    ("Add Neighbor to Path", "Return Walk Path"),
    ("Return Walk Path", "End")
]

# 生成并保存流程图
draw_flowchart(edges_text_to_graph, 'text_to_graph')
draw_flowchart(edges_display_graph, 'display_graph')
draw_flowchart(edges_query_bridge_words, 'query_bridge_words')
draw_flowchart(edges_generate_new_text, 'generate_new_text')
draw_flowchart(edges_shortest_path, 'shortest_path')
draw_flowchart(edges_random_walk, 'random_walk')
