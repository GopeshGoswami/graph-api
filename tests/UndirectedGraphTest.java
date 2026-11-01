package tests;

import java.util.List;
import m1graphs2026.Edge;
import m1graphs2026.Node;
import m1graphs2026.UndirectedGraph;

/**
 * Unit tests for UndirectedGraph (Milestone 6).
 */
public final class UndirectedGraphTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private UndirectedGraphTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting UndirectedGraph Tests ===");

        testBasicBidirectionalEdge();
        testWeightedUndirectedEdges();
        testSelfLoopsAndDegrees();
        testMultigraphUndirectedEdges();
        testEdgeRemovals();
        testSymmetricAdjacencyMatrix();
        testUndirectedTransformations();
        testUndirectedDotOutput();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("UNDIRECTED GRAPH TESTS COMPLIANT AND VERIFIED!");
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

    public static void testBasicBidirectionalEdge() {
        System.out.println("\nTesting basic bidirectional edge properties:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);

        assertEquals(2, g.nbNodes(), "2 nodes registered");
        assertEquals(1, g.nbEdges(), "nbEdges is 1 for undirected edge {1, 2}");

        // existsEdge symmetry
        assertTrue(g.existsEdge(1, 2), "existsEdge(1, 2) is true");
        assertTrue(g.existsEdge(2, 1), "existsEdge(2, 1) is true symmetrically");

        // adjacent symmetry
        assertTrue(g.adjacent(1, 2), "adjacent(1, 2) is true");
        assertTrue(g.adjacent(2, 1), "adjacent(2, 1) is true symmetrically");

        // Neighbors / Successors
        List<Node> succ1 = g.getSuccessors(1);
        assertEquals(1, succ1.size(), "Node 1 has 1 neighbor");
        assertEquals(2, succ1.get(0).getId(), "Node 1's neighbor is 2");

        List<Node> succ2 = g.getSuccessors(2);
        assertEquals(1, succ2.size(), "Node 2 has 1 neighbor");
        assertEquals(1, succ2.get(0).getId(), "Node 2's neighbor is 1");

        // Incident = In = Out edges
        assertEquals(g.getOutEdges(1), g.getInEdges(1), "getOutEdges(1) == getInEdges(1)");
        assertEquals(g.getOutEdges(1), g.getIncidentEdges(1), "getOutEdges(1) == getIncidentEdges(1)");
        assertEquals(g.getOutEdges(2), g.getInEdges(2), "getOutEdges(2) == getInEdges(2)");
        assertEquals(g.getOutEdges(2), g.getIncidentEdges(2), "getOutEdges(2) == getIncidentEdges(2)");

        // Degree equivalence
        assertEquals(1, g.inDegree(1), "inDegree(1) is 1");
        assertEquals(1, g.outDegree(1), "outDegree(1) is 1");
        assertEquals(1, g.degree(1), "degree(1) is 1");

        // getAllEdges normalized
        List<Edge> edges = g.getAllEdges();
        assertEquals(1, edges.size(), "getAllEdges has size 1");
        assertEquals("1->2", edges.get(0).toString(), "Edge normalized as 1->2");
    }

    public static void testWeightedUndirectedEdges() {
        System.out.println("\nTesting weighted undirected edges:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(10, 20, 77);

        assertEquals(1, g.nbEdges(), "nbEdges is 1 for weighted edge");
        assertEquals(Integer.valueOf(77), g.getEdges(10, 20).get(0).getWeight(), "Weight from 10 to 20 is 77");
        assertEquals(Integer.valueOf(77), g.getEdges(20, 10).get(0).getWeight(), "Weight from 20 to 10 is 77");
    }

    public static void testSelfLoopsAndDegrees() {
        System.out.println("\nTesting self-loops and degree counting:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 1); // self-loop
        g.addEdge(1, 2);

        assertEquals(2, g.nbNodes(), "2 nodes");
        assertEquals(2, g.nbEdges(), "nbEdges is 2 (1 self-loop + 1 edge)");
        assertTrue(g.hasSelfLoops(), "hasSelfLoops is true");

        // Node 1 incident edges: (1, 1) [counts 2 in undirected graph] and (1, 2) [counts 1]
        assertEquals(3, g.degree(1), "degree(1) is 3 (self-loop counts twice)");
        assertEquals(3, g.inDegree(1), "inDegree(1) is 3");
        assertEquals(3, g.outDegree(1), "outDegree(1) is 3");

        // Node 2 incident edges: (2, 1)
        assertEquals(1, g.degree(2), "degree(2) is 1");

        // getAllEdges returns both
        assertEquals(2, g.getAllEdges().size(), "getAllEdges returns 2 edges");
        assertEquals("1->1", g.getAllEdges().get(0).toString(), "First edge is self-loop 1->1");
        assertEquals("1->2", g.getAllEdges().get(1).toString(), "Second edge is 1->2");
    }

    public static void testMultigraphUndirectedEdges() {
        System.out.println("\nTesting multigraph undirected edges:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(1, 2, 10); // parallel weighted edge

        assertEquals(2, g.nbEdges(), "nbEdges is 2 for parallel edges");
        assertTrue(g.isMultiGraph(), "isMultiGraph is true");
        assertTrue(g.isMultiEdge(1, 2), "isMultiEdge(1, 2) is true");
        assertTrue(g.isMultiEdge(2, 1), "isMultiEdge(2, 1) is true");

        assertEquals(2, g.getEdges(1, 2).size(), "2 edges from 1 to 2");
        assertEquals(2, g.getEdges(2, 1).size(), "2 edges from 2 to 1");

        assertEquals(2, g.degree(1), "degree(1) is 2 (two parallel edges)");
        assertEquals(2, g.degree(2), "degree(2) is 2 (two parallel edges)");
    }

    public static void testEdgeRemovals() {
        System.out.println("\nTesting edge removals in UndirectedGraph:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(1, 2, 10);
        g.addEdge(2, 3);
        g.addEdge(3, 3);

        assertEquals(4, g.nbEdges(), "Initial edge count is 4");

        // Remove weighted edge (1, 2, 10)
        boolean removedWeight = g.removeEdge(1, 2, 10);
        assertTrue(removedWeight, "removeEdge(1, 2, 10) succeeded");
        assertEquals(3, g.nbEdges(), "Edge count decreased to 3");
        assertEquals(1, g.getEdges(1, 2).size(), "1 edge left from 1 to 2");
        assertEquals(1, g.getEdges(2, 1).size(), "1 edge left from 2 to 1");
        assertTrue(!g.isMultiGraph(), "isMultiGraph is now false");

        // Remove self-loop (3, 3)
        boolean removedLoop = g.removeEdge(3, 3);
        assertTrue(removedLoop, "removeEdge(3, 3) succeeded");
        assertEquals(2, g.nbEdges(), "Edge count decreased to 2");
        assertTrue(!g.hasSelfLoops(), "hasSelfLoops is now false");

        // Remove edge (2, 3) by Node reference
        boolean removed2_3 = g.removeEdge(g.getNode(2), g.getNode(3));
        assertTrue(removed2_3, "removeEdge(Node 2, Node 3) succeeded");
        assertEquals(1, g.nbEdges(), "Edge count decreased to 1");
        assertTrue(!g.existsEdge(2, 3), "Edge 2-3 no longer exists");
        assertTrue(!g.existsEdge(3, 2), "Edge 3-2 no longer exists");
    }

    public static void testSymmetricAdjacencyMatrix() {
        System.out.println("\nTesting symmetric adjacency matrix:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.addEdge(3, 3); // self-loop

        int[][] matrix = g.toAdjMatrix();
        assertEquals(3, matrix.length, "Matrix has 3 rows");
        assertEquals(3, matrix[0].length, "Matrix has 3 columns");

        // Verify symmetry: matrix[i][j] == matrix[j][i]
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(matrix[i][j], matrix[j][i],
                        "Matrix is symmetric at [" + i + "][" + j + "] == [" + j + "][" + i + "]");
            }
        }

        // Verify values
        assertEquals(0, matrix[0][0], "matrix[0][0] is 0");
        assertEquals(1, matrix[0][1], "matrix[0][1] is 1");
        assertEquals(1, matrix[1][0], "matrix[1][0] is 1");
        assertEquals(1, matrix[2][2], "matrix[2][2] (self-loop on 3) is 1");
    }

    public static void testUndirectedTransformations() {
        System.out.println("\nTesting UndirectedGraph transformations:");
        UndirectedGraph g = new UndirectedGraph();
        g.addNode(1, "One");
        g.addNode(2, "Two");
        g.addEdge(1, 2);
        g.addEdge(1, 2, 50); // parallel
        g.addEdge(2, 2); // self-loop

        // copy
        UndirectedGraph copy = g.copy();
        assertEquals(g.nbNodes(), copy.nbNodes(), "copy preserves node count");
        assertEquals(g.nbEdges(), copy.nbEdges(), "copy preserves edge count");
        assertEquals("One", copy.getNode(1).getName(), "copy preserves node name 'One'");
        assertEquals(g.getAllEdges().toString(), copy.getAllEdges().toString(), "copy preserves edges");

        // getReverse
        UndirectedGraph rev = g.getReverse();
        assertEquals(g.getAllEdges().toString(), rev.getAllEdges().toString(),
                "getReverse of undirected graph produces equivalent edges");

        // toSimpleGraph
        UndirectedGraph simple = g.toSimpleGraph();
        assertEquals(2, simple.nbNodes(), "simple graph has 2 nodes");
        assertEquals(1, simple.nbEdges(), "simple graph collapsed parallel edges and removed self-loop (1 edge)");
        assertTrue(simple.isSimpleGraph(), "simple graph is simple");
        assertTrue(!simple.hasSelfLoops(), "simple graph has no self-loops");
        assertTrue(!simple.isMultiGraph(), "simple graph is not a multigraph");
    }

    public static void testUndirectedDotOutput() {
        System.out.println("\nTesting UndirectedGraph DOT output:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(2, 3, 10);

        String dot = g.toDotString();
        assertTrue(dot.startsWith("graph {\n\trankdir=LR"), "DOT begins with 'graph {'");
        assertTrue(dot.contains("\t1 -- 2\n"), "DOT contains unweighted '1 -- 2'");
        assertTrue(dot.contains("\t2 -- 3 [label=10, len=10]\n"), "DOT contains weighted '2 -- 3 [label=10, len=10]'");
        assertTrue(!dot.contains("->"), "DOT does not contain directed '->'");
    }
}
