package m1graphs2026;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents an undirected graph, extending {@link Graph}.
 * <p>
 * In an undirected graph, every edge connecting two distinct nodes {@code u} and {@code v}
 * is bidirectional: all incident edges to a node are simultaneously incoming and outgoing edges.
 * Adjacency matrices are symmetric, and graph reversal produces an equivalent undirected graph.
 * </p>
 */
public class UndirectedGraph extends Graph {

    /**
     * Constructs an empty undirected graph.
     */
    public UndirectedGraph() {
        super();
    }
    //——————

    /**
     * Constructs an undirected graph described in the Successor Array (SA) formalism.
     *
     * @param successorArray sequence of node successor IDs grouped by source node (1..N) and delimited by 0
     * @throws IllegalArgumentException if {@code successorArray} is null, does not end in 0, contains negative values,
     *                                  or references a successor ID exceeding the encoded node count
     */
    @SuppressWarnings("this-escape")
    public UndirectedGraph(int... successorArray) {
        super();
        if (successorArray == null) {
            throw new IllegalArgumentException("Successor array must not be null.");
        }
        if (successorArray.length == 0) {
            return;
        }
        if (successorArray[successorArray.length - 1] != 0) {
            throw new IllegalArgumentException("Every successor group, including the last, must end with 0.");
        }

        int nodeCount = 0;
        for (int value : successorArray) {
            if (value == 0) nodeCount++;
            else if (value < 0) {
                throw new IllegalArgumentException("Successor IDs must be positive; 0 is the group delimiter.");
            }
        }

        for (int id = 1; id <= nodeCount; id++) {
            addNode(id);
        }

        int currentSource = 1;
        for (int value : successorArray) {
            if (value == 0) {
                currentSource++;
            } else {
                if (value > nodeCount) {
                    throw new IllegalArgumentException(
                            "Successor ID " + value + " exceeds the encoded node count " + nodeCount + ".");
                }
                if (currentSource <= value || !existsEdge(currentSource, value)) {
                    addEdge(currentSource, value);
                }
            }
        }

        List<Node> all = new ArrayList<>(getAllNodes());
        for (Node n : all) {
            if (degree(n) == 0) {
                removeNode(n);
            }
        }
    }
    //——————

