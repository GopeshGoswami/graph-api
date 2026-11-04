package m1graphs2026;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UndirectedGraph extends Graph {

    
    public UndirectedGraph() {
        super();
    }
    
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
    
    @Override
    public void addEdge(Node from, Node to) {
        super.addEdge(from, to);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.addEdge(to, from);
        }
    }
   
    @Override
    public void addEdge(int fromId, int toId) {
        super.addEdge(fromId, toId);
        if (fromId != toId) {
            super.addEdge(toId, fromId);
        }
    }
    
    @Override
    public void addEdge(Node from, Node to, int weight) {
        super.addEdge(from, to, weight);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.addEdge(to, from, weight);
        }
    }
    
    @Override
    public void addEdge(int fromId, int toId, int weight) {
        super.addEdge(fromId, toId, weight);
        if (fromId != toId) {
            super.addEdge(toId, fromId, weight);
        }
    }
   
    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        if (edge != null && !edge.isSelfLoop()) {
            super.addEdge(edge.getSymmetric());
        }
    }
    
    @Override
    public boolean removeEdge(Node from, Node to) {
        boolean removed = super.removeEdge(from, to);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.removeEdge(to, from);
        }
        return removed;
    }
    
    @Override
    public boolean removeEdge(int fromId, int toId) {
        boolean removed = super.removeEdge(fromId, toId);
        if (fromId != toId) {
            super.removeEdge(toId, fromId);
        }
        return removed;
    }
    
    @Override
    public boolean removeEdge(Node from, Node to, int weight) {
        boolean removed = super.removeEdge(from, to, weight);
        if (from != null && to != null && from.getId() != to.getId()) {
            super.removeEdge(to, from, weight);
        }
        return removed;
    }
    
    @Override
    public boolean removeEdge(int fromId, int toId, int weight) {
        boolean removed = super.removeEdge(fromId, toId, weight);
        if (fromId != toId) {
            super.removeEdge(toId, fromId, weight);
        }
        return removed;
    }
    
    @Override
    public boolean removeEdge(Edge edge) {
        boolean removed = super.removeEdge(edge);
        if (edge != null && !edge.isSelfLoop()) {
            super.removeEdge(edge.getSymmetric());
        }
        return removed;
    }
    
    @Override
    public int nbEdges() {
        return getAllEdges().size();
    }
   
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
    
    @Override
    public List<Edge> getEdges() {
        return getAllEdges();
    }
   
    @Override
    public List<Edge> getInEdges(Node node) {
        return getOutEdges(node);
    }
    
    @Override
    public List<Edge> getInEdges(int id) {
        return getOutEdges(id);
    }
   
    @Override
    public List<Edge> getIncidentEdges(Node node) {
        return getOutEdges(node);
    }
    
    @Override
    public List<Edge> getIncidentEdges(int id) {
        return getOutEdges(id);
    }
   
    @Override
    public int inDegree(Node node) {
        return degree(node);
    }
    
    @Override
    public int inDegree(int id) {
        return degree(id);
    }
    
    @Override
    public int outDegree(Node node) {
        return degree(node);
    }
    
    @Override
    public int outDegree(int id) {
        return degree(id);
    }
    
    @Override
    public int degree(Node node) {
        if (!holdsNode(node)) return 0;
        int count = 0;
        for (Edge e : getIncidentEdges(node)) {
            count += e.isSelfLoop() ? 2 : 1;
        }
        return count;
    }
    
    @Override
    public int degree(int id) {
        return degree(getNode(id));
    }
    
    @Override
    public UndirectedGraph getReverse() {
        return copy();
    }
    
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
    
    public static UndirectedGraph fromDotFile(String fileName) {
        return fromDotFile(fileName, ".gv");
    }
    
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
}
