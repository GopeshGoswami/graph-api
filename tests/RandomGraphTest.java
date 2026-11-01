package tests;

import m1graphs2026.Graph;
import m1graphs2026.Node;
import m1graphs2026.UndirectedGraph;

/**
 * Unit tests for Random Graph generation (Section 8) and Node(Graph) auto-id constructor.
 */
public final class RandomGraphTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private RandomGraphTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Random Graph & Node Helper Tests ===");

        testNodeAutoIdConstructor();
        testHoldsNodeOverload();
        testRandomDirectedGraph();
        testRandomDAG();
        testRandomConnectedDirectedGraph();
        testRandomUndirectedGraph();
        testRandomConnectedUndirectedGraph();
        testErrorHandling();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL RANDOM GRAPH TESTS COMPLIANT AND VERIFIED!");
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

    public static void testNodeAutoIdConstructor() {
        System.out.println("\nTesting Node(Graph) auto ID constructor:");
        Graph g = new Graph();
        Node n1 = new Node(g);
        assertEquals(1, n1.getId(), "First auto node has ID 1");
        g.addNode(n1);

        Node n2 = new Node(g);
        assertEquals(2, n2.getId(), "Second auto node has ID 2");
        g.addNode(n2);

        g.addNode(10);
        Node n11 = new Node(g);
        assertEquals(11, n11.getId(), "Next auto node has ID 11 (largestNodeId + 1)");
    }

    public static void testHoldsNodeOverload() {
        System.out.println("\nTesting holdsNode(int id) overload:");
        Graph g = new Graph();
        g.addNode(5);
        assertTrue(g.holdsNode(5), "holdsNode(5) is true");
        assertTrue(!g.holdsNode(99), "holdsNode(99) is false");
    }

    public static void testRandomDirectedGraph() {
        System.out.println("\nTesting Graph.random(nbNodes, edgeProbability):");
        Graph g = Graph.random(10, 0.3);
        assertEquals(10, g.nbNodes(), "Random graph has 10 nodes");
        assertTrue(!g.hasSelfLoops(), "Random graph has no self-loops");
    }

    public static void testRandomDAG() {
        System.out.println("\nTesting Graph.randomDAG(nbNodes, edgeProbability):");
        Graph dag = Graph.randomDAG(15, 0.4);
        assertEquals(15, dag.nbNodes(), "DAG has 15 nodes");
        assertTrue(!dag.hasSelfLoops(), "DAG has no self-loops");
        for (Node u : dag.getAllNodes()) {
            for (Node v : dag.getSuccessors(u)) {
                assertTrue(u.getId() < v.getId(), "All DAG edges point from smaller ID to larger ID: " + u + " -> " + v);
            }
        }
    }

    public static void testRandomConnectedDirectedGraph() {
        System.out.println("\nTesting Graph.randomConnected(nbNodes, extraEdges):");
        Graph g = Graph.randomConnected(8, 4);
        assertEquals(8, g.nbNodes(), "Connected graph has 8 nodes");
        assertTrue(g.nbEdges() >= 8, "Connected graph has at least 8 edges (spanning cycle)");
        assertEquals(8, g.getDFS(1).size(), "DFS from node 1 visits all 8 nodes (strongly connected)");
    }

    public static void testRandomUndirectedGraph() {
        System.out.println("\nTesting UndirectedGraph.random(nbNodes, edgeProbability):");
        UndirectedGraph g = UndirectedGraph.random(10, 0.3);
        assertEquals(10, g.nbNodes(), "Random undirected graph has 10 nodes");
        assertTrue(!g.hasSelfLoops(), "No self loops");
    }

    public static void testRandomConnectedUndirectedGraph() {
        System.out.println("\nTesting UndirectedGraph.randomConnected(nbNodes, extraEdges):");
        UndirectedGraph g = UndirectedGraph.randomConnected(12, 5);
        assertEquals(12, g.nbNodes(), "Connected undirected graph has 12 nodes");
        assertEquals(12, g.getDFS(1).size(), "DFS from node 1 reaches all 12 nodes");
    }

    public static void testErrorHandling() {
        System.out.println("\nTesting Random Graph error handling:");
        try {
            Graph.random(-1, 0.5);
            throw new AssertionError("Should reject negative node count");
        } catch (IllegalArgumentException e) {
            assertTrue(true, "Rejects negative node count");
        }

        try {
            Graph.random(5, 1.5);
            throw new AssertionError("Should reject probability > 1");
        } catch (IllegalArgumentException e) {
            assertTrue(true, "Rejects invalid probability");
        }
    }
}
