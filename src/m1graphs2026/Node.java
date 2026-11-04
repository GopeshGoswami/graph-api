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

   
    public Node(Graph graph) {
        this(graph != null ? (graph.isEmpty() ? 1 : graph.largestNodeId() + 1) : 1, null, graph);
    }
    
    public Node(int id, Graph graph) {
        this(id, null, graph);
    }
    
    public Node(int id, String name, Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("A node must have a graph holder.");
        }
        this.id = id;
        this.name = name;
        this.graph = graph;
    }
    
    public int getId() {
        return id;
    }
    
    public Graph getGraph() {
        return graph;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Node> getSuccessors() {
        return graph.getSuccessors(this);
    }
    
    public List<Node> getSuccessorsMulti() {
        return graph.getSuccessorsMulti(this);
    }
    
    public boolean adjacent(Node u) {
        return graph.adjacent(this, u);
    }
    
    public boolean adjacent(int nodeId) {
        return graph.adjacent(this.id, nodeId);
    }
    
    public int inDegree() {
        return graph.inDegree(this);
    }
    
    public int outDegree() {
        return graph.outDegree(this);
    }
    
    public int degree() {
        return inDegree() + outDegree();
    }
    
    public List<Edge> getOutEdges() {
        return graph.getOutEdges(this);
    }
    
    public List<Edge> getInEdges() {
        return graph.getInEdges(this);
    }
    
    public List<Edge> getIncidentEdges() {
        return graph.getIncidentEdges(this);
    }
    
    public List<Edge> getEdgesTo(Node u) {
        return graph.getEdges(this, u);
    }
    
    @Override
    public int compareTo(Node other) {
        Objects.requireNonNull(other, "other node");
        return Integer.compare(this.id, other.id);
    }
    
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
    
    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(id) + System.identityHashCode(graph);
    }
    
    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
