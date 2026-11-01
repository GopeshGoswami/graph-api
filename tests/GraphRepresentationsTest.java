package tests;

import java.util.Arrays;
import m1graphs2026.Graph;

/**
 * Unit tests for Graph representations: toSuccessorArray (Req 46) and toAdjMatrix (Req 47).
 */
public final class GraphRepresentationsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private GraphRepresentationsTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Graph Representations Tests ===");

        testEmptyGraph();
        testSingleNode();
        testFig1SuccessorArrayAndAdjMatrix();
        testMultigraphAndSelfLoops();
        testRoundTripReconstruction();
        testStructuralPredicates();
        testStructuralTransformations();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("GRAPH REPRESENTATIONS TESTS COMPLIANT AND VERIFIED!");
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

    public static void testEmptyGraph() {
        System.out.println("\nTesting empty graph representations:");
        Graph empty = new Graph();

        int[] sa = empty.toSuccessorArray();
        assertEquals(0, sa.length, "Empty graph SA has length 0");

        int[][] matrix = empty.toAdjMatrix();
        assertEquals(0, matrix.length, "Empty graph matrix has 0 rows");
    }

    public static void testSingleNode() {
        System.out.println("\nTesting single isolated node representations:");
        Graph g = new Graph();
        g.addNode(1);

        int[] sa = g.toSuccessorArray();
        assertEquals(1, sa.length, "Single node SA has length 1");
        assertEquals(0, sa[0], "Single node SA content is [0]");

        int[][] matrix = g.toAdjMatrix();
        assertEquals(1, matrix.length, "Single node matrix is 1x1");
        assertEquals(1, matrix[0].length, "Single node matrix row 0 length is 1");
        assertEquals(0, matrix[0][0], "Single node matrix entry [0][0] is 0");
    }

    public static void testFig1SuccessorArrayAndAdjMatrix() {
        System.out.println("\nTesting Fig 1 SA and Adjacency Matrix (PDF Section 4.1):");
        // PDF Section 4.1 line:
        // Graph g = new Graph(2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0);
        int[] expectedSA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(expectedSA);

        assertEquals(8, fig1.nbNodes(), "Fig 1 has 8 nodes");
        assertEquals(11, fig1.nbEdges(), "Fig 1 has 11 edges");

        // Req 46: toSuccessorArray
        int[] actualSA = fig1.toSuccessorArray();
        assertTrue(Arrays.equals(expectedSA, actualSA), "toSuccessorArray exactly matches Fig 1 SA from PDF");

        // Req 47: toAdjMatrix
        int[][] matrix = fig1.toAdjMatrix();
        assertEquals(8, matrix.length, "Matrix has 8 rows");
        assertEquals(8, matrix[0].length, "Matrix has 8 columns");

        // Verify rows:
        // Node 1 (row 0): -> 2, 4
        int[] expectedRow0 = {0, 1, 0, 1, 0, 0, 0, 0};
        assertTrue(Arrays.equals(expectedRow0, matrix[0]), "Row 0 (Node 1) matches");

        // Node 2 (row 1): no outgoing edges
        int[] expectedRow1 = {0, 0, 0, 0, 0, 0, 0, 0};
        assertTrue(Arrays.equals(expectedRow1, matrix[1]), "Row 1 (Node 2) matches");

        // Node 3 (row 2): -> 6
        int[] expectedRow2 = {0, 0, 0, 0, 0, 1, 0, 0};
        assertTrue(Arrays.equals(expectedRow2, matrix[2]), "Row 2 (Node 3) matches");

        // Node 4 (row 3): -> 2, 3, 5, 8
        int[] expectedRow3 = {0, 1, 1, 0, 1, 0, 0, 1};
        assertTrue(Arrays.equals(expectedRow3, matrix[3]), "Row 3 (Node 4) matches");

        // Node 5 (row 4): no outgoing edges
        int[] expectedRow4 = {0, 0, 0, 0, 0, 0, 0, 0};
        assertTrue(Arrays.equals(expectedRow4, matrix[4]), "Row 4 (Node 5) matches");

        // Node 6 (row 5): -> 4, 7
        int[] expectedRow5 = {0, 0, 0, 1, 0, 0, 1, 0};
        assertTrue(Arrays.equals(expectedRow5, matrix[5]), "Row 5 (Node 6) matches");

        // Node 7 (row 6): -> 3
        int[] expectedRow6 = {0, 0, 1, 0, 0, 0, 0, 0};
        assertTrue(Arrays.equals(expectedRow6, matrix[6]), "Row 6 (Node 7) matches");

        // Node 8 (row 7): -> 7
        int[] expectedRow7 = {0, 0, 0, 0, 0, 0, 1, 0};
        assertTrue(Arrays.equals(expectedRow7, matrix[7]), "Row 7 (Node 8) matches");
    }

    public static void testMultigraphAndSelfLoops() {
        System.out.println("\nTesting Multigraph and Self-Loops in SA and Adjacency Matrix:");
        Graph g = new Graph();
        // Graph structure:
        // 1 -> 1 (self loop)
        // 1 -> 2 (parallel edge 1)
        // 1 -> 2 (parallel edge 2)
        // 2 -> 3
        // 3 -> 3 (self loop 1)
        // 3 -> 3 (self loop 2)
        g.addEdge(1, 1);
        g.addEdge(1, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 3);
        g.addEdge(3, 3);

        // SA should preserve multi-edges and self-loops:
        // Node 1: 1, 2, 2, 0
        // Node 2: 3, 0
        // Node 3: 3, 3, 0
        int[] expectedSA = {1, 2, 2, 0, 3, 0, 3, 3, 0};
        int[] actualSA = g.toSuccessorArray();
        assertTrue(Arrays.equals(expectedSA, actualSA), "SA preserves multigraph multi-edges and self-loops");

        // Adjacency Matrix:
        // Multi-edges > 1, diagonal non-zero for self loops
        int[][] matrix = g.toAdjMatrix();
        assertEquals(3, matrix.length, "Matrix is 3x3");

        // Row 0 (Node 1): self-loop=1, parallel to 2=2, to 3=0
        assertEquals(1, matrix[0][0], "matrix[0][0] has self-loop count 1");
        assertEquals(2, matrix[0][1], "matrix[0][1] has parallel edge count 2");
        assertEquals(0, matrix[0][2], "matrix[0][2] is 0");

        // Row 1 (Node 2): to 3=1
        assertEquals(0, matrix[1][0], "matrix[1][0] is 0");
        assertEquals(0, matrix[1][1], "matrix[1][1] is 0");
        assertEquals(1, matrix[1][2], "matrix[1][2] is 1");

        // Row 2 (Node 3): self-loop=2
        assertEquals(0, matrix[2][0], "matrix[2][0] is 0");
        assertEquals(0, matrix[2][1], "matrix[2][1] is 0");
        assertEquals(2, matrix[2][2], "matrix[2][2] has double self-loop count 2");
    }

    public static void testRoundTripReconstruction() {
        System.out.println("\nTesting round-trip reconstruction via SA constructor:");
        int[] originalSA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph g1 = new Graph(originalSA);
        int[] sa1 = g1.toSuccessorArray();

        Graph g2 = new Graph(sa1);
        int[] sa2 = g2.toSuccessorArray();

        assertTrue(Arrays.equals(originalSA, sa2), "Round-trip SA reconstruction produces identical SA");
        assertEquals(g1.nbNodes(), g2.nbNodes(), "Round-trip preserves node count");
        assertEquals(g1.nbEdges(), g2.nbEdges(), "Round-trip preserves edge count");
        assertEquals(g1.getAllEdges().toString(), g2.getAllEdges().toString(), "Round-trip preserves exact edges");
        assertTrue(Arrays.deepEquals(g1.toAdjMatrix(), g2.toAdjMatrix()), "Round-trip preserves adjacency matrix");
    }

    public static void testStructuralPredicates() {
        System.out.println("\nTesting structural predicates (Reqs 50, 51, 52):");

        // Case 1: Empty graph
        Graph empty = new Graph();
        assertTrue(!empty.isMultiGraph(), "Req 50: Empty graph is not a multigraph");
        assertTrue(!empty.hasSelfLoops(), "Req 52: Empty graph has no self-loops");
        assertTrue(empty.isSimpleGraph(), "Req 51: Empty graph is a simple graph");

        // Case 2: Fig 1 from PDF (simple directed graph)
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);
        assertTrue(!fig1.isMultiGraph(), "Req 50: Fig 1 is not a multigraph");
        assertTrue(!fig1.hasSelfLoops(), "Req 52: Fig 1 has no self-loops");
        assertTrue(fig1.isSimpleGraph(), "Req 51: Fig 1 is a simple graph");

        // Case 3: Graph with only self-loop
        Graph selfLoopGraph = new Graph();
        selfLoopGraph.addEdge(1, 1);
        selfLoopGraph.addNode(2);
        assertTrue(!selfLoopGraph.isMultiGraph(), "Req 50: Self-loop graph is not a multigraph");
        assertTrue(selfLoopGraph.hasSelfLoops(), "Req 52: Self-loop graph hasSelfLoops is true");
        assertTrue(!selfLoopGraph.isSimpleGraph(), "Req 51: Graph with self-loop is not simple");

        // Case 4: Graph with only parallel edges (no self-loops)
        Graph multiGraph = new Graph();
        multiGraph.addEdge(1, 2);
        multiGraph.addEdge(1, 2); // parallel
        multiGraph.addEdge(2, 3);
        assertTrue(multiGraph.isMultiGraph(), "Req 50: Multi-graph with parallel edges isMultiGraph is true");
        assertTrue(!multiGraph.hasSelfLoops(), "Req 52: Multi-graph without self-loops hasSelfLoops is false");
        assertTrue(!multiGraph.isSimpleGraph(), "Req 51: Multi-graph is not simple");

        // Case 5: Graph with both parallel edges and self-loops
        Graph multiWithLoop = new Graph();
        multiWithLoop.addEdge(1, 2);
        multiWithLoop.addEdge(1, 2);
        multiWithLoop.addEdge(2, 2);
        assertTrue(multiWithLoop.isMultiGraph(), "Req 50: Multi-graph with loop isMultiGraph is true");
        assertTrue(multiWithLoop.hasSelfLoops(), "Req 52: Multi-graph with loop hasSelfLoops is true");
        assertTrue(!multiWithLoop.isSimpleGraph(), "Req 51: Multi-graph with loop is not simple");

        // Case 6: Dynamic transitions (removal of self-loop then duplicate edge)
        multiWithLoop.removeEdge(2, 2);
        assertTrue(!multiWithLoop.hasSelfLoops(), "hasSelfLoops becomes false after removing loop");
        assertTrue(multiWithLoop.isMultiGraph(), "isMultiGraph remains true with duplicate edge");
        assertTrue(!multiWithLoop.isSimpleGraph(), "isSimpleGraph remains false while parallel edge exists");

        multiWithLoop.removeEdge(1, 2);
        assertTrue(!multiWithLoop.isMultiGraph(), "isMultiGraph becomes false after removing parallel edge");
        assertTrue(multiWithLoop.isSimpleGraph(), "isSimpleGraph becomes true once all defects are removed");
    }

    public static void testStructuralTransformations() {
        System.out.println("\nTesting structural transformations (Reqs 48, 49, 53, 54):");

        // --- Req 54: copy() ---
        Graph original = new Graph();
        original.addNode(10, "Ten");
        original.addNode(20, "Twenty");
        original.addNode(30, "Thirty"); // isolated
        original.addEdge(10, 20); // unweighted
        original.addEdge(10, 20, 55); // parallel, weighted
        original.addEdge(20, 20, -3); // self-loop, weighted

        Graph copy = original.copy();
        assertEquals(3, copy.nbNodes(), "copy has same node count");
        assertEquals(3, copy.nbEdges(), "copy has same edge count");
        assertEquals("Ten", copy.getNode(10).getName(), "copy preserves node name 'Ten'");
        assertEquals("Twenty", copy.getNode(20).getName(), "copy preserves node name 'Twenty'");
        assertEquals("Thirty", copy.getNode(30).getName(), "copy preserves node name 'Thirty'");
        assertEquals(original.getAllEdges().toString(), copy.getAllEdges().toString(), "copy has identical edges and weights");

        // Mutation on copy does not affect original
        copy.removeEdge(10, 20);
        assertEquals(2, copy.nbEdges(), "copy edge count decreased after removal");
        assertEquals(3, original.nbEdges(), "original edge count unchanged after copy mutation");

        // --- Req 48: getReverse() ---
        Graph reverse = original.getReverse();
        assertEquals(3, reverse.nbNodes(), "reverse has same node count");
        assertEquals(3, reverse.nbEdges(), "reverse has same edge count");
        assertEquals("Ten", reverse.getNode(10).getName(), "reverse preserves node names");
        assertTrue(reverse.existsEdge(20, 10), "reverse has edge from 20 to 10");
        assertTrue(!reverse.existsEdge(10, 20), "reverse does not have edge from 10 to 20");
        assertTrue(reverse.existsEdge(20, 20), "reverse preserves self-loop on 20");
        assertEquals(Integer.valueOf(-3), reverse.getEdges(20, 20).get(0).getWeight(), "reverse preserves self-loop weight");

        // Double reverse produces original edges
        Graph doubleReverse = reverse.getReverse();
        assertEquals(original.getAllEdges().toString(), doubleReverse.getAllEdges().toString(),
                "double reverse recovers exact original edges");

        // --- Req 53: toSimpleGraph() ---
        Graph simple = original.toSimpleGraph();
        assertEquals(3, simple.nbNodes(), "simple graph has all 3 nodes");
        assertEquals(1, simple.nbEdges(), "simple graph collapsed parallel edges and removed self-loop (only 1 edge left)");
        assertTrue(simple.isSimpleGraph(), "toSimpleGraph returns a simple graph");
        assertTrue(!simple.hasSelfLoops(), "toSimpleGraph has no self-loops");
        assertTrue(!simple.isMultiGraph(), "toSimpleGraph is not a multigraph");
        assertTrue(simple.existsEdge(10, 20), "simple graph maintains edge 10->20");
        assertTrue(!simple.existsEdge(20, 20), "simple graph removed self-loop 20->20");

        // --- Req 49: getTransitiveClosure() ---
        // Chain graph: 1 -> 2 -> 3 -> 4
        Graph chain = new Graph();
        chain.addEdge(1, 2);
        chain.addEdge(2, 3);
        chain.addEdge(3, 4);

        Graph chainClosure = chain.getTransitiveClosure();
        assertEquals(4, chainClosure.nbNodes(), "chain closure has 4 nodes");
        // All pairs (u, v) with u < v should be connected: (1,2), (1,3), (1,4), (2,3), (2,4), (3,4) = 6 edges
        assertEquals(6, chainClosure.nbEdges(), "chain closure has 6 edges (all forward pairs)");
        assertTrue(chainClosure.existsEdge(1, 4), "chain closure: 1 can reach 4");
        assertTrue(chainClosure.existsEdge(1, 3), "chain closure: 1 can reach 3");
        assertTrue(!chainClosure.existsEdge(4, 1), "chain closure: 4 cannot reach 1 (DAG)");
        assertTrue(!chainClosure.hasSelfLoops(), "chain closure of DAG has no self-loops");

        // Cycle graph: 1 -> 2 -> 3 -> 1
        Graph cycle = new Graph();
        cycle.addEdge(1, 2);
        cycle.addEdge(2, 3);
        cycle.addEdge(3, 1);

        Graph cycleClosure = cycle.getTransitiveClosure();
        assertEquals(3, cycleClosure.nbNodes(), "cycle closure has 3 nodes");
        // In a directed cycle, all 3 nodes can reach all 3 nodes including themselves (3 x 3 = 9 edges)
        assertEquals(9, cycleClosure.nbEdges(), "cycle closure has 9 edges (complete directed graph with loops)");
        assertTrue(cycleClosure.hasSelfLoops(), "cycle closure has self-loops (reachable from themselves)");
        assertTrue(cycleClosure.existsEdge(1, 1), "cycle closure: 1 reaches 1");
        assertTrue(cycleClosure.existsEdge(2, 2), "cycle closure: 2 reaches 2");
        assertTrue(cycleClosure.existsEdge(3, 3), "cycle closure: 3 reaches 3");

        // Idempotence: closure of closure is identical
        Graph cycleClosure2 = cycleClosure.getTransitiveClosure();
        assertEquals(cycleClosure.getAllEdges().toString(), cycleClosure2.getAllEdges().toString(),
                "transitive closure is idempotent");

        // Empty graph closure
        Graph emptyClosure = new Graph().getTransitiveClosure();
        assertEquals(0, emptyClosure.nbNodes(), "empty closure has 0 nodes");
        assertEquals(0, emptyClosure.nbEdges(), "empty closure has 0 edges");
    }
}