    /**
     * Adds an unweighted undirected edge between {@code from} and {@code to}.
     * The edge is bidirectional and incident to both endpoints.
     *
     * @param from first endpoint of the edge
     * @param to second endpoint of the edge
     */
    @Override
    public void addEdge(Node from, Node to) {
        super.addEdge(from, to);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.addEdge(to, from);
        }
    }
    //——————

    /**
     * Adds an unweighted undirected edge between node IDs {@code fromId} and {@code toId}.
     *
     * @param fromId integer identifier of the first endpoint
     * @param toId integer identifier of the second endpoint
     */
    @Override
    public void addEdge(int fromId, int toId) {
        super.addEdge(fromId, toId);
        if (fromId != toId) {
            super.addEdge(toId, fromId);
        }
    }
    //——————

    /**
     * Adds a weighted undirected edge between {@code from} and {@code to}.
     *
     * @param from first endpoint of the edge
     * @param to second endpoint of the edge
     * @param weight integer weight of the edge
     */
    @Override
    public void addEdge(Node from, Node to, int weight) {
        super.addEdge(from, to, weight);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.addEdge(to, from, weight);
        }
    }
    //——————

    /**
     * Adds a weighted undirected edge between node IDs {@code fromId} and {@code toId}.
     *
     * @param fromId integer identifier of the first endpoint
     * @param toId integer identifier of the second endpoint
     * @param weight integer weight of the edge
     */
    @Override
    public void addEdge(int fromId, int toId, int weight) {
        super.addEdge(fromId, toId, weight);
        if (fromId != toId) {
            super.addEdge(toId, fromId, weight);
        }
    }
    //——————

    /**
     * Adds the supplied edge as an undirected edge, ensuring symmetry.
     *
     * @param edge edge to add
     */
    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        if (edge != null && !edge.isSelfLoop()) {
            super.addEdge(edge.getSymmetric());
        }
    }
    //——————

    /**
     * Removes an undirected edge between {@code from} and {@code to}.
     *
     * @param from first endpoint
     * @param to second endpoint
     * @return {@code true} if an edge was found and removed; {@code false} otherwise
     */
    @Override
    public boolean removeEdge(Node from, Node to) {
        boolean removed = super.removeEdge(from, to);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.removeEdge(to, from);
        }
        return removed;
    }
    //——————

    /**
     * Removes an undirected edge between node IDs {@code fromId} and {@code toId}.
     *
     * @param fromId integer identifier of the first endpoint
     * @param toId integer identifier of the second endpoint
     * @return {@code true} if an edge was found and removed; {@code false} otherwise
     */
    @Override
    public boolean removeEdge(int fromId, int toId) {
        boolean removed = super.removeEdge(fromId, toId);
        if (fromId != toId) {
            super.removeEdge(toId, fromId);
        }
        return removed;
    }
    //——————

    /**
     * Removes a weighted undirected edge between {@code from} and {@code to}.
     *
     * @param from first endpoint
     * @param to second endpoint
     * @param weight integer weight of the edge to remove
     * @return {@code true} if a matching edge was found and removed; {@code false} otherwise
     */
    @Override
    public boolean removeEdge(Node from, Node to, int weight) {
        boolean removed = super.removeEdge(from, to, weight);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.removeEdge(to, from, weight);
        }
        return removed;
    }
    //——————

    /**
     * Removes a weighted undirected edge between node IDs {@code fromId} and {@code toId}.
     *
     * @param fromId integer identifier of the first endpoint
     * @param toId integer identifier of the second endpoint
     * @param weight integer weight of the edge to remove
     * @return {@code true} if a matching edge was found and removed; {@code false} otherwise
     */
    @Override
    public boolean removeEdge(int fromId, int toId, int weight) {
        boolean removed = super.removeEdge(fromId, toId, weight);
        if (fromId != toId) {
            super.removeEdge(toId, fromId, weight);
        }
        return removed;
    }
    //——————

    /**
     * Removes the specified edge symmetrically from this undirected graph.
     *
     * @param edge edge to remove
     * @return {@code true} if an edge was found and removed; {@code false} otherwise
     */
    @Override
    public boolean removeEdge(Edge edge) {
        boolean removed = super.removeEdge(edge);
        if (edge != null && !edge.isSelfLoop()) {
            super.removeEdge(edge.getSymmetric());
        }
        return removed;
    }
    //——————

    /**
     * Returns the total number of distinct undirected edges in this graph.
     *
     * @return total count of undirected edges (counting multi-edges and self-loops)
     */
    @Override
    public int nbEdges() {
        return getAllEdges().size();
    }
    //——————

    /**
     * Returns a sorted list of all unique undirected edges in this graph,
     * normalized such that {@code from.getId() <= to.getId()}.
     *
     * @return a sorted {@link List} of all undirected edges
     */
    @Override
    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        for (Node u : getAllNodes()) {
            for (Edge edge : getOutEdges(u)) {
                if (edge.from().getId() <= edge.to().getId()) {
                    result.add(edge);
                }
            }
        }
        Collections.sort(result);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all unique undirected edges in this graph (alias for {@link #getAllEdges()}).
     *
     * @return a sorted {@link List} of all undirected edges
     */
    @Override
    public List<Edge> getEdges() {
        return getAllEdges();
    }
    //——————

    /**
     * Returns all edges incident to {@code node} (for an undirected graph, in-edges are incident edges).
     *
     * @param node target node
     * @return a sorted list of all incident edges to {@code node}
     */
    @Override
    public List<Edge> getInEdges(Node node) {
        return getOutEdges(node);
    }
    //——————

    /**
     * Returns all edges incident to the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return a sorted list of all incident edges to the node
     */
    @Override
    public List<Edge> getInEdges(int id) {
        return getOutEdges(id);
    }
    //——————

    /**
     * Returns all edges incident to {@code node}.
     *
     * @param node node whose incident edges are requested
     * @return a sorted list of all incident edges to {@code node}
     */
    @Override
    public List<Edge> getIncidentEdges(Node node) {
        return getOutEdges(node);
    }
    //——————

    /**
     * Returns all edges incident to the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return a sorted list of all incident edges to the node
     */
    @Override
    public List<Edge> getIncidentEdges(int id) {
        return getOutEdges(id);
    }
    //——————

    /**
     * Computes the in-degree of {@code node} in this undirected graph, which equals its degree.
     *
     * @param node the node
     * @return number of incident edges to {@code node}
     */
    @Override
    public int inDegree(Node node) {
        return degree(node);
    }
    //——————

    /**
     * Computes the in-degree of the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return number of incident edges to the node
     */
    @Override
    public int inDegree(int id) {
        return degree(id);
    }
    //——————

    /**
     * Computes the out-degree of {@code node} in this undirected graph, which equals its degree.
     *
     * @param node the node
     * @return number of incident edges to {@code node}
     */
    @Override
    public int outDegree(Node node) {
        return degree(node);
    }
    //——————

    /**
     * Computes the out-degree of the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return number of incident edges to the node
     */
    @Override
    public int outDegree(int id) {
        return degree(id);
    }
    //——————

    /**
     * Computes the degree of {@code node} in this undirected graph (number of incident edges).
     *
     * @param node the node
     * @return number of incident edges to {@code node}
     */
    @Override
    public int degree(Node node) {
        if (!holdsNode(node)) return 0;
        int count = 0;
        for (Edge e : getIncidentEdges(node)) {
            count += e.isSelfLoop() ? 2 : 1;
        }
        return count;
    }
    //——————

    /**
     * Computes the degree of the node with ID {@code id} in this undirected graph.
     *
     * @param id integer identifier of the node
     * @return number of incident edges to the node
     */
    @Override
    public int degree(int id) {
        return degree(getNode(id));
    }
    //——————

    /**
     * Reversing an undirected graph produces an identical undirected graph.
     *
     * @return a new {@link UndirectedGraph} copy of this graph
     */
    @Override
    public UndirectedGraph getReverse() {
        return copy();
    }
    //——————

    /**
     * Creates a deep copy of this undirected graph.
     *
     * @return a new {@link UndirectedGraph} instance with identical nodes and edges
     */
    @Override
    public UndirectedGraph copy() {
        UndirectedGraph copy = new UndirectedGraph();
        for (Node node : getAllNodes()) {
            copy.addNode(node.getId(), node.getName());
        }
        for (Edge edge : getAllEdges()) {
            if (edge.isWeighted()) {
                copy.addEdge(edge.from().getId(), edge.to().getId(), edge.getWeight());
            } else {
                copy.addEdge(edge.from().getId(), edge.to().getId());
            }
        }
        return copy;
    }
    //——————

    /**
     * Transforms this undirected graph into a simple undirected graph by removing self-loops and multi-edges.
     *
     * @return a new simple {@link UndirectedGraph} instance
     */
    @Override
    public UndirectedGraph toSimpleGraph() {
        UndirectedGraph simple = new UndirectedGraph();
        for (Node node : getAllNodes()) {
            simple.addNode(node.getId(), node.getName());
        }
        for (Node u : getAllNodes()) {
            for (Node v : getSuccessors(u)) {
                if (u.getId() < v.getId()) {
                    List<Edge> edges = getEdges(u, v);
                    if (!edges.isEmpty()) {
                        Edge first = edges.get(0);
                        if (first.isWeighted()) {
                            simple.addEdge(u.getId(), v.getId(), first.getWeight());
                        } else {
                            simple.addEdge(u.getId(), v.getId());
                        }
                    }
                }
            }
        }
        return simple;
    }
    //——————

    /**
     * Returns a representation of this undirected graph as a Successor Array (SA)
     * using the upper-triangular representation (only edges {@code u <= v}).
     *
     * @return an integer array containing node successors separated and terminated by zeros
     */
    @Override
    public int[] toSuccessorArray() {
        if (isEmpty()) {
            return new int[0];
        }
        List<Integer> list = new ArrayList<>();
        for (Node u : getAllNodes()) {
            for (Edge e : getOutEdges(u)) {
                if (u.getId() <= e.to().getId()) {
                    list.add(e.to().getId());
                }
            }
            list.add(0);
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
    //——————

    /**
     * Computes the transitive closure of this undirected graph in a new {@link UndirectedGraph} instance.
     * Nodes in the same connected component are joined by an undirected edge without self-loops.
     *
     * @return a new {@link UndirectedGraph} representing the transitive closure
     */
    @Override
    public UndirectedGraph getTransitiveClosure() {
        UndirectedGraph closure = new UndirectedGraph();
        List<Node> nodes = getAllNodes();
        int n = nodes.size();
        for (Node node : nodes) {
            closure.addNode(node.getId(), node.getName());
        }
        if (n == 0) {
            return closure;
        }

        Map<Integer, Integer> idToIndex = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            idToIndex.put(nodes.get(i).getId(), i);
        }

        boolean[][] reach = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            Node u = nodes.get(i);
            for (Edge edge : getOutEdges(u)) {
                Integer j = idToIndex.get(edge.to().getId());
                if (j != null) {
                    reach[i][j] = true;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (reach[i][k]) {
                    for (int j = 0; j < n; j++) {
                        if (reach[k][j]) {
                            reach[i][j] = true;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (reach[i][j]) {
                    closure.addEdge(nodes.get(i).getId(), nodes.get(j).getId());
                }
            }
        }

        return closure;
    }
    //——————

    /**
     * Returns an elementary DOT description of this undirected graph.
     *
     * @return a valid DOT description string for this undirected graph
     */
    @Override
    public String toDotString() {
        StringBuilder output = new StringBuilder("graph {\n\trankdir=LR\n");
        List<Node> nodes = getAllNodes();
        Collections.sort(nodes);
        for (Node node : nodes) {
            if (degree(node) == 0) {
                output.append("\t").append(node.getId()).append("\n");
            } else {
                List<Edge> edges = getOutEdges(node);
                Collections.sort(edges);
                for (Edge edge : edges) {
                    if (edge.from().getId() <= edge.to().getId()) {
                        if (edge.isWeighted()) {
                            output.append("\t").append(edge.from().getId())
                                  .append(" -- ").append(edge.to().getId())
                                  .append(" [label=").append(edge.getWeight())
                                  .append(", len=").append(edge.getWeight()).append("]\n");
                        } else {
                            output.append("\t").append(edge.from().getId())
                                  .append(" -- ").append(edge.to().getId()).append("\n");
                        }
                    }
                }
            }
        }
        return output.append("}\n").toString();
    }
    //——————

    /**
     * Imports an undirected graph from a DOT file using the specified extension.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @param extension the file extension
     * @return an {@link UndirectedGraph} imported from the file
     * @throws IllegalArgumentException if {@code fileName} or {@code extension} is {@code null}, or if the file does not exist
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static UndirectedGraph fromDotFile(String fileName, String extension) {
        Graph g = Graph.fromDotFile(fileName, extension);
        if (g instanceof UndirectedGraph ug) {
            return ug;
        }
        UndirectedGraph ug = new UndirectedGraph();
        for (Node n : g.getAllNodes()) {
            ug.addNode(n.getId(), n.getName());
        }
        for (Edge e : g.getAllEdges()) {
            if (e.isWeighted()) {
                ug.addEdge(e.from().getId(), e.to().getId(), e.getWeight());
            } else {
                ug.addEdge(e.from().getId(), e.to().getId());
            }
        }
        return ug;
    }
    //——————

    /**
     * Imports an undirected graph from a DOT file using default extension {@code ".gv"}.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @return an {@link UndirectedGraph} imported from the file
     * @throws IllegalArgumentException if {@code fileName} is {@code null}, or if the file does not exist
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static UndirectedGraph fromDotFile(String fileName) {
        return fromDotFile(fileName, ".gv");
    }
    //——————

    /**
     * Generates a random undirected graph according to the Erdős–Rényi model.
     * Nodes with identifiers 1 to {@code nbNodes} are created, and each pair u &lt; v
     * is connected by an undirected edge with probability {@code edgeProbability}.
     *
     * @param nbNodes number of nodes (must be &gt;= 0)
     * @param edgeProbability edge probability (0.0 &lt;= p &lt;= 1.0)
     * @return a new random {@link UndirectedGraph}
     * @throws IllegalArgumentException if {@code nbNodes < 0} or {@code edgeProbability} is not in [0.0, 1.0]
     */
    public static UndirectedGraph random(int nbNodes, double edgeProbability) {
        if (nbNodes < 0) {
            throw new IllegalArgumentException("nbNodes must be non-negative");
        }
        if (edgeProbability < 0.0 || edgeProbability > 1.0) {
            throw new IllegalArgumentException("edgeProbability must be between 0.0 and 1.0");
        }
        UndirectedGraph g = new UndirectedGraph();
        for (int i = 1; i <= nbNodes; i++) {
            g.addNode(i);
        }
        Random rng = new Random();
        for (int u = 1; u <= nbNodes; u++) {
            for (int v = u + 1; v <= nbNodes; v++) {
                if (rng.nextDouble() < edgeProbability) {
                    g.addEdge(u, v);
                }
            }
        }
        return g;
    }
    //——————

    /**
     * Generates a random connected undirected graph with {@code nbNodes} nodes.
     * First creates a random spanning tree to ensure connectedness, followed by
     * {@code extraEdges} additional random edges.
     *
     * @param nbNodes number of nodes (must be >= 1)
     * @param extraEdges number of additional random edges (must be >= 0)
     * @return a new connected {@link UndirectedGraph}
     * @throws IllegalArgumentException if {@code nbNodes < 1} or {@code extraEdges < 0}
     */
    public static UndirectedGraph randomConnected(int nbNodes, int extraEdges) {
        if (nbNodes < 1) {
            throw new IllegalArgumentException("nbNodes must be at least 1");
        }
        if (extraEdges < 0) {
            throw new IllegalArgumentException("extraEdges must be non-negative");
        }
        UndirectedGraph g = new UndirectedGraph();
        for (int i = 1; i <= nbNodes; i++) {
            g.addNode(i);
        }
        if (nbNodes == 1) {
            return g;
        }
        List<Integer> inTree = new ArrayList<>();
        List<Integer> outTree = new ArrayList<>();
        inTree.add(1);
        for (int i = 2; i <= nbNodes; i++) {
            outTree.add(i);
        }
        Random rng = new Random();
        Collections.shuffle(outTree);
        while (!outTree.isEmpty()) {
            int v = outTree.remove(outTree.size() - 1);
            int u = inTree.get(rng.nextInt(inTree.size()));
            g.addEdge(u, v);
            inTree.add(v);
        }
        for (int k = 0; k < extraEdges; k++) {
            int u = 1 + rng.nextInt(nbNodes);
            int v = 1 + rng.nextInt(nbNodes);
            if (u != v) {
                g.addEdge(u, v);
            }
        }
        return g;
    }
    //——————
}
