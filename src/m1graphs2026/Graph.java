package m1graphs2026;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a directed graph using adjacent edge lists.
 * Supports directed graphs, multigraphs, self-loops, and weighted edges.
 */
public class Graph {
    
    private final Map<Node, List<Edge>> adjEdList = new LinkedHashMap<>();

    private final Map<Integer, Node> nodesById = new LinkedHashMap<>();

    /** Static timestamp counter used during Depth-First Search traversals per the lecture specification. */
    private static int dfsTime = 0;

    /**
     * Constructs an empty directed graph.
     */
    public Graph() {
    }
    //——————

    /**
     * Constructs a directed graph described in the Successor Array (SA) formalism.
     *
     * @param successorArray sequence of node successor IDs grouped by source node (1..N) and delimited by 0
     * @throws IllegalArgumentException if {@code successorArray} is null, does not end in 0, contains negative values,
     *                                  or references a successor ID exceeding the encoded node count
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
    //——————

    /**
     * Retrieves the node with the specified identifier from this graph.
     *
     * @param id integer identifier of the node
     * @return the {@link Node} associated with {@code id}, or {@code null} if not found
     */
    public Node getNode(int id) {
        return nodesById.get(id);
    }
    //——————

    /**
     * Returns the number of nodes currently held by this graph.
     *
     * @return number of nodes in this graph
     */
    public int nbNodes() {
        return nodesById.size();
    }
    //——————

    /**
     * Returns the total number of edges in this graph, counting parallel edges separately.
     *
     * @return number of edges in this graph
     */
    public int nbEdges() {
        int count = 0;
        for (List<Edge> edges : adjEdList.values()) {
            count += edges.size();
        }
        return count;
    }
    //——————

    /**
     * Checks whether this graph contains a node with the specified identifier.
     *
     * @param id integer identifier of the node
     * @return {@code true} if a node with {@code id} is present in this graph; {@code false} otherwise
     */
    public boolean usesNode(int id) {
        return nodesById.containsKey(id);
    }
    //——————

    /**
     * Checks whether this graph contains a node whose identifier matches the specified node's identifier.
     *
     * @param node the node to check
     * @return {@code true} if a node with matching ID exists in this graph; {@code false} otherwise
     */
    public boolean usesNode(Node node) {
        return node != null && usesNode(node.getId());
    }
    //——————

    /**
     * Checks whether the exact node instance belongs to this graph and is currently held by it.
     *
     * @param node the node to check
     * @return {@code true} if {@code node} belongs to and is present in this graph; {@code false} otherwise
     */
    public boolean holdsNode(Node node) {
        return node != null && node.getGraph() == this && usesNode(node.getId());
    }
    //——————

    /**
     * Indicates whether a node with the specified identifier is present in this graph.
     *
     * @param id integer identifier of the node
     * @return {@code true} if a node with ID {@code id} exists in this graph; {@code false} otherwise
     */
    public boolean holdsNode(int id) {
        return getNode(id) != null;
    }
    //——————

    /**
     * Adds an existing node instance to this graph if it belongs to this graph and has a unique identifier.
     *
     * @param node the node to add
     * @return {@code true} if the node was successfully added; {@code false} if already present or foreign
     */
    public boolean addNode(Node node) {
        if (node == null || node.getGraph() != this || nodesById.containsKey(node.getId())) {
            return false;
        }
        nodesById.put(node.getId(), node);
        adjEdList.put(node, new ArrayList<>());
        return true;
    }
    //——————

