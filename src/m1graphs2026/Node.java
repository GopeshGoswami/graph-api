package m1graphs2026;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class Node implements Comparable<Node> {
    private final int id;
    private final String name;
    private final Graph graph;

    /**
     
     * @param id unique identifier of this node within {@code graph}
     * @param graph graph that owns the node; must not be {@code null}
     * @throws IllegalArgumentException if {@code graph} is {@code null}
     */
    public Node(int id, Graph graph) {
        this(id, null, graph);
    }

    /**
     
     * @param id unique identifier of this node within {@code graph}
     * @param name optional name; may be {@code null}
     * @param graph graph that owns the node; must not be {@code null}
     * @throws IllegalArgumentException if {@code graph} is {@code null}
     */
    public Node(int id, String name, Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("A node must have a graph holder.");
        }
        this.id = id;
        this.name = name;
        this.graph = graph;
    }

    /** @return this node's integer identifier */
    public int getId() {
        return id;
    }

    /** @return the graph that owns this node */
    public Graph getGraph() {
        return graph;
    }

    /** @return this node's optional name, or {@code null} when unnamed */
    public String getName() {
        return name;
    }

    /**
    
     * @return a new list of unique successors
     */
    public List<Node> getSuccessors() {
        Set<Node> unique = new LinkedHashSet<>(getSuccessorsMulti());
        List<Node> result = new ArrayList<>(unique);
        result.sort(null);
        return result;
    }

    /**
   
     * @return a new list containing one successor entry per outgoing edge
     */
    public List<Node> getSuccessorsMulti() {
        List<Node> result = new ArrayList<>();
        for (Edge edge : getOutEdges()) {
            result.add(edge.to());
        }
        result.sort(null);
        return result;
    }

    /**
     * Tests whether this node has a directed edge to {@code u}.
     *
     * @param u possible successor node
     * @return {@code true} if an edge from this node to {@code u} exists
     */
    public boolean adjacent(Node u) {
        return graph.adjacent(this, u);
    }

    /**
     * Tests whether this node has an outgoing edge to the node with {@code id}.
     *
     * @param nodeId ID of a possible successor
     * @return {@code true} if that node is a successor of this node
     */
    public boolean adjacent(int nodeId) {
        return graph.adjacent(this.id, nodeId);
    }

    /** @return number of edges entering this node, counting parallel edges */
    public int inDegree() {
        return graph.inDegree(this);
    }

    /** @return number of edges leaving this node, counting parallel edges */
    public int outDegree() {
        return graph.outDegree(this);
    }

    /**
    
     * @return in-degree plus out-degree
     */
    public int degree() {
        return inDegree() + outDegree();
    }

    /** @return a new list of all edges leaving this node */
    public List<Edge> getOutEdges() {
        return graph.getOutEdges(this);
    }

    /**
     *
     * @param other node to compare with
     * @return a negative number, zero, or positive number according to IDs
     */
    @Override
    public int compareTo(Node other) {
        Objects.requireNonNull(other, "other node");
        return Integer.compare(this.id, other.id);
    }

    /**
     * @param object object to compare
     * @return whether the objects represent the same graph node
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Node other)) {
            return false;
        }
        return id == other.id && graph == other.graph;
    }

    /** @return a hash code consistent with {@link #equals(Object)} */
    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(id) + System.identityHashCode(graph);
    }

    /** @return the node ID, making node lists easy to read */
    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
