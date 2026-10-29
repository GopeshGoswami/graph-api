package m1graphs2026;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final Node from;
    private final Node to;
    private final Integer weight;

    
    public Edge(Node from, Node to) {
        this(from, to, null);
    }

    /**
     
     *
     * @param from 
     * @param to 
     * @param weight integer weight, or {@code null} for an unweighted edge
     * @throws IllegalArgumentException
     
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

    
    public Edge(int fromId, int toId, Graph graph) {
        this(requireGraph(graph).getOrCreateNode(fromId), graph.getOrCreateNode(toId), null);
    }

    
    public Edge(int fromId, int toId, Graph graph, int weight) {
        this(requireGraph(graph).getOrCreateNode(fromId), graph.getOrCreateNode(toId), weight);
    }

    private static Graph requireGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("An edge must have a graph holder.");
        }
        return graph;
    }

    /** @return the source node */
    public Node from() {
        return from;
    }

    /** @return the target node */
    public Node to() {
        return to;
    }

    /** @return {@code true} when this edge is a self-loop */
    public boolean isSelfLoop() {
        return from.equals(to);
    }

    /** @return {@code true} when this edge has an integer weight */
    public boolean isWeighted() {
        return weight != null;
    }

    /** @return edge weight, or {@code null} if unweighted */
    public Integer getWeight() {
        return weight;
    }

    /** @return a new edge with the endpoints reversed and the same weight */
    public Edge getSymmetric() {
        return new Edge(to, from, weight);
    }

    /** @return whether another edge with the same endpoints is in this graph */
    public boolean isMultiEdge() {
        return from.getGraph().isMultiEdge(this);
    }

    
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Edge other)) return false;
        return from.equals(other.from) && to.equals(other.to)
                && Objects.equals(weight, other.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    /** @return a readable endpoint pair, followed by its weight if present */
    @Override
    public String toString() {
        return weight == null
                ? "(" + from.getId() + ", " + to.getId() + ")"
                : "(" + from.getId() + ", " + to.getId() + ", weight=" + weight + ")";
    }
}