    /**
     * Creates and adds a new node with the specified identifier to this graph.
     *
     * @param id integer identifier for the new node
     * @return {@code true} if the node was added; {@code false} if a node with this ID already exists
     */
    public boolean addNode(int id) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, this));
    }
    //——————

    /**
     * Creates and adds a new node with the specified identifier and custom name to this graph.
     *
     * @param id integer identifier for the new node
     * @param name custom label or name for the new node
     * @return {@code true} if the node was added; {@code false} if a node with this ID already exists
     */
    public boolean addNode(int id, String name) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, name, this));
    }
    //——————

    /**
     * Removes the specified node from the graph if it exists.
     * Consequently removes all edges incident to that node (both outgoing and incoming).
     *
     * @param n the node to remove
     * @return {@code true} if the node was present in this graph and removed; {@code false} otherwise
     */
    public boolean removeNode(Node n) {
        if (n == null || !holdsNode(n)) {
            return false;
        }
        return removeNode(n.getId());
    }
    //——————

    /**
     * Removes the node with the specified ID from the graph if it exists.
     * Consequently removes all edges incident to that node (both outgoing and incoming).
     *
     * @param id integer identifier of the node to remove
     * @return {@code true} if the node was present and removed; {@code false} otherwise
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
    //——————

    /**
     * Returns a new sorted list of all nodes belonging to this graph.
     *
     * @return a new sorted {@link List} containing all nodes in this graph
     */
    public List<Node> getAllNodes() {
        List<Node> result = new ArrayList<>(nodesById.values());
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns the largest ID used by a node in the graph.
     *
     * @return the maximum integer identifier among all nodes in this graph
     * @throws NoSuchElementException if the graph contains no nodes
     */
    public int largestNodeId() {
        if (nodesById.isEmpty()) {
            throw new NoSuchElementException("The graph contains no nodes.");
        }
        int max = Integer.MIN_VALUE;
        for (int id : nodesById.keySet()) {
            if (id > max) {
                max = id;
            }
        }
        return max;
    }
    //——————

    /**
     * Returns the smallest ID used by a node in the graph.
     *
     * @return the minimum integer identifier among all nodes in this graph
     * @throws NoSuchElementException if the graph contains no nodes
     */
    public int smallestNodeId() {
        if (nodesById.isEmpty()) {
            throw new NoSuchElementException("The graph contains no nodes.");
        }
        int min = Integer.MAX_VALUE;
        for (int id : nodesById.keySet()) {
            if (id < min) {
                min = id;
            }
        }
        return min;
    }
    //——————

    /**
     * Returns a list without duplicates of the successors (or neighbors in the undirected case) of node {@code n}.
     *
     * @param n the node whose successors are requested
     * @return a new sorted {@link List} of unique successor nodes
     */
    public List<Node> getSuccessors(Node n) {
        if (!holdsNode(n)) return new ArrayList<>();
        Set<Node> unique = new LinkedHashSet<>(getSuccessorsMulti(n));
        List<Node> result = new ArrayList<>(unique);
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns a list without duplicates of the successors of the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return a new sorted {@link List} of unique successor nodes
     */
    public List<Node> getSuccessors(int id) {
        return getSuccessors(getNode(id));
    }
    //——————

    /**
     * Returns a list with possible duplicates of the successors (or neighbors in the undirected case) of node {@code n}.
     *
     * @param n the node whose successors are requested
     * @return a new sorted {@link List} containing one successor entry per outgoing edge
     */
    public List<Node> getSuccessorsMulti(Node n) {
        if (!holdsNode(n)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        for (Edge edge : getOutEdges(n)) {
            result.add(edge.to());
        }
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns a list with possible duplicates of the successors of the node with ID {@code id}.
     *
     * @param id integer identifier of the node
     * @return a new sorted {@link List} containing one successor entry per outgoing edge
     */
    public List<Node> getSuccessorsMulti(int id) {
        return getSuccessorsMulti(getNode(id));
    }
    //——————

    /**
     * Returns a sorted list of all edges leaving node {@code node}.
     *
     * @param node the node whose outgoing edges are requested
     * @return a new sorted {@link List} of outgoing edges from {@code node}, or an empty list if not in this graph
     */
    public List<Edge> getOutEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>(adjEdList.get(nodesById.get(node.getId())));
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all edges leaving the node with ID {@code id}.
     *
     * @param id integer identifier of the source node
     * @return a new sorted {@link List} of outgoing edges from the node with ID {@code id}, or an empty list if absent
     */
    public List<Edge> getOutEdges(int id) {
        return getOutEdges(getNode(id));
    }
    //——————

    /**
     * Returns a sorted list of all edges entering node {@code node}, counting parallel edges.
     *
     * @param node the node whose incoming edges are requested
     * @return a new sorted {@link List} of incoming edges to {@code node}, or an empty list if not in this graph
     */
    public List<Edge> getInEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>();
        for (List<Edge> outgoing : adjEdList.values()) {
            for (Edge edge : outgoing) {
                if (edge.to().getId() == node.getId()) {
                    result.add(edge);
                }
            }
        }
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all edges entering the node with ID {@code id}, counting parallel edges.
     *
     * @param id integer identifier of the target node
     * @return a new sorted {@link List} of incoming edges to the node with ID {@code id}, or an empty list if absent
     */
    public List<Edge> getInEdges(int id) {
        return getInEdges(getNode(id));
    }
    //——————

    /**
     * Returns a sorted list of all edges incident to the specified node (union of out-edges and in-edges).
     * Self-loops are included exactly once.
     *
     * @param node node whose incident edges are requested
     * @return a new sorted list of all incident edges to {@code node}, or an empty list if not in this graph
     */
    public List<Edge> getIncidentEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>(getOutEdges(node));
        for (Edge edge : getInEdges(node)) {
            if (!edge.isSelfLoop()) {
                result.add(edge);
            }
        }
        Collections.sort(result);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all edges incident to the node with ID {@code id}.
     * Self-loops are included exactly once.
     *
     * @param id integer identifier of the node
     * @return a new sorted list of all incident edges to the node with ID {@code id}, or an empty list if absent
     */
    public List<Edge> getIncidentEdges(int id) {
        return getIncidentEdges(getNode(id));
    }
    //——————

    /**
     * Returns a sorted list of all directed edges going from node {@code from} to node {@code to}.
     *
     * @param from the source node
     * @param to the target node
     * @return a new sorted list of directed edges going from {@code from} to {@code to}
     */
    public List<Edge> getEdges(Node from, Node to) {
        List<Edge> result = new ArrayList<>();
        if (!holdsNode(from) || !holdsNode(to)) return result;
        for (Edge edge : getOutEdges(from)) {
            if (edge.to().getId() == to.getId()) {
                result.add(edge);
            }
        }
        result.sort(null);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all directed edges going from node ID {@code u} to node ID {@code v}.
     *
     * @param u integer identifier of the source node
     * @param v integer identifier of the target node
     * @return a new sorted list of directed edges going from node ID {@code u} to node ID {@code v}
     */
    public List<Edge> getEdges(int u, int v) {
        return getEdges(getNode(u), getNode(v));
    }
    //——————

    /**
     * Returns whether a directed edge from node {@code u} to node {@code v} exists.
     *
     * @param u source node
     * @param v target node
     * @return {@code true} if a directed edge exists from {@code u} to {@code v}; {@code false} otherwise
     */
    public boolean adjacent(Node u, Node v) {
        return u != null && v != null && !getEdges(u, v).isEmpty();
    }
    //——————

    /**
     * Returns whether a directed edge from node ID {@code fromId} to node ID {@code toId} exists.
     *
     * @param fromId integer identifier of the source node
     * @param toId integer identifier of the target node
     * @return {@code true} if a directed edge exists from {@code fromId} to {@code toId}; {@code false} otherwise
     */
    public boolean adjacent(int fromId, int toId) {
        return adjacent(getNode(fromId), getNode(toId));
    }
    //——————

    /**
     * Computes the in-degree of the specified node in this graph.
     *
     * @param node the node whose in-degree is requested
     * @return the number of edges entering {@code node} (counting parallel edges and self-loops)
     */
    public int inDegree(Node node) {
        return getInEdges(node).size();
    }
    //——————

    /**
     * Computes the in-degree of the node with ID {@code id} in this graph.
     *
     * @param id integer identifier of the node
     * @return the number of edges entering the node with ID {@code id}
     */
    public int inDegree(int id) {
        return inDegree(getNode(id));
    }
    //——————

    /**
     * Computes the out-degree of the specified node in this graph.
     *
     * @param node the node whose out-degree is requested
     * @return the number of edges leaving {@code node} (counting parallel edges and self-loops)
     */
    public int outDegree(Node node) {
        return getOutEdges(node).size();
    }
    //——————

    /**
     * Computes the out-degree of the node with ID {@code id} in this graph.
     *
     * @param id integer identifier of the node
     * @return the number of edges leaving the node with ID {@code id}
     */
    public int outDegree(int id) {
        return outDegree(getNode(id));
    }
    //——————

    /**
     * Computes the total degree of the specified node in this graph.
     * For directed graphs, this is the sum of in-degree and out-degree.
     *
     * @param node the node whose degree is requested
     * @return in-degree plus out-degree of {@code node}
     */
    public int degree(Node node) {
        return inDegree(node) + outDegree(node);
    }
    //——————

    /**
     * Computes the total degree of the node with ID {@code id} in this graph.
     * For directed graphs, this is the sum of in-degree and out-degree.
     *
     * @param id integer identifier of the node
     * @return in-degree plus out-degree of the node with ID {@code id}
     */
    public int degree(int id) {
        return degree(getNode(id));
    }
    //——————

    private Node ensureNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Edge endpoint must not be null.");
        }
        if (node.getGraph() != this) {
            throw new IllegalArgumentException("Both endpoints must belong to this graph.");
        }
        Node existing = nodesById.get(node.getId());
        if (existing != null) {
            return existing;
        }
        nodesById.put(node.getId(), node);
        adjEdList.put(node, new ArrayList<>());
        return node;
    }
    //——————

    private void addEdgeInternal(Node from, Node to, Integer weight) {
        Node src = ensureNode(from);
        Node dst = ensureNode(to);
        adjEdList.get(src).add(new Edge(src, dst, weight));
    }
    //——————

    /**
     * Adds an unweighted directed edge from node {@code from} to node {@code to}.
     * The nodes are registered in this graph first if they do not already belong to it.
     *
     * @param from source node of the edge; must belong to this graph and not be {@code null}
     * @param to target node of the edge; must belong to this graph and not be {@code null}
     * @throws IllegalArgumentException if either endpoint is {@code null} or belongs to a different graph
     */
    public void addEdge(Node from, Node to) {
        addEdgeInternal(from, to, null);
    }
    //——————

    /**
     * Adds an unweighted directed edge between node IDs, creating either endpoint in this graph if absent.
     *
     * @param fromId integer identifier of the source node
     * @param toId integer identifier of the target node
     */
    public void addEdge(int fromId, int toId) {
        addEdgeInternal(getOrCreateNode(fromId), getOrCreateNode(toId), null);
    }
    //——————

    /**
     * Adds a weighted directed edge from node {@code from} to node {@code to}.
     * The nodes are registered in this graph first if they do not already belong to it.
     *
     * @param from source node of the edge; must belong to this graph and not be {@code null}
     * @param to target node of the edge; must belong to this graph and not be {@code null}
     * @param weight integer weight of the edge
     * @throws IllegalArgumentException if either endpoint is {@code null} or belongs to a different graph
     */
    public void addEdge(Node from, Node to, int weight) {
        addEdgeInternal(from, to, weight);
    }
    //——————

    /**
     * Adds a weighted directed edge between node IDs, creating either endpoint in this graph if absent.
     *
     * @param fromId integer identifier of the source node
     * @param toId integer identifier of the target node
     * @param weight integer weight of the edge
     */
    public void addEdge(int fromId, int toId, int weight) {
        addEdgeInternal(getOrCreateNode(fromId), getOrCreateNode(toId), weight);
    }
    //——————

    /**
     * Adds the supplied edge to this graph, registering its endpoints if not already registered.
     *
     * @param edge edge to add; must not be {@code null} and its endpoints must belong to this graph
     * @throws IllegalArgumentException if {@code edge} is {@code null} or belongs to another graph
     */
    public void addEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge to add must not be null.");
        }
        if (edge.from().getGraph() != this || edge.to().getGraph() != this) {
            throw new IllegalArgumentException("Edge endpoints must belong to this graph.");
        }
        addEdgeInternal(edge.from(), edge.to(), edge.getWeight());
    }
    //——————

    /**
     * Removes an edge going from node {@code from} to node {@code to}.
     * If multiple parallel edges exist, removes one of them.
     *
     * @param from source node of the edge to remove
     * @param to target node of the edge to remove
     * @return {@code true} if an edge was found and removed; {@code false} otherwise
     */
    public boolean removeEdge(Node from, Node to) {
        if (!holdsNode(from) || !holdsNode(to)) return false;
        List<Edge> outgoing = adjEdList.get(nodesById.get(from.getId()));
        if (outgoing == null) return false;
        for (int i = 0; i < outgoing.size(); i++) {
            if (outgoing.get(i).to().getId() == to.getId()) {
                outgoing.remove(i);
                return true;
            }
        }
        return false;
    }
    //——————

    /**
     * Removes an edge going from node ID {@code fromId} to node ID {@code toId}.
     * If multiple parallel edges exist, removes one of them.
     *
     * @param fromId integer identifier of the source node
     * @param toId integer identifier of the target node
     * @return {@code true} if an edge was found and removed; {@code false} otherwise
     */
    public boolean removeEdge(int fromId, int toId) {
        return removeEdge(getNode(fromId), getNode(toId));
    }
    //——————

    /**
     * Removes an edge going from node {@code from} to node {@code to} with the specified weight.
     *
     * @param from source node of the edge to remove
     * @param to target node of the edge to remove
     * @param weight integer weight of the edge to remove
     * @return {@code true} if a matching edge was found and removed; {@code false} otherwise
     */
    public boolean removeEdge(Node from, Node to, int weight) {
        if (!holdsNode(from) || !holdsNode(to)) return false;
        List<Edge> outgoing = adjEdList.get(nodesById.get(from.getId()));
        if (outgoing == null) return false;
        for (int i = 0; i < outgoing.size(); i++) {
            Edge edge = outgoing.get(i);
            if (edge.to().getId() == to.getId() && edge.getWeight() != null && edge.getWeight() == weight) {
                outgoing.remove(i);
                return true;
            }
        }
        return false;
    }
    //——————

    /**
     * Removes an edge going from node ID {@code fromId} to node ID {@code toId} with the specified weight.
     *
     * @param fromId integer identifier of the source node
     * @param toId integer identifier of the target node
     * @param weight integer weight of the edge to remove
     * @return {@code true} if a matching edge was found and removed; {@code false} otherwise
     */
    public boolean removeEdge(int fromId, int toId, int weight) {
        return removeEdge(getNode(fromId), getNode(toId), weight);
    }
    //——————

    /**
     * Removes the specified edge from this graph.
     *
     * @param edge edge to remove
     * @return {@code true} if an equal edge was found and removed; {@code false} otherwise
     */
    public boolean removeEdge(Edge edge) {
        if (edge == null || !holdsNode(edge.from()) || !holdsNode(edge.to())) return false;
        List<Edge> outgoing = adjEdList.get(nodesById.get(edge.from().getId()));
        if (outgoing == null) return false;
        return outgoing.remove(edge);
    }
    //——————

    /**
     * Determines whether an edge exists in this graph from node {@code u} to node {@code v}.
     *
     * @param u source node
     * @param v target node
     * @return {@code true} if at least one directed edge goes from {@code u} to {@code v}; {@code false} otherwise
     */
    public boolean existsEdge(Node u, Node v) {
        return u != null && v != null && holdsNode(u) && holdsNode(v) && !getEdges(u, v).isEmpty();
    }
    //——————

    /**
     * Determines whether an edge exists in this graph from node with ID {@code u} to node with ID {@code v}.
     *
     * @param u ID of the source node
     * @param v ID of the target node
     * @return {@code true} if at least one directed edge goes from {@code u} to {@code v}; {@code false} otherwise
     */
    public boolean existsEdge(int u, int v) {
        return existsEdge(getNode(u), getNode(v));
    }
    //——————

    /**
     * Determines whether the specified edge exists in this graph.
     *
     * @param e edge to look for
     * @return {@code true} if an edge equal to {@code e} exists in this graph; {@code false} otherwise
     */
    public boolean existsEdge(Edge e) {
        if (e == null || !holdsNode(e.from()) || !holdsNode(e.to())) return false;
        for (Edge edge : getEdges(e.from(), e.to())) {
            if (edge.equals(e)) return true;
        }
        return false;
    }
    //——————

    /**
     * Determines whether an edge from node {@code u} to node {@code v} is a multi-edge
     * (i.e., there is at least one other edge from {@code u} to {@code v} in this graph).
     *
     * @param u source node
     * @param v target node
     * @return {@code true} if two or more edges go from {@code u} to {@code v}; {@code false} otherwise
     */
    public boolean isMultiEdge(Node u, Node v) {
        return u != null && v != null && holdsNode(u) && holdsNode(v) && getEdges(u, v).size() > 1;
    }
    //——————

    /**
     * Determines whether an edge from node with ID {@code u} to node with ID {@code v} is a multi-edge.
     *
     * @param u ID of the source node
     * @param v ID of the target node
     * @return {@code true} if two or more edges go from {@code u} to {@code v}; {@code false} otherwise
     */
    public boolean isMultiEdge(int u, int v) {
        return isMultiEdge(getNode(u), getNode(v));
    }
    //——————

    /**
     * Determines whether the specified edge is a multi-edge in this graph
     * (i.e., at least one other edge shares its endpoints in this graph).
     *
     * @param edge edge to check
     * @return {@code true} if at least one other edge shares the same endpoints in this graph; {@code false} otherwise
     */
    public boolean isMultiEdge(Edge edge) {
        return edge != null && isMultiEdge(edge.from(), edge.to());
    }
    //——————

    /**
     * Indicates whether this graph currently contains no nodes.
     *
     * @return {@code true} when the graph has no nodes; {@code false} otherwise
     */
    public boolean isEmpty() {
        return nodesById.isEmpty();
    }
    //——————

    /**
     * Removes every node and edge from this graph, resetting it to an empty state.
     */
    public void clear() {
        adjEdList.clear();
        nodesById.clear();
    }
    //——————

    /**
     * Internal helper that returns the graph's node for an ID, creating it when missing.
     * Package-private so Edge's ID constructors can share this behavior.
     *
     * @param id integer identifier of the node
     * @return existing or newly created {@link Node} belonging to this graph
     */
    Node getOrCreateNode(int id) {
        Node existing = nodesById.get(id);
        if (existing != null) return existing;
        Node created = new Node(id, this);
        nodesById.put(id, created);
        adjEdList.put(created, new ArrayList<>());
        return created;
    }
    //——————

    /**
     * Returns a sorted list of all edges currently present in this graph.
     * Edges are ordered by source ID, target ID, and weight (unweighted first).
     *
     * @return a new sorted {@link List} of all edges in this graph
     */
    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        for (List<Edge> outgoing : adjEdList.values()) {
            result.addAll(outgoing);
        }
        Collections.sort(result);
        return result;
    }
    //——————

    /**
     * Returns a sorted list of all edges currently present in this graph (alias for {@link #getAllEdges()}).
     *
     * @return a new sorted {@link List} of all edges in this graph
     */
    public List<Edge> getEdges() {
        return getAllEdges();
    }
    //——————

    /**
     * Returns a representation of this graph in the Successor Array (SA) formalism.
     * <p>
     * For nodes sorted by ID, their successors are enumerated in ascending order
     * (preserving multi-edges), terminated by {@code 0} for each node.
     * If the graph is empty, an empty array is returned.
     * </p>
     *
     * @return an {@code int[]} representing the graph in the successor array format
     */
    public int[] toSuccessorArray() {
        if (nodesById.isEmpty()) {
            return new int[0];
        }
        List<Integer> list = new ArrayList<>();
        for (Node u : getAllNodes()) {
            for (Edge e : getOutEdges(u)) {
                list.add(e.to().getId());
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
     * Returns an adjacency matrix representation of this graph.
     * <p>
     * The matrix is of size {@code N x N} where {@code N} is the number of nodes in this graph.
     * Rows and columns correspond to nodes ordered by increasing ID.
     * The entry at row {@code i} and column {@code j} contains the number of directed edges
     * connecting the {@code i}-th node to the {@code j}-th node.
     * Multi-edges result in values greater than 1, and self-loops appear as non-zero values on the diagonal.
     * If the graph contains no nodes, an empty {@code 0 x 0} matrix is returned.
     * </p>
     *
     * @return an {@code int[][]} representing the adjacency matrix of this graph
     */
    public int[][] toAdjMatrix() {
        List<Node> nodes = getAllNodes();
        int n = nodes.size();
        int[][] matrix = new int[n][n];
        if (n == 0) {
            return matrix;
        }
        Map<Integer, Integer> idToIndex = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            idToIndex.put(nodes.get(i).getId(), i);
        }
        for (int i = 0; i < n; i++) {
            Node u = nodes.get(i);
            for (Edge e : getOutEdges(u)) {
                Integer col = idToIndex.get(e.to().getId());
                if (col != null) {
                    matrix[i][col]++;
                }
            }
        }
        return matrix;
    }
    //——————

    /**
     * Determines whether this graph is a multi-graph (i.e., contains at least one multi-edge).
     *
     * @return {@code true} if at least two directed edges connect the same pair of nodes in the same direction;
     *         {@code false} otherwise
     */
    public boolean isMultiGraph() {
        for (List<Edge> outgoing : adjEdList.values()) {
            Set<Integer> seenTargetIds = new LinkedHashSet<>();
            for (Edge edge : outgoing) {
                if (!seenTargetIds.add(edge.to().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
    //——————

    /**
     * Determines whether this graph contains any self-loops (edges whose source and target are the same node).
     *
     * @return {@code true} if this graph contains at least one self-loop; {@code false} otherwise
     */
    public boolean hasSelfLoops() {
        for (List<Edge> outgoing : adjEdList.values()) {
            for (Edge edge : outgoing) {
                if (edge.isSelfLoop()) {
                    return true;
                }
            }
        }
        return false;
    }
    //——————

    /**
     * Determines whether this graph is a simple graph.
     * A graph is simple if and only if it has neither self-loops nor multi-edges.
     *
     * @return {@code true} if this graph has neither self-loops nor multi-edges; {@code false} otherwise
     */
    public boolean isSimpleGraph() {
        return !hasSelfLoops() && !isMultiGraph();
    }
    //——————

    /**
     * Computes the reverse graph (G^-1) in a new graph instance.
     * All edge directions are inverted while preserving node identifiers, names, and edge weights.
     *
     * @return a new {@link Graph} instance representing the reverse of this graph
     */
    public Graph getReverse() {
        Graph reverse = new Graph();
        for (Node node : getAllNodes()) {
            reverse.addNode(node.getId(), node.getName());
        }
        for (Edge edge : getAllEdges()) {
            if (edge.isWeighted()) {
                reverse.addEdge(edge.to().getId(), edge.from().getId(), edge.getWeight());
            } else {
                reverse.addEdge(edge.to().getId(), edge.from().getId());
            }
        }
        return reverse;
    }
    //——————

    /**
     * Computes the transitive closure (G+) of this graph in a new graph instance using Warshall's algorithm.
     * In the transitive closure, a directed edge exists from node u to node v if and only if
     * there exists a directed path of length 1 or more from u to v.
     *
     * @return a new {@link Graph} instance representing the transitive closure of this graph
     */
    public Graph getTransitiveClosure() {
        Graph closure = new Graph();
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
            for (int j = 0; j < n; j++) {
                if (i != j && reach[i][j]) {
                    closure.addEdge(nodes.get(i).getId(), nodes.get(j).getId());
                }
            }
        }

        return closure;
    }
    //——————

    /**
     * Transforms this graph into a simple graph by removing all self-loops and multi-edges.
     * Returns the simplified graph as a new graph instance.
     *
     * @return a new simple {@link Graph} instance without self-loops or multi-edges
     */
    public Graph toSimpleGraph() {
        Graph simple = new Graph();
        for (Node node : getAllNodes()) {
            simple.addNode(node.getId(), node.getName());
        }
        for (Node u : getAllNodes()) {
            for (Node v : getSuccessors(u)) {
                if (u.getId() != v.getId()) {
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
     * Creates a deep structural copy of this graph into a new graph instance.
     * All nodes and edges are duplicated with their identifiers, names, and weights preserved.
     *
     * @return a new {@link Graph} instance structurally identical to this graph
     */
    public Graph copy() {
        Graph copy = new Graph();
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
     * Performs a Depth-First Search (DFS) traversal of the graph,
     * launched by default from the node with the lowest identifier.
     *
     * @return a {@link List} of nodes in depth-first visitation order, or an empty list if this graph is empty
     */
    public List<Node> getDFS() {
        if (isEmpty()) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        List<Node> nodes = getAllNodes();
        Collections.sort(nodes);
        for (Node node : nodes) {
            if (!visited.contains(node)) {
                dfsHelper(node, visited, result);
            }
        }
        return result;
    }
    //——————

    /**
     * Performs a Depth-First Search (DFS) traversal of the graph starting from node {@code u}.
     * Successor nodes are explored in increasing order of their identifiers for reproducible visitation.
     *
     * @param u the starting node for the traversal
     * @return a {@link List} of nodes reachable from {@code u} in depth-first order, or an empty list if {@code u} is not in this graph
     */
    public List<Node> getDFS(Node u) {
        if (!holdsNode(u)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        dfsHelper(u, visited, result);
        return result;
    }
    //——————

    private void dfsHelper(Node current, Set<Node> visited, List<Node> result) {
        visited.add(current);
        result.add(current);
        for (Node succ : getSuccessors(current)) {
            if (!visited.contains(succ)) {
                dfsHelper(succ, visited, result);
            }
        }
    }
    //——————

    /**
     * Performs a Depth-First Search (DFS) traversal of the graph starting from the node with ID {@code id}.
     *
     * @param id integer identifier of the starting node
     * @return a {@link List} of nodes reachable from the node with ID {@code id} in depth-first order
     */
    public List<Node> getDFS(int id) {
        return getDFS(getNode(id));
    }
    //——————

    /**
     * Performs a Breadth-First Search (BFS) traversal of the graph,
     * launched by default from the node with the lowest identifier.
     *
     * @return a {@link List} of nodes in breadth-first visitation order, or an empty list if this graph is empty
     */
    public List<Node> getBFS() {
        if (isEmpty()) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        List<Node> nodes = getAllNodes();
        Collections.sort(nodes);
        for (Node node : nodes) {
            if (!visited.contains(node)) {
                bfsHelper(node, visited, result);
            }
        }
        return result;
    }
    //——————

    /**
     * Performs a Breadth-First Search (BFS) traversal of the graph starting from node {@code u}.
     * Successor nodes are explored in increasing order of their identifiers for reproducible visitation.
     *
     * @param u the starting node for the traversal
     * @return a {@link List} of nodes reachable from {@code u} in breadth-first order, or an empty list if {@code u} is not in this graph
     */
    public List<Node> getBFS(Node u) {
        if (!holdsNode(u)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        bfsHelper(u, visited, result);
        return result;
    }
    //——————

    private void bfsHelper(Node start, Set<Node> visited, List<Node> result) {
        Queue<Node> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.add(current);
            for (Node succ : getSuccessors(current)) {
                if (!visited.contains(succ)) {
                    visited.add(succ);
                    queue.add(succ);
                }
            }
        }
    }
    //——————

    /**
     * Performs a Breadth-First Search (BFS) traversal of the graph starting from the node with ID {@code id}.
     *
     * @param id integer identifier of the starting node
     * @return a {@link List} of nodes reachable from the node with ID {@code id} in breadth-first order
     */
    public List<Node> getBFS(int id) {
        return getBFS(getNode(id));
    }
    //——————

    /**
     * Performs an enriched Depth-First Search (DFS) traversal of the graph with color visit information,
     * launched by default from the node with the lowest identifier.
     *
     * @param nodeVisit map to populate with each node's visit characterization (color, predecessor, discovery, finished)
     * @param edgeVisit map to populate with each visited edge's classification (TREE, BACKWARD, FORWARD, CROSS)
     * @return a {@link List} of nodes in depth-first visitation order, or an empty list if this graph is empty
     * @throws IllegalArgumentException if {@code nodeVisit} or {@code edgeVisit} is {@code null}
     */
    public List<Node> getDFSWithVisitInfo(Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        if (nodeVisit == null || edgeVisit == null) {
            throw new IllegalArgumentException("nodeVisit and edgeVisit maps must not be null");
        }
        if (isEmpty()) {
            nodeVisit.clear();
            edgeVisit.clear();
            return new ArrayList<>();
        }
        return getDFSWithVisitInfo(smallestNodeId(), nodeVisit, edgeVisit);
    }
    //——————

    /**
     * Performs an enriched Depth-First Search (DFS) traversal of the graph starting from node {@code u},
     * collecting node visit information and edge classifications along the traversal.
     * Successor nodes and outgoing edges are explored in increasing order of their identifiers
     * (and increasing edge weights) for reproducible traversal.
     *
     * @param u the starting node for the traversal
     * @param nodeVisit map to populate with each node's visit characterization
     * @param edgeVisit map to populate with each visited edge's classification
     * @return a {@link List} of nodes reachable from {@code u} in depth-first order, or an empty list if {@code u} is not in this graph
     * @throws IllegalArgumentException if {@code nodeVisit} or {@code edgeVisit} is {@code null}
     */
    public List<Node> getDFSWithVisitInfo(Node u, Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        if (nodeVisit == null || edgeVisit == null) {
            throw new IllegalArgumentException("nodeVisit and edgeVisit maps must not be null");
        }
        nodeVisit.clear();
        edgeVisit.clear();
        for (Node n : getAllNodes()) {
            nodeVisit.put(n, new NodeVisitInfo(NodeColor.WHITE, null, null, null));
        }
        List<Node> result = new ArrayList<>();
        if (u == null || !holdsNode(u)) {
            return result;
        }
        dfsTime = 0;
        dfsVisitWithInfoHelper(u, nodeVisit, edgeVisit, result);
        return result;
    }
    //——————

    private void dfsVisitWithInfoHelper(Node current, Map<Node, NodeVisitInfo> nodeVisit,
                                        Map<Edge, EdgeVisitType> edgeVisit, List<Node> result) {
        dfsTime++;
        NodeVisitInfo currentInfo = nodeVisit.get(current);
        currentInfo.setColor(NodeColor.GRAY);
        currentInfo.setDiscovery(dfsTime);
        result.add(current);

        for (Edge edge : getOutEdges(current)) {
            Node target = edge.to();
            NodeVisitInfo targetInfo = nodeVisit.get(target);
            if (targetInfo == null) continue;
            if (targetInfo.getColor() == NodeColor.WHITE) {
                edgeVisit.put(edge, EdgeVisitType.TREE);
                targetInfo.setPredecessor(current);
                dfsVisitWithInfoHelper(target, nodeVisit, edgeVisit, result);
            } else if (targetInfo.getColor() == NodeColor.GRAY) {
                edgeVisit.put(edge, EdgeVisitType.BACKWARD);
            } else {
                if (currentInfo.getDiscovery() < targetInfo.getDiscovery()) {
                    edgeVisit.put(edge, EdgeVisitType.FORWARD);
                } else {
                    edgeVisit.put(edge, EdgeVisitType.CROSS);
                }
            }
        }

        dfsTime++;
        currentInfo.setColor(NodeColor.BLACK);
        currentInfo.setFinished(dfsTime);
    }
    //——————

    /**
     * Performs an enriched Depth-First Search (DFS) traversal of the graph starting from the node with ID {@code id},
     * collecting node visit information and edge classifications along the traversal.
     *
     * @param id integer identifier of the starting node
     * @param nodeVisit map to populate with each node's visit characterization
     * @param edgeVisit map to populate with each visited edge's classification
     * @return a {@link List} of nodes reachable from the node with ID {@code id} in depth-first order
     * @throws IllegalArgumentException if {@code nodeVisit} or {@code edgeVisit} is {@code null}
     */
    public List<Node> getDFSWithVisitInfo(int id, Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        return getDFSWithVisitInfo(getNode(id), nodeVisit, edgeVisit);
    }
    //——————

    /**
     * Returns an elementary DOT description of this directed graph.
     *
     * @return a valid DOT description string for this directed graph
     */
    public String toDotString() {
        StringBuilder output = new StringBuilder("digraph {\n\trankdir=LR\n");
        List<Node> nodes = getAllNodes();
        Collections.sort(nodes);
        for (Node node : nodes) {
            if (degree(node) == 0) {
                output.append("\t").append(node.getId()).append("\n");
            } else {
                List<Edge> edges = getOutEdges(node);
                Collections.sort(edges);
                for (Edge edge : edges) {
                    if (edge.isWeighted()) {
                        output.append("\t").append(edge.from().getId())
                              .append(" -> ").append(edge.to().getId())
                              .append(" [label=").append(edge.getWeight())
                              .append(", len=").append(edge.getWeight()).append("]\n");
                    } else {
                        output.append("\t").append(edge.from().getId())
                              .append(" -> ").append(edge.to().getId()).append("\n");
                    }
                }
            }
        }
        return output.append("}\n").toString();
    }
    //——————

    /**
     * Exports this graph as a file in the DOT syntax using the specified file extension.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @param extension the file extension to append (e.g., {@code ".gv"}, {@code ".dot"}, or {@code "dot"})
     * @throws IllegalArgumentException if {@code fileName} or {@code extension} is {@code null}
     * @throws RuntimeException if an I/O error occurs while writing the file
     */
    public void toDotFile(String fileName, String extension) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName must not be null");
        }
        if (extension == null) {
            throw new IllegalArgumentException("extension must not be null");
        }
        String ext = extension.startsWith(".") ? extension : "." + extension;
        String fullPath = fileName + ext;
        try {
            Path path = Path.of(fullPath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, toDotString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write DOT file to " + fullPath, e);
        }
    }
    //——————

    /**
     * Exports this graph as a file in the DOT syntax using the default extension {@code ".gv"}.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @throws IllegalArgumentException if {@code fileName} is {@code null}
     * @throws RuntimeException if an I/O error occurs while writing the file
     */
    public void toDotFile(String fileName) {
        toDotFile(fileName, ".gv");
    }
    //——————

    /**
     * Imports a graph described in the simplified DOT format from a file using the specified extension.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @param extension the file extension to read (e.g., {@code ".gv"}, {@code ".dot"}, or {@code "dot"})
     * @return a {@link Graph} (or {@link UndirectedGraph} if undirected) imported from the file
     * @throws IllegalArgumentException if {@code fileName} or {@code extension} is {@code null}, or if the file does not exist
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static Graph fromDotFile(String fileName, String extension) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName must not be null");
        }
        if (extension == null) {
            throw new IllegalArgumentException("extension must not be null");
        }
        String ext = extension.startsWith(".") ? extension : "." + extension;
        String fullPath = fileName.endsWith(ext) ? fileName : fileName + ext;
        Path path = Path.of(fullPath);
        if (!Files.exists(path)) {
            Path altPath = Path.of(fileName);
            if (Files.exists(altPath)) {
                path = altPath;
            } else {
                Path srcPath = Path.of("src", fullPath);
                Path altSrcPath = Path.of("src", fileName);
                if (Files.exists(srcPath)) {
                    path = srcPath;
                } else if (Files.exists(altSrcPath)) {
                    path = altSrcPath;
                } else {
                    throw new IllegalArgumentException("DOT file does not exist: " + fullPath);
                }
            }
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read DOT file from " + fullPath, e);
        }

        return parseDotLines(lines);
    }
    //——————

    /**
     * Imports a graph described in the simplified DOT format from a file using default extension {@code ".gv"}.
     * The argument {@code fileName} specifies the file path with no extension.
     *
     * @param fileName the path to the DOT file without extension
     * @return a {@link Graph} (or {@link UndirectedGraph} if undirected) imported from the file
     * @throws IllegalArgumentException if {@code fileName} is {@code null}, or if the file does not exist
     * @throws RuntimeException if an I/O error occurs while reading the file
     */
    public static Graph fromDotFile(String fileName) {
        return fromDotFile(fileName, ".gv");
    }
    //——————

    private static Graph parseDotLines(List<String> lines) {
        boolean isDirected = true;
        boolean graphTypeDetermined = false;
        Graph graph = null;
        boolean inBlockComment = false;

        Pattern weightPattern = Pattern.compile("(?:label|len)\\s*=\\s*(-?\\d+)");

        for (String rawLine : lines) {
            String line = rawLine;

            if (inBlockComment) {
                int endIdx = line.indexOf("*/");
                if (endIdx >= 0) {
                    line = line.substring(endIdx + 2);
                    inBlockComment = false;
                } else {
                    continue;
                }
            }
            while (line.contains("/*")) {
                int startIdx = line.indexOf("/*");
                int endIdx = line.indexOf("*/", startIdx + 2);
                if (endIdx >= 0) {
                    line = line.substring(0, startIdx) + line.substring(endIdx + 2);
                } else {
                    line = line.substring(0, startIdx);
                    inBlockComment = true;
                    break;
                }
            }

            int hashIdx = line.indexOf('#');
            int slashIdx = line.indexOf("//");
            int commentIdx = -1;
            if (hashIdx >= 0 && slashIdx >= 0) {
                commentIdx = Math.min(hashIdx, slashIdx);
            } else if (hashIdx >= 0) {
                commentIdx = hashIdx;
            } else if (slashIdx >= 0) {
                commentIdx = slashIdx;
            }
            if (commentIdx >= 0) {
                line = line.substring(0, commentIdx);
            }

            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (!graphTypeDetermined) {
                String lower = line.toLowerCase();
                if (lower.contains("digraph")) {
                    isDirected = true;
                    graphTypeDetermined = true;
                    graph = new Graph();
                    continue;
                } else if (lower.contains("graph")) {
                    isDirected = false;
                    graphTypeDetermined = true;
                    graph = new UndirectedGraph();
                    continue;
                }
            }

            if (line.equals("}")) {
                continue;
            }

            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1).trim();
            }

            if (line.contains("->") || line.contains("--")) {
                boolean edgeIsDirected = line.contains("->");
                String op = edgeIsDirected ? "->" : "--";
                int opIdx = line.indexOf(op);
                String srcStr = line.substring(0, opIdx).trim();
                String rest = line.substring(opIdx + op.length()).trim();

                Integer weight = null;
                String targetStr;
                int bracketIdx = rest.indexOf('[');
                if (bracketIdx >= 0) {
                    targetStr = rest.substring(0, bracketIdx).trim();
                    int closeBracketIdx = rest.indexOf(']', bracketIdx);
                    String attrStr = (closeBracketIdx >= 0)
                            ? rest.substring(bracketIdx + 1, closeBracketIdx).trim()
                            : rest.substring(bracketIdx + 1).trim();
                    Matcher m = weightPattern.matcher(attrStr);
                    if (m.find()) {
                        weight = Integer.parseInt(m.group(1));
                    }
                } else {
                    targetStr = rest;
                }

                if (graph == null) {
                    graph = edgeIsDirected ? new Graph() : new UndirectedGraph();
                    graphTypeDetermined = true;
                }

                try {
                    int srcId = Integer.parseInt(srcStr);
                    int targetId = Integer.parseInt(targetStr);
                    if (weight != null) {
                        graph.addEdge(srcId, targetId, weight);
                    } else {
                        graph.addEdge(srcId, targetId);
                    }
                } catch (NumberFormatException ignored) {
                    // Non-numeric node ID or other directive; skip gracefully
                }
                continue;
            }

            String nodePart = line;
            int bIdx = nodePart.indexOf('[');
            if (bIdx >= 0) {
                nodePart = nodePart.substring(0, bIdx).trim();
            }
            if (nodePart.matches("-?\\d+")) {
                if (graph == null) {
                    graph = isDirected ? new Graph() : new UndirectedGraph();
                    graphTypeDetermined = true;
                }
                int nodeId = Integer.parseInt(nodePart);
                graph.getOrCreateNode(nodeId);
            }
        }

        if (graph == null) {
            graph = isDirected ? new Graph() : new UndirectedGraph();
        }
        return graph;
    }
    //——————

    /**
     * Generates a random directed graph according to the Erdős–Rényi G(n, p) model.
     * Nodes with identifiers 1 to {@code nbNodes} are created, and each directed pair (u, v)
     * (excluding self-loops) is joined by an edge with probability {@code edgeProbability}.
     *
     * @param nbNodes number of nodes to generate (must be &gt;= 0)
     * @param edgeProbability probability of edge existence between any two nodes (0.0 &lt;= p &lt;= 1.0)
     * @return a new randomly generated {@link Graph}
     * @throws IllegalArgumentException if {@code nbNodes < 0} or {@code edgeProbability} is not in [0.0, 1.0]
     */
    public static Graph random(int nbNodes, double edgeProbability) {
        if (nbNodes < 0) {
            throw new IllegalArgumentException("nbNodes must be non-negative");
        }
        if (edgeProbability < 0.0 || edgeProbability > 1.0) {
            throw new IllegalArgumentException("edgeProbability must be between 0.0 and 1.0");
        }
        Graph g = new Graph();
        for (int i = 1; i <= nbNodes; i++) {
            g.addNode(i);
        }
        Random rng = new Random();
        for (int u = 1; u <= nbNodes; u++) {
            for (int v = 1; v <= nbNodes; v++) {
                if (u != v && rng.nextDouble() < edgeProbability) {
                    g.addEdge(u, v);
                }
            }
        }
        return g;
    }
    //——————

    /**
     * Generates a random Directed Acyclic Graph (DAG) with {@code nbNodes} nodes.
     * Edges are only directed from nodes with smaller IDs to nodes with larger IDs,
     * guaranteeing the absence of directed cycles.
     *
     * @param nbNodes number of nodes (must be >= 0)
     * @param edgeProbability probability of edge existence between any ordered pair u &lt; v (0.0 &lt;= p &lt;= 1.0)
     * @return a new randomly generated DAG as a {@link Graph}
     * @throws IllegalArgumentException if {@code nbNodes < 0} or {@code edgeProbability} is not in [0.0, 1.0]
     */
    public static Graph randomDAG(int nbNodes, double edgeProbability) {
        if (nbNodes < 0) {
            throw new IllegalArgumentException("nbNodes must be non-negative");
        }
        if (edgeProbability < 0.0 || edgeProbability > 1.0) {
            throw new IllegalArgumentException("edgeProbability must be between 0.0 and 1.0");
        }
        Graph g = new Graph();
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
     * Generates a random strongly connected directed graph with {@code nbNodes} nodes.
     * A random spanning directed cycle is created to ensure connectivity, followed by
     * {@code extraEdges} additional random directed edges.
     *
     * @param nbNodes number of nodes (must be >= 1)
     * @param extraEdges number of additional random edges (must be >= 0)
     * @return a new strongly connected {@link Graph}
     * @throws IllegalArgumentException if {@code nbNodes < 1} or {@code extraEdges < 0}
     */
    public static Graph randomConnected(int nbNodes, int extraEdges) {
        if (nbNodes < 1) {
            throw new IllegalArgumentException("nbNodes must be at least 1");
        }
        if (extraEdges < 0) {
            throw new IllegalArgumentException("extraEdges must be non-negative");
        }
        Graph g = new Graph();
        for (int i = 1; i <= nbNodes; i++) {
            g.addNode(i);
        }
        if (nbNodes == 1) {
            return g;
        }
        List<Integer> perm = new ArrayList<>();
        for (int i = 1; i <= nbNodes; i++) {
            perm.add(i);
        }
        Collections.shuffle(perm);
        for (int i = 0; i < nbNodes; i++) {
            int u = perm.get(i);
            int v = perm.get((i + 1) % nbNodes);
            g.addEdge(u, v);
        }
        Random rng = new Random();
        for (int k = 0; k < extraEdges; k++) {
            int u = perm.get(rng.nextInt(nbNodes));
            int v = perm.get(rng.nextInt(nbNodes));
            if (u != v) {
                g.addEdge(u, v);
            }
        }
        return g;
    }
    //——————
}
