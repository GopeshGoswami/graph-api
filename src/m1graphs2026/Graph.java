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


public class Graph {
    
    private final Map<Node, List<Edge>> adjEdList = new LinkedHashMap<>();

    private final Map<Integer, Node> nodesById = new LinkedHashMap<>();

    private static int dfsTime = 0;

    public Graph() {
    }
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
    public Node getNode(int id) {
        return nodesById.get(id);
    }
    public int nbNodes() {
        return nodesById.size();
    }
    public int nbEdges() {
        int count = 0;
        for (List<Edge> edges : adjEdList.values()) {
            count += edges.size();
        }
        return count;
    }
    public boolean usesNode(int id) {
        return nodesById.containsKey(id);
    }
    public boolean usesNode(Node node) {
        return node != null && usesNode(node.getId());
    }
    
    public boolean holdsNode(Node node) {
        return node != null && node.getGraph() == this && usesNode(node.getId());
    }
    
    public boolean holdsNode(int id) {
        return getNode(id) != null;
    }
    
    public boolean addNode(Node node) {
        if (node == null || node.getGraph() != this || nodesById.containsKey(node.getId())) {
            return false;
        }
        nodesById.put(node.getId(), node);
        adjEdList.put(node, new ArrayList<>());
        return true;
    }
    
