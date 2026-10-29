package m1graphs2026;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Graph {
    
    private final Map<Node, List<Edge>> adjEdList = new LinkedHashMap<>();


    private final Map<Integer, Node> nodesById = new LinkedHashMap<>();


    public Graph() {
    }

    /**
    
     * @param successorArray
     * @throws IllegalArgumentException
    
     */
    @SuppressWarnings("this-escape")
    public Graph(int... successorArray) {
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
            Node node = new Node(id, this);
            nodesById.put(id, node);
            adjEdList.put(node, new ArrayList<>());
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
                addEdge(currentSource, value);
            }
        }
    }

    /**
     
     * @param id
     * @return
     */
    public Node getNode(int id) {
        return nodesById.get(id);
    }

    /** @return number of nodes currently held by this graph */
    public int nbNodes() {
        return nodesById.size();
    }

    /** @return number of edges, counting parallel edges separately */
    public int nbEdges() {
        int count = 0;
        for (List<Edge> edges : adjEdList.values()) count += edges.size();
        return count;
    }

    /**
     
     * @param id
     * @return
     */
    public boolean usesNode(int id) {
        return nodesById.containsKey(id);
    }


    public boolean usesNode(Node node) {
        return node != null && usesNode(node.getId());
    }
    public boolean holdsNode(Node node) {
        return node != null && node.getGraph() == this && usesNode(node.getId());
    }

    /**
     * @param node
     * @return
     */
    public boolean addNode(Node node) {
        if (node == null || node.getGraph() != this || nodesById.containsKey(node.getId())) {
            return false;
        }
        nodesById.put(node.getId(), node);
        adjEdList.put(node, new ArrayList<>());
        return true;
    }

    /**
     * @param id 
     * @return
     */
    public boolean addNode(int id) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, this));
    }

    
    public boolean addNode(int id, String name) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, name, this));
    }

    /**
     
     * @param id
     * @return
     */
    public boolean removeNode(int id) {
        Node node = nodesById.get(id);
        if (node == null) return false;
        adjEdList.remove(node);
        nodesById.remove(id);
        for (List<Edge> outgoing : adjEdList.values()) {
            outgoing.removeIf(edge -> edge.to().getId() == id);
        }
        return true;
    }

    /** @return a new list of all graph nodes */
    public List<Node> getAllNodes() {
        List<Node> result = new ArrayList<>(nodesById.values());
        result.sort(null);
        return result;
    }

    /**
     
     * @param node
     * @return 
     */
    public List<Edge> getOutEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>(adjEdList.get(nodesById.get(node.getId())));
        result.sort(null);
        return result;
    }

    /** Returns all incoming edges to a node, counting parallel edges. */
    public List<Edge> getInEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>();
        for (List<Edge> outgoing : adjEdList.values()) {
            for (Edge edge : outgoing) {
                if (edge.to().getId() == node.getId()) result.add(edge);
            }
        }
        result.sort(null);
        return result;
    }

    /** Returns the source's outgoing edges whose target has the specified ID. */
    public List<Edge> getEdges(Node from, Node to) {
        List<Edge> result = new ArrayList<>();
        if (!holdsNode(from) || !holdsNode(to)) return result;
        for (Edge edge : getOutEdges(from)) {
            if (edge.to().getId() == to.getId()) result.add(edge);
        }
        return result;
    }

    /** Returns whether a directed edge from u to v exists. */
    public boolean adjacent(Node u, Node v) {
        return u != null && v != null && !getEdges(u, v).isEmpty();
    }

    /** Returns whether a directed edge from source ID to target ID exists. */
    public boolean adjacent(int fromId, int toId) {
        return adjacent(getNode(fromId), getNode(toId));
    }

    /** Number of edges entering the supplied node. */
    public int inDegree(Node node) {
        return getInEdges(node).size();
    }

    /** Number of edges leaving the supplied node. */
    public int outDegree(Node node) {
        return getOutEdges(node).size();
    }

    /** Total directed degree: in-degree plus out-degree. */
    public int degree(Node node) {
        return inDegree(node) + outDegree(node);
    }

    /**
     * Adds an edge between IDs, creating either endpoint if absent.
     * Parallel edges and self-loops are permitted.
     */
    public void addEdge(int fromId, int toId) {
        Node from = getOrCreateNode(fromId);
        Node to = getOrCreateNode(toId);
        adjEdList.get(from).add(new Edge(from, to));
    }

    /** Adds an edge between existing node objects belonging to this graph. */
    public void addEdge(Node from, Node to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Edge endpoints must not be null.");
        }
        if (from.getGraph() != this || to.getGraph() != this) {
            throw new IllegalArgumentException("Both endpoints must belong to this graph.");
        }
        Node canonicalFrom = getOrCreateNode(from.getId());
        Node canonicalTo = getOrCreateNode(to.getId());
        adjEdList.get(canonicalFrom).add(new Edge(canonicalFrom, canonicalTo));
    }

    /** Adds a supplied edge after resolving its endpoints to this graph's nodes. */
    public void addEdge(Edge edge) {
        Objects.requireNonNull(edge, "edge");
        if (edge.from().getGraph() != this || edge.to().getGraph() != this) {
            throw new IllegalArgumentException("Both endpoints must belong to this graph.");
        }
        Node from = getOrCreateNode(edge.from().getId());
        Node to = getOrCreateNode(edge.to().getId());
        adjEdList.get(from).add(new Edge(from, to, edge.getWeight()));
    }

    /** Returns true if at least two stored edges share these endpoints. */
    public boolean isMultiEdge(Edge edge) {
        return edge != null && isMultiEdge(edge.from().getId(), edge.to().getId());
    }

    /** Returns true if two or more edges go from {@code fromId} to {@code toId}. */
    public boolean isMultiEdge(int fromId, int toId) {
        Node from = getNode(fromId);
        Node to = getNode(toId);
        return from != null && to != null && getEdges(from, to).size() > 1;
    }

    /** @return true when the graph has no nodes */
    public boolean isEmpty() {
        return nodesById.isEmpty();
    }

    /** Removes every node and edge; a small helper used by the demo GUI. */
    public void clear() {
        adjEdList.clear();
        nodesById.clear();
    }

    /**
     * Internal helper that returns the graph's node for an ID, creating it when
     * missing. Package-private so Edge's ID constructors can share this behavior.
     */
    Node getOrCreateNode(int id) {
        Node existing = nodesById.get(id);
        if (existing != null) return existing;
        Node created = new Node(id, this);
        nodesById.put(id, created);
        adjEdList.put(created, new ArrayList<>());
        return created;
    }

    /** @return all edges sorted by source ID, target ID, then weight */
    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        for (List<Edge> outgoing : adjEdList.values()) result.addAll(outgoing);
        Collections.sort(result);
        return result;
    }

    /** @return an elementary DOT description of this directed graph */
    public String toDotString() {
        StringBuilder output = new StringBuilder("digraph G {\n");
        // for (Node node : getAllNodes()) {
        //     // Explicit node declarations preserve isolated nodes in the drawing.
        //     output.append("  ").append(node.getId()).append(";\n");
        // }
        for (Node node : getAllNodes()) {
            output.append(" ").append(node.getId()).append(";\n")
        }
        for (Edge edge : getAllEdges()) {
            output.append("  ").append(edge.from().getId())
                  .append(" -> ").append(edge.to().getId()).append(";\n");
        }
        return output.append("}\n").toString();
    }
}
