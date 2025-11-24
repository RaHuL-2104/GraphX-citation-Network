import networkx as nx
import matplotlib.pyplot as plt

# Load edges
path = r"C:\GraphX-Citation\output\graph_edges\part-00000"

G = nx.DiGraph()

with open(path, "r") as f:
    for line in f:
        src, dst = line.strip().split(",")
        G.add_edge(src, dst)

# Compute PageRank (small graphs only)
pr = nx.pagerank(G)
top_nodes = sorted(pr.items(), key=lambda x: x[1], reverse=True)[:10]
top_set = {n for (n, _) in top_nodes}

plt.figure(figsize=(12, 10))
pos = nx.spring_layout(G, k=0.25)

nx.draw_networkx_nodes(G, pos, node_size=10, alpha=0.6)
nx.draw_networkx_nodes(G, pos, nodelist=list(top_set), node_color="red", node_size=80)
nx.draw_networkx_edges(G, pos, alpha=0.1)

plt.title("Academic Citation Network — Top PageRank Nodes Highlighted")
plt.axis("off")
plt.show()