    public boolean addNode(int id) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, this));
    }
   
    public boolean addNode(int id, String name) {
        if (nodesById.containsKey(id)) return false;
        return addNode(new Node(id, name, this));
    }
    
    public boolean removeNode(Node n) {
        if (n == null || !holdsNode(n)) {
            return false;
        }
        return removeNode(n.getId());
    }
    
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
    
    public List<Node> getAllNodes() {
        List<Node> result = new ArrayList<>(nodesById.values());
        result.sort(null);
        return result;
    }
    
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
    
    public List<Node> getSuccessors(Node n) {
        if (!holdsNode(n)) return new ArrayList<>();
        Set<Node> unique = new LinkedHashSet<>(getSuccessorsMulti(n));
        List<Node> result = new ArrayList<>(unique);
        result.sort(null);
        return result;
    }
   
    public List<Node> getSuccessors(int id) {
        return getSuccessors(getNode(id));
    }
    
    public List<Node> getSuccessorsMulti(Node n) {
        if (!holdsNode(n)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        for (Edge edge : getOutEdges(n)) {
            result.add(edge.to());
        }
        result.sort(null);
        return result;
    }
    
    public List<Node> getSuccessorsMulti(int id) {
        return getSuccessorsMulti(getNode(id));
    }
    
    public List<Edge> getOutEdges(Node node) {
        if (!holdsNode(node)) return new ArrayList<>();
        List<Edge> result = new ArrayList<>(adjEdList.get(nodesById.get(node.getId())));
        result.sort(null);
        return result;
    }
    
    public List<Edge> getOutEdges(int id) {
        return getOutEdges(getNode(id));
    }
    
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
    
    public List<Edge> getInEdges(int id) {
        return getInEdges(getNode(id));
    }
    
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
    
    public List<Edge> getIncidentEdges(int id) {
        return getIncidentEdges(getNode(id));
    }
    
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
    
    public List<Edge> getEdges(int u, int v) {
        return getEdges(getNode(u), getNode(v));
    }
    
    public boolean adjacent(Node u, Node v) {
        return u != null && v != null && !getEdges(u, v).isEmpty();
    }
    
    public boolean adjacent(int fromId, int toId) {
        return adjacent(getNode(fromId), getNode(toId));
    }
    
    public int inDegree(Node node) {
        return getInEdges(node).size();
    }
    
    public int inDegree(int id) {
        return inDegree(getNode(id));
    }
   
    public int outDegree(Node node) {
        return getOutEdges(node).size();
    }
    
    public int outDegree(int id) {
        return outDegree(getNode(id));
    }
    
    public int degree(Node node) {
        return inDegree(node) + outDegree(node);
    }
    
    public int degree(int id) {
        return degree(getNode(id));
    }
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
    private void addEdgeInternal(Node from, Node to, Integer weight) {
        Node src = ensureNode(from);
        Node dst = ensureNode(to);
        adjEdList.get(src).add(new Edge(src, dst, weight));
    }
    
    public void addEdge(Node from, Node to) {
        addEdgeInternal(from, to, null);
    }
   
    public void addEdge(int fromId, int toId) {
        addEdgeInternal(getOrCreateNode(fromId), getOrCreateNode(toId), null);
    }
    
    public void addEdge(Node from, Node to, int weight) {
        addEdgeInternal(from, to, weight);
    }
   
    public void addEdge(int fromId, int toId, int weight) {
        addEdgeInternal(getOrCreateNode(fromId), getOrCreateNode(toId), weight);
    }
    
    public void addEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge to add must not be null.");
        }
        if (edge.from().getGraph() != this || edge.to().getGraph() != this) {
            throw new IllegalArgumentException("Edge endpoints must belong to this graph.");
        }
        addEdgeInternal(edge.from(), edge.to(), edge.getWeight());
    }
    
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
    
    public boolean removeEdge(int fromId, int toId) {
        return removeEdge(getNode(fromId), getNode(toId));
    }
    
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
    
    public boolean removeEdge(int fromId, int toId, int weight) {
        return removeEdge(getNode(fromId), getNode(toId), weight);
    }
    
    public boolean removeEdge(Edge edge) {
        if (edge == null || !holdsNode(edge.from()) || !holdsNode(edge.to())) return false;
        List<Edge> outgoing = adjEdList.get(nodesById.get(edge.from().getId()));
        if (outgoing == null) return false;
        return outgoing.remove(edge);
    }
    
    public boolean existsEdge(Node u, Node v) {
        return u != null && v != null && holdsNode(u) && holdsNode(v) && !getEdges(u, v).isEmpty();
    }
    
    public boolean existsEdge(int u, int v) {
        return existsEdge(getNode(u), getNode(v));
    }
    
    public boolean existsEdge(Edge e) {
        if (e == null || !holdsNode(e.from()) || !holdsNode(e.to())) return false;
        for (Edge edge : getEdges(e.from(), e.to())) {
            if (edge.equals(e)) return true;
        }
        return false;
    }
    
    public boolean isMultiEdge(Node u, Node v) {
        return u != null && v != null && holdsNode(u) && holdsNode(v) && getEdges(u, v).size() > 1;
    }
    
    public boolean isMultiEdge(int u, int v) {
        return isMultiEdge(getNode(u), getNode(v));
    }
    
    public boolean isMultiEdge(Edge edge) {
        return edge != null && isMultiEdge(edge.from(), edge.to());
    }
    
    public boolean isEmpty() {
        return nodesById.isEmpty();
    }
    
    public void clear() {
        adjEdList.clear();
        nodesById.clear();
    }
    
    Node getOrCreateNode(int id) {
        Node existing = nodesById.get(id);
        if (existing != null) return existing;
        Node created = new Node(id, this);
        nodesById.put(id, created);
        adjEdList.put(created, new ArrayList<>());
        return created;
    }
    
    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        for (List<Edge> outgoing : adjEdList.values()) {
            result.addAll(outgoing);
        }
        Collections.sort(result);
        return result;
    }
    
    public List<Edge> getEdges() {
        return getAllEdges();
    }
    
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
    
    public boolean isSimpleGraph() {
        return !hasSelfLoops() && !isMultiGraph();
    }
    
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
    
    public List<Node> getDFS(Node u) {
        if (!holdsNode(u)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        dfsHelper(u, visited, result);
        return result;
    }
    private void dfsHelper(Node current, Set<Node> visited, List<Node> result) {
        visited.add(current);
        result.add(current);
        for (Node succ : getSuccessors(current)) {
            if (!visited.contains(succ)) {
                dfsHelper(succ, visited, result);
            }
        }
    }
    
    public List<Node> getDFS(int id) {
        return getDFS(getNode(id));
    }
    
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
    
    public List<Node> getBFS(Node u) {
        if (!holdsNode(u)) return new ArrayList<>();
        List<Node> result = new ArrayList<>();
        Set<Node> visited = new LinkedHashSet<>();
        bfsHelper(u, visited, result);
        return result;
    }
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
    
    public List<Node> getBFS(int id) {
        return getBFS(getNode(id));
    }
    
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
    
    public List<Node> getDFSWithVisitInfo(int id, Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        return getDFSWithVisitInfo(getNode(id), nodeVisit, edgeVisit);
    }
    
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
    
    public void toDotFile(String fileName) {
        toDotFile(fileName, ".gv");
    }
    
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
    
    public static Graph fromDotFile(String fileName) {
        return fromDotFile(fileName, ".gv");
    }
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
}
