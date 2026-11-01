package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import m1graphs2026.Edge;
import m1graphs2026.EdgeVisitType;
import m1graphs2026.Graph;
import m1graphs2026.Node;
import m1graphs2026.NodeColor;
import m1graphs2026.NodeVisitInfo;
import m1graphs2026.UndirectedGraph;

/**
 * Unit tests for Graph Traversals (Milestone 7): getDFS (Reqs 55, 56) and getBFS (Reqs 57, 58).
 */
public final class GraphTraversalTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private GraphTraversalTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Graph Traversal Tests ===");

        testEmptyGraphTraversals();
        testFig1DFSTraversal();
        testFig1BFSTraversal();
        testPartialReachability();
        testCyclesAndSelfLoops();
        testUndirectedTraversals();
        testBoundaryCases();
        testNodeColorAndEdgeVisitTypeEnums();
        testNodeVisitInfoClass();
        testEnrichedDFSFig1();
        testForwardEdgesAndMultigraphs();
        testSelfLoopEdgeClassification();
        testEnrichedDFSPartialReachability();
        testEnrichedDFSOverloadsAndErrorHandling();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("GRAPH TRAVERSAL TESTS COMPLIANT AND VERIFIED!");
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
            throw new AssertionError("Test failed: " + testName);
        }
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        testsRun++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName + " (expected: " + expected + ", actual: " + actual + ")");
            throw new AssertionError("Test failed: " + testName + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static List<Integer> toIdList(List<Node> nodes) {
        List<Integer> ids = new ArrayList<>();
        for (Node n : nodes) {
            ids.add(n.getId());
        }
        return ids;
    }

    public static void testEmptyGraphTraversals() {
        System.out.println("\nTesting empty graph traversals:");
        Graph empty = new Graph();

        assertEquals(0, empty.getDFS().size(), "Empty graph getDFS() is empty");
        assertEquals(0, empty.getBFS().size(), "Empty graph getBFS() is empty");
        assertEquals(0, empty.getDFS(1).size(), "Empty graph getDFS(1) is empty");
        assertEquals(0, empty.getBFS(1).size(), "Empty graph getBFS(1) is empty");
    }

    public static void testFig1DFSTraversal() {
        System.out.println("\nTesting Fig 1 DFS Traversal (PDF Section 5.4, Section 5.6):");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        // Expected deterministic DFS order starting from lowest ID node (1):
        // 1 -> 2
        // 1 -> 4 -> 3 -> 6 -> 7
        // 4 -> 5
        // 4 -> 8
        // Result: [1, 2, 4, 3, 6, 7, 5, 8]
        List<Integer> expectedDFS = List.of(1, 2, 4, 3, 6, 7, 5, 8);

        List<Node> dfsDefault = fig1.getDFS();
        assertEquals(expectedDFS, toIdList(dfsDefault), "getDFS() default matches expected DFS order");

        List<Node> dfsFromId = fig1.getDFS(1);
        assertEquals(expectedDFS, toIdList(dfsFromId), "getDFS(1) matches expected DFS order");

        List<Node> dfsFromNode = fig1.getDFS(fig1.getNode(1));
        assertEquals(expectedDFS, toIdList(dfsFromNode), "getDFS(Node 1) matches expected DFS order");
    }

    public static void testFig1BFSTraversal() {
        System.out.println("\nTesting Fig 1 BFS Traversal (PDF Section 5.4, Section 5.6):");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        // Expected deterministic BFS order starting from lowest ID node (1):
        // Level 0: 1
        // Level 1: 2, 4
        // Level 2: 3, 5, 8
        // Level 3: 6, 7
        // Result: [1, 2, 4, 3, 5, 8, 6, 7]
        List<Integer> expectedBFS = List.of(1, 2, 4, 3, 5, 8, 6, 7);

        List<Node> bfsDefault = fig1.getBFS();
        assertEquals(expectedBFS, toIdList(bfsDefault), "getBFS() default matches expected BFS order");

        List<Node> bfsFromId = fig1.getBFS(1);
        assertEquals(expectedBFS, toIdList(bfsFromId), "getBFS(1) matches expected BFS order");

        List<Node> bfsFromNode = fig1.getBFS(fig1.getNode(1));
        assertEquals(expectedBFS, toIdList(bfsFromNode), "getBFS(Node 1) matches expected BFS order");
    }

    public static void testPartialReachability() {
        System.out.println("\nTesting partial reachability from specific nodes in Fig 1:");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        // From node 4:
        // Reachable: 4, 2, 3, 5, 8, 6, 7 (node 1 cannot be reached)
        List<Integer> dfsFrom4 = toIdList(fig1.getDFS(4));
        assertEquals(List.of(4, 2, 3, 6, 7, 5, 8), dfsFrom4, "DFS from 4 explores reachable component");
        assertTrue(!dfsFrom4.contains(1), "DFS from 4 does not contain unreachable node 1");

        List<Integer> bfsFrom4 = toIdList(fig1.getBFS(4));
        assertEquals(List.of(4, 2, 3, 5, 8, 6, 7), bfsFrom4, "BFS from 4 explores level-order reachable component");
        assertTrue(!bfsFrom4.contains(1), "BFS from 4 does not contain unreachable node 1");

        // From node 2 (sink):
        List<Integer> dfsFrom2 = toIdList(fig1.getDFS(2));
        assertEquals(List.of(2), dfsFrom2, "DFS from sink 2 visits only node 2");

        List<Integer> bfsFrom2 = toIdList(fig1.getBFS(2));
        assertEquals(List.of(2), bfsFrom2, "BFS from sink 2 visits only node 2");
    }

    public static void testCyclesAndSelfLoops() {
        System.out.println("\nTesting cycles and self-loops in traversals:");
        Graph g = new Graph();
        // 1 -> 2 -> 3 -> 1 (cycle)
        // 2 -> 2 (self-loop)
        g.addEdge(1, 2);
        g.addEdge(2, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        List<Integer> dfs = toIdList(g.getDFS(1));
        assertEquals(List.of(1, 2, 3), dfs, "DFS on cycle and self-loop visits each node exactly once");

        List<Integer> bfs = toIdList(g.getBFS(1));
        assertEquals(List.of(1, 2, 3), bfs, "BFS on cycle and self-loop visits each node exactly once");
    }

    public static void testUndirectedTraversals() {
        System.out.println("\nTesting traversals on UndirectedGraph:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(1, 4);

        // From node 1, visits all 4 nodes
        List<Integer> dfs = toIdList(g.getDFS(1));
        assertEquals(4, dfs.size(), "Undirected DFS visits all 4 nodes");
        assertEquals(Integer.valueOf(1), dfs.get(0), "DFS starts at 1");

        List<Integer> bfs = toIdList(g.getBFS(1));
        assertEquals(List.of(1, 2, 4, 3), bfs, "Undirected BFS explores neighbors 2 and 4, then 3");
    }

    public static void testBoundaryCases() {
        System.out.println("\nTesting boundary cases for traversals:");
        Graph g = new Graph();
        g.addNode(1);

        assertEquals(0, g.getDFS(999).size(), "getDFS(999) with absent ID returns empty list");
        assertEquals(0, g.getBFS(999).size(), "getBFS(999) with absent ID returns empty list");
        assertEquals(0, g.getDFS((Node) null).size(), "getDFS(null) returns empty list");
        assertEquals(0, g.getBFS((Node) null).size(), "getBFS(null) returns empty list");

        Graph foreignGraph = new Graph();
        foreignGraph.addNode(1);
        assertEquals(0, g.getDFS(foreignGraph.getNode(1)).size(), "getDFS with foreign node returns empty list");
        assertEquals(0, g.getBFS(foreignGraph.getNode(1)).size(), "getBFS with foreign node returns empty list");
    }

    public static void testNodeColorAndEdgeVisitTypeEnums() {
        System.out.println("\nTesting NodeColor and EdgeVisitType enums:");
        assertEquals(3, NodeColor.values().length, "NodeColor has 3 constants");
        assertEquals(NodeColor.WHITE, NodeColor.valueOf("WHITE"), "NodeColor.WHITE exists");
        assertEquals(NodeColor.GRAY, NodeColor.valueOf("GRAY"), "NodeColor.GRAY exists");
        assertEquals(NodeColor.BLACK, NodeColor.valueOf("BLACK"), "NodeColor.BLACK exists");

        assertEquals(4, EdgeVisitType.values().length, "EdgeVisitType has 4 constants");
        assertEquals(EdgeVisitType.TREE, EdgeVisitType.valueOf("TREE"), "EdgeVisitType.TREE exists");
        assertEquals(EdgeVisitType.BACKWARD, EdgeVisitType.valueOf("BACKWARD"), "EdgeVisitType.BACKWARD exists");
        assertEquals(EdgeVisitType.FORWARD, EdgeVisitType.valueOf("FORWARD"), "EdgeVisitType.FORWARD exists");
        assertEquals(EdgeVisitType.CROSS, EdgeVisitType.valueOf("CROSS"), "EdgeVisitType.CROSS exists");
    }

    public static void testNodeVisitInfoClass() {
        System.out.println("\nTesting NodeVisitInfo class functionality:");
        NodeVisitInfo defaultInfo = new NodeVisitInfo();
        assertEquals(NodeColor.WHITE, defaultInfo.getColor(), "Default NodeVisitInfo has WHITE color");
        assertEquals(null, defaultInfo.getPredecessor(), "Default NodeVisitInfo has null predecessor");
        assertEquals(null, defaultInfo.getDiscovery(), "Default NodeVisitInfo has null discovery");
        assertEquals(null, defaultInfo.getFinished(), "Default NodeVisitInfo has null finished");

        NodeVisitInfo colorOnly = new NodeVisitInfo(NodeColor.GRAY);
        assertEquals(NodeColor.GRAY, colorOnly.getColor(), "1-arg constructor sets color");

        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);

        NodeVisitInfo fullInfo = new NodeVisitInfo(NodeColor.BLACK, n1, 3, 8);
        assertEquals(NodeColor.BLACK, fullInfo.getColor(), "Full constructor sets color");
        assertEquals(n1, fullInfo.getPredecessor(), "Full constructor sets predecessor");
        assertEquals(Integer.valueOf(3), fullInfo.getDiscovery(), "Full constructor sets discovery");
        assertEquals(Integer.valueOf(3), fullInfo.getDiscoveryTime(), "getDiscoveryTime() alias matches getDiscovery()");
        assertEquals(Integer.valueOf(8), fullInfo.getFinished(), "Full constructor sets finished");
        assertEquals(Integer.valueOf(8), fullInfo.getFinishedTime(), "getFinishedTime() alias matches getFinished()");
        assertEquals(Integer.valueOf(8), fullInfo.getFinishTime(), "getFinishTime() alias matches getFinished()");

        fullInfo.setColor(NodeColor.WHITE);
        fullInfo.setPredecessor(n2);
        fullInfo.setDiscovery(5);
        fullInfo.setFinished(12);
        assertEquals(NodeColor.WHITE, fullInfo.getColor(), "setColor updates color");
        assertEquals(n2, fullInfo.getPredecessor(), "setPredecessor updates predecessor");
        assertEquals(Integer.valueOf(5), fullInfo.getDiscovery(), "setDiscovery updates discovery");
        assertEquals(Integer.valueOf(12), fullInfo.getFinished(), "setFinished updates finished");

        fullInfo.setDiscoveryTime(6);
        fullInfo.setFinishedTime(14);
        assertEquals(Integer.valueOf(6), fullInfo.getDiscovery(), "setDiscoveryTime updates discovery");
        assertEquals(Integer.valueOf(14), fullInfo.getFinished(), "setFinishedTime updates finished");
        fullInfo.setFinishTime(16);
        assertEquals(Integer.valueOf(16), fullInfo.getFinished(), "setFinishTime updates finished");

        NodeVisitInfo infoA = new NodeVisitInfo(NodeColor.BLACK, n1, 2, 7);
        NodeVisitInfo infoB = new NodeVisitInfo(NodeColor.BLACK, n1, 2, 7);
        NodeVisitInfo infoC = new NodeVisitInfo(NodeColor.BLACK, n2, 2, 7);
        assertEquals(infoA, infoB, "NodeVisitInfo equals with identical fields");
        assertEquals(infoA.hashCode(), infoB.hashCode(), "NodeVisitInfo hashCode identical for equal objects");
        assertTrue(!infoA.equals(infoC), "NodeVisitInfo not equals with different predecessor");
        assertTrue(fullInfo.toString().contains("color=WHITE"), "NodeVisitInfo toString contains color");
    }

    public static void testEnrichedDFSFig1() {
        System.out.println("\nTesting Enriched DFS on Figure 1 (PDF Section 5.4):");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        Map<Node, NodeVisitInfo> nodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> edgeVisit = new HashMap<>();

        List<Node> traversal = fig1.getDFSWithVisitInfo(nodeVisit, edgeVisit);
        List<Integer> expectedOrder = List.of(1, 2, 4, 3, 6, 7, 5, 8);
        assertEquals(expectedOrder, toIdList(traversal), "getDFSWithVisitInfo default order matches DFS");

        // Verify all nodes are present and finished BLACK
        assertEquals(8, nodeVisit.size(), "nodeVisit contains all 8 nodes");
        for (int id = 1; id <= 8; id++) {
            Node n = fig1.getNode(id);
            NodeVisitInfo info = nodeVisit.get(n);
            assertTrue(info != null, "nodeVisit contains node " + id);
            assertEquals(NodeColor.BLACK, info.getColor(), "Node " + id + " color is BLACK");
            assertTrue(info.getDiscovery() != null, "Node " + id + " has non-null discovery timestamp");
            assertTrue(info.getFinished() != null, "Node " + id + " has non-null finished timestamp");
            assertTrue(info.getDiscovery() < info.getFinished(), "Node " + id + " discovery < finished");
        }

        // Verify exact timestamps per Section 5.4 DFS traversal on Fig 1:
        assertEquals(Integer.valueOf(1), nodeVisit.get(fig1.getNode(1)).getDiscovery(), "Node 1 discovery is 1");
        assertEquals(Integer.valueOf(16), nodeVisit.get(fig1.getNode(1)).getFinished(), "Node 1 finished is 16");

        assertEquals(Integer.valueOf(2), nodeVisit.get(fig1.getNode(2)).getDiscovery(), "Node 2 discovery is 2");
        assertEquals(Integer.valueOf(3), nodeVisit.get(fig1.getNode(2)).getFinished(), "Node 2 finished is 3");

        assertEquals(Integer.valueOf(4), nodeVisit.get(fig1.getNode(4)).getDiscovery(), "Node 4 discovery is 4");
        assertEquals(Integer.valueOf(15), nodeVisit.get(fig1.getNode(4)).getFinished(), "Node 4 finished is 15");

        assertEquals(Integer.valueOf(5), nodeVisit.get(fig1.getNode(3)).getDiscovery(), "Node 3 discovery is 5");
        assertEquals(Integer.valueOf(10), nodeVisit.get(fig1.getNode(3)).getFinished(), "Node 3 finished is 10");

        assertEquals(Integer.valueOf(6), nodeVisit.get(fig1.getNode(6)).getDiscovery(), "Node 6 discovery is 6");
        assertEquals(Integer.valueOf(9), nodeVisit.get(fig1.getNode(6)).getFinished(), "Node 6 finished is 9");

        assertEquals(Integer.valueOf(7), nodeVisit.get(fig1.getNode(7)).getDiscovery(), "Node 7 discovery is 7");
        assertEquals(Integer.valueOf(8), nodeVisit.get(fig1.getNode(7)).getFinished(), "Node 7 finished is 8");

        assertEquals(Integer.valueOf(11), nodeVisit.get(fig1.getNode(5)).getDiscovery(), "Node 5 discovery is 11");
        assertEquals(Integer.valueOf(12), nodeVisit.get(fig1.getNode(5)).getFinished(), "Node 5 finished is 12");

        assertEquals(Integer.valueOf(13), nodeVisit.get(fig1.getNode(8)).getDiscovery(), "Node 8 discovery is 13");
        assertEquals(Integer.valueOf(14), nodeVisit.get(fig1.getNode(8)).getFinished(), "Node 8 finished is 14");

        // Verify predecessors:
        assertEquals(null, nodeVisit.get(fig1.getNode(1)).getPredecessor(), "Node 1 predecessor is null (root)");
        assertEquals(fig1.getNode(1), nodeVisit.get(fig1.getNode(2)).getPredecessor(), "Node 2 predecessor is 1");
        assertEquals(fig1.getNode(1), nodeVisit.get(fig1.getNode(4)).getPredecessor(), "Node 4 predecessor is 1");
        assertEquals(fig1.getNode(4), nodeVisit.get(fig1.getNode(3)).getPredecessor(), "Node 3 predecessor is 4");
        assertEquals(fig1.getNode(3), nodeVisit.get(fig1.getNode(6)).getPredecessor(), "Node 6 predecessor is 3");
        assertEquals(fig1.getNode(6), nodeVisit.get(fig1.getNode(7)).getPredecessor(), "Node 7 predecessor is 6");
        assertEquals(fig1.getNode(4), nodeVisit.get(fig1.getNode(5)).getPredecessor(), "Node 5 predecessor is 4");
        assertEquals(fig1.getNode(4), nodeVisit.get(fig1.getNode(8)).getPredecessor(), "Node 8 predecessor is 4");

        // Verify edge classifications:
        // Tree edges: (1, 2), (1, 4), (4, 3), (3, 6), (6, 7), (4, 5), (4, 8)
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(1), fig1.getNode(2))), "Edge (1, 2) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(1), fig1.getNode(4))), "Edge (1, 4) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(4), fig1.getNode(3))), "Edge (4, 3) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(3), fig1.getNode(6))), "Edge (3, 6) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(6), fig1.getNode(7))), "Edge (6, 7) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(4), fig1.getNode(5))), "Edge (4, 5) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(fig1.getNode(4), fig1.getNode(8))), "Edge (4, 8) is TREE");

        // Backward edges (cycles): (6, 4), (7, 3)
        assertEquals(EdgeVisitType.BACKWARD, edgeVisit.get(new Edge(fig1.getNode(6), fig1.getNode(4))), "Edge (6, 4) is BACKWARD");
        assertEquals(EdgeVisitType.BACKWARD, edgeVisit.get(new Edge(fig1.getNode(7), fig1.getNode(3))), "Edge (7, 3) is BACKWARD");

        // Cross edges: (4, 2), (8, 7)
        assertEquals(EdgeVisitType.CROSS, edgeVisit.get(new Edge(fig1.getNode(4), fig1.getNode(2))), "Edge (4, 2) is CROSS");
        assertEquals(EdgeVisitType.CROSS, edgeVisit.get(new Edge(fig1.getNode(8), fig1.getNode(7))), "Edge (8, 7) is CROSS");
    }

    public static void testForwardEdgesAndMultigraphs() {
        System.out.println("\nTesting FORWARD edge classification and multigraphs:");
        Graph g = new Graph();
        // 1 -> 2, 2 -> 3, 1 -> 3
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(1, 3); // nontree edge to finished descendant: FORWARD

        Map<Node, NodeVisitInfo> nodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> edgeVisit = new HashMap<>();

        g.getDFSWithVisitInfo(1, nodeVisit, edgeVisit);

        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(g.getNode(1), g.getNode(2))), "Edge (1, 2) is TREE");
        assertEquals(EdgeVisitType.TREE, edgeVisit.get(new Edge(g.getNode(2), g.getNode(3))), "Edge (2, 3) is TREE");
        assertEquals(EdgeVisitType.FORWARD, edgeVisit.get(new Edge(g.getNode(1), g.getNode(3))), "Edge (1, 3) is FORWARD");

        // Multigraph with distinct weights:
        Graph mg = new Graph();
        mg.addEdge(1, 2, 10);
        mg.addEdge(1, 2, 20);

        Map<Node, NodeVisitInfo> mgNodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> mgEdgeVisit = new HashMap<>();
        mg.getDFSWithVisitInfo(mgNodeVisit, mgEdgeVisit);

        Edge e1 = new Edge(mg.getNode(1), mg.getNode(2), 10);
        Edge e2 = new Edge(mg.getNode(1), mg.getNode(2), 20);
        assertEquals(EdgeVisitType.TREE, mgEdgeVisit.get(e1), "First parallel edge (weight 10) is TREE");
        assertEquals(EdgeVisitType.FORWARD, mgEdgeVisit.get(e2), "Second parallel edge (weight 20) is FORWARD");
    }

    public static void testSelfLoopEdgeClassification() {
        System.out.println("\nTesting self-loop edge classification:");
        Graph g = new Graph();
        g.addEdge(1, 1);

        Map<Node, NodeVisitInfo> nodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> edgeVisit = new HashMap<>();
        g.getDFSWithVisitInfo(1, nodeVisit, edgeVisit);

        Edge loop = new Edge(g.getNode(1), g.getNode(1));
        assertEquals(EdgeVisitType.BACKWARD, edgeVisit.get(loop), "Self-loop edge (1, 1) is BACKWARD");
    }

    public static void testEnrichedDFSPartialReachability() {
        System.out.println("\nTesting Enriched DFS partial reachability:");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        Map<Node, NodeVisitInfo> nodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> edgeVisit = new HashMap<>();

        // Start DFS specifically from node 4 (which cannot reach node 1)
        List<Node> resultFrom4 = fig1.getDFSWithVisitInfo(fig1.getNode(4), nodeVisit, edgeVisit);
        assertEquals(7, resultFrom4.size(), "DFS from node 4 visits exactly 7 reachable nodes");
        assertTrue(!resultFrom4.contains(fig1.getNode(1)), "DFS result from 4 does not contain node 1");

        // Node 1 was never reached and must remain WHITE
        NodeVisitInfo n1Info = nodeVisit.get(fig1.getNode(1));
        assertEquals(NodeColor.WHITE, n1Info.getColor(), "Unreachable node 1 remains WHITE");
        assertEquals(null, n1Info.getPredecessor(), "Unreachable node 1 has null predecessor");
        assertEquals(null, n1Info.getDiscovery(), "Unreachable node 1 has null discovery");
        assertEquals(null, n1Info.getFinished(), "Unreachable node 1 has null finished");

        // Reachable nodes (e.g. 4 and 2) are BLACK
        assertEquals(NodeColor.BLACK, nodeVisit.get(fig1.getNode(4)).getColor(), "Reachable node 4 is BLACK");
        assertEquals(NodeColor.BLACK, nodeVisit.get(fig1.getNode(2)).getColor(), "Reachable node 2 is BLACK");
    }

    public static void testEnrichedDFSOverloadsAndErrorHandling() {
        System.out.println("\nTesting Enriched DFS overloads and error handling:");
        Graph g = new Graph();
        g.addEdge(1, 2);

        Map<Node, NodeVisitInfo> nodeVisit = new HashMap<>();
        Map<Edge, EdgeVisitType> edgeVisit = new HashMap<>();

        // ID overload:
        List<Node> fromId = g.getDFSWithVisitInfo(1, nodeVisit, edgeVisit);
        assertEquals(List.of(1, 2), toIdList(fromId), "getDFSWithVisitInfo(int id, ...) works correctly");

        // Null checks:
        boolean threwNodeVisitNull = false;
        try {
            g.getDFSWithVisitInfo(null, edgeVisit);
        } catch (IllegalArgumentException e) {
            threwNodeVisitNull = true;
        }
        assertTrue(threwNodeVisitNull, "Null nodeVisit throws IllegalArgumentException");

        boolean threwEdgeVisitNull = false;
        try {
            g.getDFSWithVisitInfo(nodeVisit, null);
        } catch (IllegalArgumentException e) {
            threwEdgeVisitNull = true;
        }
        assertTrue(threwEdgeVisitNull, "Null edgeVisit throws IllegalArgumentException");

        // Empty graph:
        Graph empty = new Graph();
        List<Node> emptyResult = empty.getDFSWithVisitInfo(nodeVisit, edgeVisit);
        assertEquals(0, emptyResult.size(), "Empty graph returns empty list");
        assertEquals(0, nodeVisit.size(), "Empty graph clears nodeVisit map");
        assertEquals(0, edgeVisit.size(), "Empty graph clears edgeVisit map");

        // Nonexistent start node returns empty list:
        nodeVisit.clear();
        edgeVisit.clear();
        List<Node> absentResult = g.getDFSWithVisitInfo(999, nodeVisit, edgeVisit);
        assertEquals(0, absentResult.size(), "Absent start ID returns empty list");
        assertEquals(2, nodeVisit.size(), "Absent start ID leaves all graph nodes in nodeVisit (WHITE)");
        assertEquals(NodeColor.WHITE, nodeVisit.get(g.getNode(1)).getColor(), "Node 1 is WHITE");
        assertEquals(NodeColor.WHITE, nodeVisit.get(g.getNode(2)).getColor(), "Node 2 is WHITE");
    }
}
