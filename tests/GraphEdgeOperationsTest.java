package tests;

import m1graphs2026.Edge;
import m1graphs2026.Graph;
import m1graphs2026.Node;

/**
 * Tests for Graph Edge Operations (Milestone 4).
 */
public final class GraphEdgeOperationsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private GraphEdgeOperationsTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Graph Edge Operations Tests ===");

        testExistsEdgeAndIsMultiEdge();
        testAddEdgeOverloads();
        testRemoveEdgeOverloads();
        testEdgeRetrievalOverloads();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("GRAPH EDGE OPERATIONS TESTS COMPLIANT AND VERIFIED!");
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

    public static void testExistsEdgeAndIsMultiEdge() {
        System.out.println("\nTesting existsEdge and isMultiEdge overloads (Reqs 37, 38):");
        Graph g = new Graph();
        // Graph structure:
        // 1 -> 2 (parallel edges: one unweighted, one weight=10)
        // 2 -> 3 (single unweighted)
        // 3 -> 3 (single self loop)
        g.addEdge(1, 2);
        g.addEdge(1, 2); // parallel
        g.addEdge(2, 3);
        g.addEdge(3, 3);
        g.addNode(4); // isolated node

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);
        Node n4 = g.getNode(4);

        // Req 37: existsEdge(Node, Node)
        assertTrue(g.existsEdge(n1, n2), "Req 37: existsEdge(n1, n2) is true");
        assertTrue(g.existsEdge(n2, n3), "Req 37: existsEdge(n2, n3) is true");
        assertTrue(g.existsEdge(n3, n3), "Req 37: existsEdge(n3, n3) self-loop is true");
        assertTrue(!g.existsEdge(n2, n1), "Req 37: directed existsEdge(n2, n1) is false");
        assertTrue(!g.existsEdge(n1, n4), "Req 37: existsEdge to isolated node is false");
        assertTrue(!g.existsEdge(null, n2), "Req 37: existsEdge with null source is false");
        assertTrue(!g.existsEdge(n1, null), "Req 37: existsEdge with null target is false");

        // Req 37: existsEdge(int, int)
        assertTrue(g.existsEdge(1, 2), "Req 37: existsEdge(1, 2) is true");
        assertTrue(g.existsEdge(2, 3), "Req 37: existsEdge(2, 3) is true");
        assertTrue(!g.existsEdge(2, 1), "Req 37: existsEdge(2, 1) is false");
        assertTrue(!g.existsEdge(1, 999), "Req 37: existsEdge with absent target ID is false");
        assertTrue(!g.existsEdge(888, 2), "Req 37: existsEdge with absent source ID is false");

        // Req 37: existsEdge(Edge)
        Edge e1_2 = new Edge(n1, n2);
        assertTrue(g.existsEdge(e1_2), "Req 37: existsEdge(Edge) is true for stored edge");
        Edge e2_1 = new Edge(n2, n1);
        assertTrue(!g.existsEdge(e2_1), "Req 37: existsEdge(Edge) is false for absent edge");
        assertTrue(!g.existsEdge((Edge) null), "Req 37: existsEdge(null) is false");

        // Foreign edge from different graph
        Graph foreignGraph = new Graph();
        foreignGraph.addNode(1);
        foreignGraph.addNode(2);
        Edge foreignEdge = new Edge(foreignGraph.getNode(1), foreignGraph.getNode(2));
        assertTrue(!g.existsEdge(foreignEdge), "Req 37: existsEdge on foreign edge is false");

        // Req 38: isMultiEdge(Node, Node)
        assertTrue(g.isMultiEdge(n1, n2), "Req 38: isMultiEdge(n1, n2) is true (parallel edges)");
        assertTrue(!g.isMultiEdge(n2, n3), "Req 38: isMultiEdge(n2, n3) is false (unique edge)");
        assertTrue(!g.isMultiEdge(n3, n3), "Req 38: isMultiEdge(n3, n3) is false (single self loop)");
        assertTrue(!g.isMultiEdge(n1, n4), "Req 38: isMultiEdge to isolated node is false");

        // Req 38: isMultiEdge(int, int)
        assertTrue(g.isMultiEdge(1, 2), "Req 38: isMultiEdge(1, 2) is true");
        assertTrue(!g.isMultiEdge(2, 3), "Req 38: isMultiEdge(2, 3) is false");
        assertTrue(!g.isMultiEdge(999, 1), "Req 38: isMultiEdge on absent ID is false");

        // Req 38: isMultiEdge(Edge)
        assertTrue(g.isMultiEdge(e1_2), "Req 38: isMultiEdge(e1_2) is true");
        Edge e2_3 = new Edge(n2, n3);
        assertTrue(!g.isMultiEdge(e2_3), "Req 38: isMultiEdge(e2_3) is false");
        assertTrue(!g.isMultiEdge((Edge) null), "Req 38: isMultiEdge(null) is false");
    }

    public static void testAddEdgeOverloads() {
        System.out.println("\nTesting addEdge overloads (Req 39):");
        Graph g = new Graph();

        // Overload 1: addEdge(Node, Node) where nodes are created first if not already belonging to graph
        Node a = new Node(100, "NodeHundred", g);
        Node b = new Node(200, "NodeTwoHundred", g);
        assertTrue(!g.usesNode(100), "Node 100 not yet in graph");
        g.addEdge(a, b);
        assertTrue(g.usesNode(100), "Node 100 automatically registered in graph");
        assertTrue(g.usesNode(200), "Node 200 automatically registered in graph");
        assertEquals("NodeHundred", g.getNode(100).getName(), "Node 100 name preserved");
        assertTrue(g.existsEdge(100, 200), "Edge 100->200 exists");
        assertEquals(null, g.getEdges(a, b).get(0).getWeight(), "Edge 100->200 is unweighted");

        // Overload 2: addEdge(int, int) creating endpoints if absent
        g.addEdge(300, 400);
        assertTrue(g.usesNode(300), "Node 300 created");
        assertTrue(g.usesNode(400), "Node 400 created");
        assertTrue(g.existsEdge(300, 400), "Edge 300->400 exists");

        // Overload 3: addEdge(Node, Node, int weight)
        Node c = g.getNode(100);
        Node d = g.getNode(300);
        g.addEdge(c, d, 25);
        assertTrue(g.existsEdge(100, 300), "Edge 100->300 exists");
        assertEquals(Integer.valueOf(25), g.getEdges(c, d).get(0).getWeight(), "Weight 25 preserved");

        // Overload 4: addEdge(int, int, int weight)
        g.addEdge(300, 500, -99);
        assertTrue(g.usesNode(500), "Node 500 created");
        assertTrue(g.existsEdge(300, 500), "Edge 300->500 exists");
        assertEquals(Integer.valueOf(-99), g.getEdges(g.getNode(300), g.getNode(500)).get(0).getWeight(), "Weight -99 preserved");

        // Overload 5: addEdge(Edge)
        Edge e = new Edge(g.getNode(200), g.getNode(400), 50);
        g.addEdge(e);
        assertTrue(g.existsEdge(200, 400), "Edge 200->400 added via addEdge(Edge)");
        assertEquals(Integer.valueOf(50), g.getEdges(g.getNode(200), g.getNode(400)).get(0).getWeight(), "Weight 50 preserved");

        // Error cases
        boolean threwNullFrom = false;
        try {
            g.addEdge((Node) null, b);
        } catch (IllegalArgumentException ex) {
            threwNullFrom = true;
        }
        assertTrue(threwNullFrom, "addEdge rejects null from");

        boolean threwNullEdge = false;
        try {
            g.addEdge((Edge) null);
        } catch (IllegalArgumentException ex) {
            threwNullEdge = true;
        }
        assertTrue(threwNullEdge, "addEdge rejects null Edge");

        Graph foreignGraph = new Graph();
        foreignGraph.addNode(1);
        boolean threwForeign = false;
        try {
            g.addEdge(foreignGraph.getNode(1), b);
        } catch (IllegalArgumentException ex) {
            threwForeign = true;
        }
        assertTrue(threwForeign, "addEdge rejects foreign node");
    }

    public static void testRemoveEdgeOverloads() {
        System.out.println("\nTesting removeEdge overloads (Req 40):");
        Graph g = new Graph();
        // Setup graph:
        // 1 -> 2 (parallel: unweighted, weight=10, weight=20)
        // 2 -> 3 (single unweighted)
        // 3 -> 3 (self-loop, weight=5)
        // 4 (isolated)
        g.addEdge(1, 2);
        g.addEdge(1, 2, 10);
        g.addEdge(1, 2, 20);
        g.addEdge(2, 3);
        g.addEdge(3, 3, 5);
        g.addNode(4);

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);
        Node n4 = g.getNode(4);

        // --- Overload 1: removeEdge(Node, Node) ---
        assertEquals(3, g.getEdges(n1, n2).size(), "Initial edges between 1 and 2 is 3");
        boolean removed1 = g.removeEdge(n1, n2);
        assertTrue(removed1, "removeEdge(n1, n2) succeeded");
        assertEquals(2, g.getEdges(n1, n2).size(), "Edges between 1 and 2 decreased to 2");
        assertTrue(g.existsEdge(n1, n2), "existsEdge(n1, n2) still true");
        assertTrue(g.isMultiEdge(n1, n2), "isMultiEdge(n1, n2) still true (2 edges left)");

        // Non-existent edge removal
        assertTrue(!g.removeEdge(n4, n1), "removeEdge(n4, n1) returns false (no edge)");
        assertTrue(!g.removeEdge((Node) null, n1), "removeEdge(null, n1) returns false");
        assertTrue(!g.removeEdge(n1, (Node) null), "removeEdge(n1, null) returns false");

        // Foreign node removal attempt
        Graph foreignGraph = new Graph();
        foreignGraph.addNode(999);
        assertTrue(!g.removeEdge(foreignGraph.getNode(999), n2), "removeEdge with foreign node returns false");

        // --- Overload 2: removeEdge(int, int) ---
        assertTrue(g.existsEdge(2, 3), "Edge 2->3 exists before removal");
        boolean removed2 = g.removeEdge(2, 3);
        assertTrue(removed2, "removeEdge(2, 3) succeeded");
        assertTrue(!g.existsEdge(2, 3), "Edge 2->3 no longer exists");
        assertTrue(!g.removeEdge(2, 3), "removeEdge(2, 3) again returns false");
        assertTrue(!g.removeEdge(99, 100), "removeEdge on absent node IDs returns false");

        // --- Overload 3: removeEdge(Node, Node, int weight) ---
        // Currently remaining between 1 and 2: weight 10, weight 20
        assertTrue(!g.removeEdge(n1, n2, 999), "removeEdge with non-matching weight 999 returns false");
        boolean removed3 = g.removeEdge(n1, n2, 10);
        assertTrue(removed3, "removeEdge(n1, n2, 10) succeeded");
        assertEquals(1, g.getEdges(n1, n2).size(), "Edges between 1 and 2 decreased to 1");
        assertTrue(!g.isMultiEdge(n1, n2), "isMultiEdge(n1, n2) is now false (only 1 edge left)");
        assertTrue(!g.removeEdge(n1, n2, 10), "removeEdge(n1, n2, 10) again returns false");

        // --- Overload 4: removeEdge(int, int, int weight) ---
        assertTrue(g.existsEdge(3, 3), "Self-loop 3->3 exists before removal");
        assertTrue(!g.removeEdge(3, 3, 999), "removeEdge(3, 3, 999) returns false (wrong weight)");
        boolean removed4 = g.removeEdge(3, 3, 5);
        assertTrue(removed4, "removeEdge(3, 3, 5) succeeded");
        assertTrue(!g.existsEdge(3, 3), "Self-loop 3->3 no longer exists");
        assertTrue(!g.removeEdge(88, 3, 5), "removeEdge with absent source ID returns false");
        assertTrue(!g.removeEdge(3, 88, 5), "removeEdge with absent target ID returns false");

        // --- Overload 5: removeEdge(Edge) ---
        // Add edge 1 -> 4 with weight 77
        g.addEdge(1, 4, 77);
        assertTrue(g.existsEdge(1, 4), "Edge 1->4 exists");
        Edge edgeToRemove = new Edge(n1, n4, 77);
        boolean removed5 = g.removeEdge(edgeToRemove);
        assertTrue(removed5, "removeEdge(Edge) succeeded");
        assertTrue(!g.existsEdge(1, 4), "Edge 1->4 no longer exists");
        assertTrue(!g.removeEdge(edgeToRemove), "removeEdge(Edge) again returns false");
        assertTrue(!g.removeEdge((Edge) null), "removeEdge(null Edge) returns false");

        Edge foreignEdge = new Edge(foreignGraph.getNode(999), foreignGraph.getNode(999));
        assertTrue(!g.removeEdge(foreignEdge), "removeEdge with foreign Edge returns false");
    }

    public static void testEdgeRetrievalOverloads() {
        System.out.println("\nTesting edge retrieval overloads (Reqs 41-45):");
        Graph g = new Graph();
        // Graph structure:
        // 1 -> 2 (unweighted)
        // 1 -> 2 (weight=10)
        // 1 -> 3 (weight=-5)
        // 2 -> 3 (unweighted)
        // 3 -> 3 (self loop, weight=0)
        // 4 (isolated)
        g.addEdge(1, 2);
        g.addEdge(1, 2, 10);
        g.addEdge(1, 3, -5);
        g.addEdge(2, 3);
        g.addEdge(3, 3, 0);
        g.addNode(4);

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);
        Node n4 = g.getNode(4);

        // --- Req 41: getOutEdges(Node) and getOutEdges(int) ---
        assertEquals(3, g.getOutEdges(n1).size(), "Node 1 has 3 out-edges");
        assertEquals(g.getOutEdges(n1), g.getOutEdges(1), "getOutEdges(1) matches getOutEdges(n1)");
        assertEquals(1, g.getOutEdges(2).size(), "Node 2 has 1 out-edge");
        assertEquals(g.getOutEdges(n2), g.getOutEdges(2), "getOutEdges(2) matches getOutEdges(n2)");
        assertEquals(1, g.getOutEdges(3).size(), "Node 3 has 1 out-edge (self-loop)");
        assertEquals(0, g.getOutEdges(4).size(), "Isolated node 4 has 0 out-edges");
        assertEquals(0, g.getOutEdges(999).size(), "Absent node ID 999 has 0 out-edges");
        assertEquals(0, g.getOutEdges((Node) null).size(), "Null node has 0 out-edges");

        // --- Req 42: getInEdges(Node) and getInEdges(int) ---
        assertEquals(0, g.getInEdges(n1).size(), "Node 1 has 0 in-edges");
        assertEquals(g.getInEdges(n1), g.getInEdges(1), "getInEdges(1) matches getInEdges(n1)");
        assertEquals(2, g.getInEdges(2).size(), "Node 2 has 2 in-edges (parallel from 1)");
        assertEquals(g.getInEdges(n2), g.getInEdges(2), "getInEdges(2) matches getInEdges(n2)");
        assertEquals(3, g.getInEdges(3).size(), "Node 3 has 3 in-edges (from 1, from 2, self-loop)");
        assertEquals(g.getInEdges(n3), g.getInEdges(3), "getInEdges(3) matches getInEdges(n3)");
        assertEquals(0, g.getInEdges(4).size(), "Isolated node 4 has 0 in-edges");
        assertEquals(0, g.getInEdges(999).size(), "Absent node ID 999 has 0 in-edges");
        assertEquals(0, g.getInEdges((Node) null).size(), "Null node has 0 in-edges");

        // --- Req 43: getIncidentEdges(Node) and getIncidentEdges(int) ---
        // Self loop in node 3 must be counted only once in incident edges
        assertEquals(3, g.getIncidentEdges(1).size(), "Node 1 incident edges count is 3");
        assertEquals(g.getIncidentEdges(n1), g.getIncidentEdges(1), "getIncidentEdges(1) matches getIncidentEdges(n1)");
        assertEquals(3, g.getIncidentEdges(2).size(), "Node 2 incident edges count is 3 (2 in + 1 out)");
        assertEquals(g.getIncidentEdges(n2), g.getIncidentEdges(2), "getIncidentEdges(2) matches getIncidentEdges(n2)");
        // Node 3: out = { (3,3,0) }, in = { (1,3,-5), (2,3,null), (3,3,0) }. Incident union has 3 edges:
        assertEquals(3, g.getIncidentEdges(3).size(), "Node 3 incident edges count is 3 (self-loop counted once)");
        assertEquals(g.getIncidentEdges(n3), g.getIncidentEdges(3), "getIncidentEdges(3) matches getIncidentEdges(n3)");
        assertEquals(0, g.getIncidentEdges(4).size(), "Isolated node 4 has 0 incident edges");
        assertEquals(0, g.getIncidentEdges(999).size(), "Absent node ID 999 has 0 incident edges");
        assertEquals(0, g.getIncidentEdges((Node) null).size(), "Null node has 0 incident edges");

        // --- Req 44: getEdges(Node, Node) and getEdges(int, int) ---
        assertEquals(2, g.getEdges(1, 2).size(), "Edges between 1 and 2 is 2 (parallel)");
        assertEquals(g.getEdges(n1, n2), g.getEdges(1, 2), "getEdges(1, 2) matches getEdges(n1, n2)");
        assertEquals(1, g.getEdges(1, 3).size(), "Edges between 1 and 3 is 1");
        assertEquals(g.getEdges(n1, n3), g.getEdges(1, 3), "getEdges(1, 3) matches getEdges(n1, n3)");
        assertEquals(0, g.getEdges(3, 1).size(), "Edges between 3 and 1 is 0 (directed)");
        assertEquals(g.getEdges(n3, n1), g.getEdges(3, 1), "getEdges(3, 1) matches getEdges(n3, n1)");
        assertEquals(0, g.getEdges(1, 4).size(), "Edges to isolated node is 0");
        assertEquals(0, g.getEdges(1, 999).size(), "Edges to absent ID is 0");
        assertEquals(0, g.getEdges(999, 1).size(), "Edges from absent ID is 0");

        // --- Req 45: getAllEdges() and getEdges() alias ---
        assertEquals(5, g.getAllEdges().size(), "Total edges in graph is 5");
        assertEquals(g.getAllEdges(), g.getEdges(), "getEdges() returns exact same list as getAllEdges()");

        // Check sorted natural order: (1,2,null), (1,2,10), (1,3,-5), (2,3,null), (3,3,0)
        assertEquals("1->2", g.getAllEdges().get(0).toString(), "First edge is 1->2");
        assertEquals("1->2(10)", g.getAllEdges().get(1).toString(), "Second edge is 1->2(10)");
        assertEquals("1->3(-5)", g.getAllEdges().get(2).toString(), "Third edge is 1->3(-5)");
        assertEquals("2->3", g.getAllEdges().get(3).toString(), "Fourth edge is 2->3");
        assertEquals("3->3(0)", g.getAllEdges().get(4).toString(), "Fifth edge is 3->3(0)");

        // --- Adjacent methods ---
        assertTrue(g.adjacent(n1, n2), "adjacent(n1, n2) is true");
        assertTrue(g.adjacent(1, 2), "adjacent(1, 2) is true");
        assertTrue(!g.adjacent(n2, n1), "adjacent(n2, n1) is false");
        assertTrue(!g.adjacent(2, 1), "adjacent(2, 1) is false");
        assertTrue(g.adjacent(3, 3), "adjacent(3, 3) is true");
        assertTrue(!g.adjacent(1, 4), "adjacent(1, 4) is false");
        assertTrue(!g.adjacent(1, 999), "adjacent(1, 999) is false");
        assertTrue(!g.adjacent(999, 1), "adjacent(999, 1) is false");
        assertTrue(!g.adjacent(null, n2), "adjacent(null, n2) is false");
        assertTrue(!g.adjacent(n1, null), "adjacent(n1, null) is false");
    }
}
