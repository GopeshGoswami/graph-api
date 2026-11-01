package m1graphs2026;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Represents a node (vertex) in a graph.
 * <p>
 * A node has an integer identifier ({@code id}), an optional name, and a reference
 * to the graph holder to which the node belongs. The {@code id} uniquely identifies
 * a node within its graph.
 * </p>
 *
 * @author M1 ISL
 */
public class Node implements Comparable<Node> {
    private final int id;
    private final String name;
    private final Graph graph;

    /**
     * Constructs a node parameterized only by its owning graph holder,
     * automatically generating an identifier.
     *
     * @param graph graph that owns the node; must not be {@code null}
     * @throws IllegalArgumentException if {@code graph} is {@code null}
     */
    public Node(Graph graph) {
        this(graph != null ? (graph.isEmpty() ? 1 : graph.largestNodeId() + 1) : 1, null, graph);
    }
    //——————

    /**
     * Constructs a node with an identifier and an owning graph holder.
     *
     * @param id unique identifier of this node within {@code graph}
     * @param graph graph that owns the node; must not be {@code null}
     * @throws IllegalArgumentException if {@code graph} is {@code null}
     */
    public Node(int id, Graph graph) {
        this(id, null, graph);
    }
    //——————

    /**
     * Constructs a node with an identifier, an optional name, and an owning graph holder.
     *
     * @param id unique identifier of this node within {@code graph}
     * @param name optional name of the node; may be {@code null}
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
    //——————

    /**
     * Gets the unique identifier of this node.
     *
     * @return this node's integer identifier
     */
    public int getId() {
        return id;
    }
    //——————

    /**
     * Gets the graph holder of this node.
     *
     * @return the graph that owns this node
     */
    public Graph getGraph() {
        return graph;
    }
    //——————

    /**
     * Gets the optional name of this node.
     *
     * @return this node's optional name, or {@code null} when unnamed
     */
    public String getName() {
        return name;
    }
    //——————

    /**
     * Returns a list without duplicates of the successors (or neighbors in the undirected case)
     * of this node.
     *
     * @return a new sorted list of unique successors
     */
    public List<Node> getSuccessors() {
        return graph.getSuccessors(this);
    }
    //——————

    /**
     * Returns a list with possible duplicates of the successors (or neighbors in the undirected case)
     * of this node.
     *
     * @return a new sorted list containing one successor entry per outgoing edge
     */
    public List<Node> getSuccessorsMulti() {
        return graph.getSuccessorsMulti(this);
    }
    //——————

    /**
     * Tests whether node {@code u} is adjacent to this node.
     *
     * @param u possible successor node
     * @return {@code true} if an edge from this node to {@code u} exists; {@code false} otherwise
     */
    public boolean adjacent(Node u) {
        return graph.adjacent(this, u);
    }
    //——————

    /**
     * Tests whether the node with ID {@code nodeId} is adjacent to this node.
     *
     * @param nodeId ID of a possible successor
     * @return {@code true} if that node is a successor of this node; {@code false} otherwise
     */
    public boolean adjacent(int nodeId) {
        return graph.adjacent(this.id, nodeId);
    }
    //——————

    /**
     * Computes the in-degree of this node, counting all incoming edges including parallel edges.
     *
     * @return number of edges entering this node
     */
    public int inDegree() {
        return graph.inDegree(this);
    }
    //——————

    /**
     * Computes the out-degree of this node, counting all outgoing edges including parallel edges.
     *
     * @return number of edges leaving this node
     */
    public int outDegree() {
        return graph.outDegree(this);
    }
    //——————

    /**
     * Computes the total degree of this node.
     * For directed graphs, this is the sum of in-degree and out-degree.
     *
     * @return in-degree plus out-degree
     */
    public int degree() {
        return inDegree() + outDegree();
    }
    //——————

    /**
     * Returns a list of all edges leaving this node.
     *
     * @return a new sorted list of all edges leaving this node
     */
    public List<Edge> getOutEdges() {
        return graph.getOutEdges(this);
    }
    //——————

    /**
     * Returns a list of all edges entering this node.
     *
     * @return a new sorted list of all edges entering this node
     */
    public List<Edge> getInEdges() {
        return graph.getInEdges(this);
    }
    //——————

    /**
     * Returns a list of all edges incident to this node (union of out-edges and in-edges).
     *
     * @return a new sorted list of all edges incident to this node
     */
    public List<Edge> getIncidentEdges() {
        return graph.getIncidentEdges(this);
    }
    //——————

    /**
     * Returns a list of all edges going from this node to node {@code u}.
     *
     * @param u target node
     * @return a new sorted list of all directed edges from this node to {@code u}
     */
    public List<Edge> getEdgesTo(Node u) {
        return graph.getEdges(this, u);
    }
    //——————

    /**
     * Compares this node with another node for order by increasing ID.
     *
     * @param other node to compare with; must not be {@code null}
     * @return a negative number, zero, or positive number according to ID order
     * @throws NullPointerException if {@code other} is {@code null}
     */
    @Override
    public int compareTo(Node other) {
        Objects.requireNonNull(other, "other node");
        return Integer.compare(this.id, other.id);
    }
    //——————

    /**
     * Indicates whether some other object is equal to this node.
     * Two nodes are equal if they share the same ID and belong to the same graph instance.
     *
     * @param object object to compare
     * @return {@code true} if the objects represent the same graph node; {@code false} otherwise
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
    //——————

    /**
     * Computes a hash code consistent with {@link #equals(Object)}.
     *
     * @return a hash code value for this node
     */
    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(id) + System.identityHashCode(graph);
    }
    //——————

    /**
     * Returns a string representation of this node's ID.
     *
     * @return the node ID as a string
     */
    @Override
    public String toString() {
        return Integer.toString(id);
    }
    //——————
}
