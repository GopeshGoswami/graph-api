package m1graphs2026;

import java.util.Objects;

/**
 * Represents a directed edge connecting a source node ({@code from}) to a target node ({@code to})
 * in a graph, with an optional integer weight.
 * <p>
 * Edges can be unweighted (weight is {@code null}) or weighted with any integer (including zero
 * and negative values). Natural ordering is implemented via {@link Comparable} based first on
 * source node ID, then target node ID, then increasing weight.
 * </p>
 *
 * @author M1 ISL
 */
public class Edge implements Comparable<Edge> {
    private final Node from;
    private final Node to;
    private final Integer weight;

    /**
     * Constructs an unweighted directed edge between two nodes.
     *
     * @param from source node of the directed edge; must not be {@code null}
     * @param to target node of the directed edge; must not be {@code null}
     * @throws IllegalArgumentException if either endpoint is {@code null} or if they belong to different graphs
     */
    public Edge(Node from, Node to) {
        this(from, to, null);
    }
    //——————

    /**
     * Constructs a directed edge between two nodes with an optional integer weight.
     *
     * @param from source node of the directed edge; must not be {@code null}
     * @param to target node of the directed edge; must not be {@code null}
     * @param weight integer weight of the edge, or {@code null} for an unweighted edge
     * @throws IllegalArgumentException if either endpoint is {@code null} or if they belong to different graphs
     */
    public Edge(Node from, Node to, Integer weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("An edge's endpoints must not be null.");
        }
        if (from.getGraph() != to.getGraph()) {
            throw new IllegalArgumentException("Both endpoints must belong to the same graph.");
        }
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
    //——————

    /**
     * Constructs an unweighted directed edge using node identifiers.
     *
     * @param fromId integer ID of the source node; must already exist in {@code graph}
     * @param toId integer ID of the target node; must already exist in {@code graph}
     * @param graph graph holding both nodes; must not be {@code null}
     * @throws IllegalArgumentException if {@code graph} is {@code null} or either node does not exist in {@code graph}
     */
    public Edge(int fromId, int toId, Graph graph) {
        this(requireNodeInGraph(graph, fromId), requireNodeInGraph(graph, toId), null);
    }
    //——————

    /**
     * Constructs a weighted directed edge using node identifiers and an integer weight.
     *
     * @param fromId integer ID of the source node; must already exist in {@code graph}
     * @param toId integer ID of the target node; must already exist in {@code graph}
     * @param graph graph holding both nodes; must not be {@code null}
     * @param weight integer weight, or {@code null} if unweighted
     * @throws IllegalArgumentException if {@code graph} is {@code null} or either node does not exist in {@code graph}
     */
    public Edge(int fromId, int toId, Graph graph, Integer weight) {
        this(requireNodeInGraph(graph, fromId), requireNodeInGraph(graph, toId), weight);
    }
    //——————

    private static Node requireNodeInGraph(Graph graph, int id) {
        if (graph == null) {
            throw new IllegalArgumentException("An edge must have a graph holder.");
        }
        return graph.getOrCreateNode(id);
    }
    //——————

    /**
     * Returns the source node of this directed edge.
     *
     * @return the source node
     */
    public Node from() {
        return from;
    }
    //——————

    /**
     * Returns the target node of this directed edge.
     *
     * @return the target node
     */
    public Node to() {
        return to;
    }
    //——————

    /**
     * Returns the symmetric of this edge as a new {@link Edge} instance with endpoints reversed
     * and the same weight.
     *
     * @return a new edge with endpoints reversed and identical weight
     */
    public Edge getSymmetric() {
        return new Edge(to, from, weight);
    }
    //——————

    /**
     * Indicates whether this edge is a self-loop (source and target nodes are the same).
     *
     * @return {@code true} when this edge is a self-loop; {@code false} otherwise
     */
    public boolean isSelfLoop() {
        return from.equals(to);
    }
    //——————

    /**
     * Indicates whether this edge is a multi-edge, i.e., there is at least one other edge
     * with the same endpoints in the graph holder.
     *
     * @return {@code true} if another edge connects the same endpoints in the graph; {@code false} otherwise
     */
    public boolean isMultiEdge() {
        return from.getGraph().isMultiEdge(this);
    }
    //——————

    /**
     * Indicates whether this edge has an assigned integer weight.
     *
     * @return {@code true} when this edge is weighted; {@code false} otherwise
     */
    public boolean isWeighted() {
        return weight != null;
    }
    //——————

    /**
     * Gets the weight of this edge.
     *
     * @return the integer weight, or {@code null} if unweighted
     */
    public Integer getWeight() {
        return weight;
    }
    //——————

    /**
     * Compares this edge with another edge for order.
     * Edges are ordered first by source node ID, then by target node ID, then by increasing weight
     * (unweighted edges precede weighted edges).
     *
     * @param other other edge to compare with; must not be {@code null}
     * @return a negative integer, zero, or a positive integer according to edge ordering rules
     * @throws NullPointerException if {@code other} is {@code null}
     */
    @Override
    public int compareTo(Edge other) {
        Objects.requireNonNull(other, "other edge");
        int comparison = Integer.compare(from.getId(), other.from.getId());
        if (comparison != 0) return comparison;
        comparison = Integer.compare(to.getId(), other.to.getId());
        if (comparison != 0) return comparison;
        if (weight == null && other.weight == null) return 0;
        if (weight == null) return -1;
        if (other.weight == null) return 1;
        return Integer.compare(weight, other.weight);
    }
    //——————

    /**
     * Indicates whether some other object is equal to this edge.
     * Two edges are equal if they have equal source nodes, equal target nodes, and equal weights.
     *
     * @param object object to compare
     * @return {@code true} if the objects represent the same edge; {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Edge other)) return false;
        return from.equals(other.from) && to.equals(other.to)
                && Objects.equals(weight, other.weight);
    }
    //——————

    /**
     * Computes a hash code value for this edge, consistent with {@link #equals(Object)}.
     *
     * @return a hash code value for this edge
     */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }
    //——————

    /**
     * Returns a string representation of this edge.
     *
     * @return a readable string representation of endpoints and weight
     */
    @Override
    public String toString() {
        return weight == null
                ? from.getId() + "->" + to.getId()
                : from.getId() + "->" + to.getId() + "(" + weight + ")";
    }
    //——————
}
