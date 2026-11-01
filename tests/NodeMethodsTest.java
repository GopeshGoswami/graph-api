package tests;

import m1graphs2026.Edge;
import m1graphs2026.Graph;
import m1graphs2026.Node;

import java.util.List;

/**
 * Comprehensive verification of all 13 Node API methods specified in PDF Section 5.1.
 */
public final class NodeMethodsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private NodeMethodsTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Comprehensive Node API Tests ===");

        testConstructorsAndAccessors();
        testSuccessorsAndMultiSuccessors();
        testAdjacency();
        testDegreesWithSelfLoopsAndParallelEdges();
        testEdgeRetrievalMethods();
        testIsolatedNode();
        testComparableAndEquality();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL NODE API TESTS COMPLIANT AND VERIFIED!");
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

    private static void testConstructorsAndAccessors() {
        System.out.println("\nTesting Node constructors, getId, getGraph, getName (Methods 1-3):");
        Graph g1 = new Graph();
        Node n1 = new Node(10, g1);
        assertEquals(10, n1.getId(), "Method 1: getId() returns correct id");
        assertTrue(n1.getGraph() == g1, "Method 2: getGraph() returns owning graph holder");
        assertEquals(null, n1.getName(), "Method 3: getName() returns null when unnamed");

        Node n2 = new Node(20, "NodeTwenty", g1);
        assertEquals(20, n2.getId(), "getId() for named node");
        assertEquals("NodeTwenty", n2.getName(), "Method 3: getName() returns assigned name");

        boolean threwOnNullGraph = false;
        try {
            new Node(1, null);
        } catch (IllegalArgumentException e) {
            threwOnNullGraph = true;
        }
        assertTrue(threwOnNullGraph, "Constructor rejects null graph holder");
    }

    private static void testSuccessorsAndMultiSuccessors() {
        System.out.println("\nTesting getSuccessors and getSuccessorsMulti (Methods 4-5):");
        Graph g = new Graph();
        // Add edges: 1->3, 1->2, 1->2 (parallel)
        g.addEdge(1, 3);
        g.addEdge(1, 2);
        g.addEdge(1, 2);

        Node n1 = g.getNode(1);

        List<Node> uniqueSuccessors = n1.getSuccessors();
        assertEquals(2, uniqueSuccessors.size(), "Method 4: getSuccessors() size without duplicates");
        assertEquals(2, uniqueSuccessors.get(0).getId(), "Method 4: getSuccessors() sorted order (first is 2)");
        assertEquals(3, uniqueSuccessors.get(1).getId(), "Method 4: getSuccessors() sorted order (second is 3)");

        List<Node> multiSuccessors = n1.getSuccessorsMulti();
        assertEquals(3, multiSuccessors.size(), "Method 5: getSuccessorsMulti() size with duplicates");
        assertEquals(2, multiSuccessors.get(0).getId(), "Method 5: multiSuccessors[0] is 2");
        assertEquals(2, multiSuccessors.get(1).getId(), "Method 5: multiSuccessors[1] is 2");
        assertEquals(3, multiSuccessors.get(2).getId(), "Method 5: multiSuccessors[2] is 3");
    }

    private static void testAdjacency() {
        System.out.println("\nTesting adjacent(Node) and adjacent(int) (Method 6):");
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(2, 3);

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);

        assertTrue(n1.adjacent(n2), "Method 6: 1 is adjacent to 2 (Node overload)");
        assertTrue(n1.adjacent(2), "Method 6: 1 is adjacent to 2 (int overload)");

        assertTrue(!n2.adjacent(n1), "Method 6: directed adjacency (2 is not adjacent to 1)");
        assertTrue(!n2.adjacent(1), "Method 6: directed adjacency int (2 is not adjacent to 1)");

        assertTrue(!n1.adjacent(n3), "Method 6: 1 is not adjacent to 3");
        assertTrue(!n1.adjacent(3), "Method 6: 1 is not adjacent to 3 (int overload)");
    }

    private static void testDegreesWithSelfLoopsAndParallelEdges() {
        System.out.println("\nTesting inDegree, outDegree, degree (Methods 7-9):");
        Graph g = new Graph();
        // Graph structure around node 2:
        // Incoming to 2: 1->2, 1->2 (two parallel edges)
        // Outgoing from 2: 2->3
        // Self-loop on 2: 2->2 (counts as 1 in-edge and 1 out-edge)
        g.addEdge(1, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 2);

        Node n2 = g.getNode(2);

        assertEquals(3, n2.inDegree(), "Method 7: inDegree() counts parallel edges and self-loop");
        assertEquals(2, n2.outDegree(), "Method 8: outDegree() counts outgoing edge and self-loop");
        assertEquals(5, n2.degree(), "Method 9: degree() is inDegree + outDegree (3 + 2 = 5)");
    }

    private static void testEdgeRetrievalMethods() {
        System.out.println("\nTesting getOutEdges, getInEdges, getIncidentEdges, getEdgesTo (Methods 10-13):");
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 2); // self-loop

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);

        // Method 10: getOutEdges()
        List<Edge> out1 = n1.getOutEdges();
        assertEquals(2, out1.size(), "Method 10: n1 has 2 out edges");

        // Method 11: getInEdges()
        List<Edge> in2 = n2.getInEdges();
        assertEquals(3, in2.size(), "Method 11: n2 has 3 in edges (two from 1, one loop)");

        // Method 12: getIncidentEdges()
        // Out of n2: (2, 2), (2, 3)
        // In to n2: (1, 2), (1, 2), (2, 2)
        // Incident union: (1, 2), (1, 2), (2, 2), (2, 3) -> size 4 (self-loop not duplicated)
        List<Edge> incident2 = n2.getIncidentEdges();
        assertEquals(4, incident2.size(), "Method 12: getIncidentEdges() includes parallel in-edges, out-edges, and loop once");

        // Method 13: getEdgesTo(Node u)
        List<Edge> edges1to2 = n1.getEdgesTo(n2);
        assertEquals(2, edges1to2.size(), "Method 13: getEdgesTo(n2) returns both parallel edges");
        assertEquals(0, n1.getEdgesTo(n3).size(), "Method 13: getEdgesTo(n3) returns empty list when no edge");
    }

    private static void testIsolatedNode() {
        System.out.println("\nTesting isolated node behavior:");
        Graph g = new Graph();
        g.addNode(99);
        Node isolated = g.getNode(99);

        assertEquals(0, isolated.inDegree(), "Isolated node inDegree is 0");
        assertEquals(0, isolated.outDegree(), "Isolated node outDegree is 0");
        assertEquals(0, isolated.degree(), "Isolated node degree is 0");
        assertEquals(0, isolated.getSuccessors().size(), "Isolated node getSuccessors is empty");
        assertEquals(0, isolated.getSuccessorsMulti().size(), "Isolated node getSuccessorsMulti is empty");
        assertEquals(0, isolated.getOutEdges().size(), "Isolated node getOutEdges is empty");
        assertEquals(0, isolated.getInEdges().size(), "Isolated node getInEdges is empty");
        assertEquals(0, isolated.getIncidentEdges().size(), "Isolated node getIncidentEdges is empty");
    }

    private static void testComparableAndEquality() {
        System.out.println("\nTesting Comparable<Node>, equals, hashCode:");
        Graph g1 = new Graph();
        Graph g2 = new Graph();

        Node a = new Node(5, g1);
        Node b = new Node(10, g1);
        Node aClone = new Node(5, g1);
        Node aDifferentGraph = new Node(5, g2);

        assertTrue(a.compareTo(b) < 0, "compareTo: 5 < 10");
        assertTrue(b.compareTo(a) > 0, "compareTo: 10 > 5");
        assertEquals(0, a.compareTo(aClone), "compareTo: 5 == 5");

        assertEquals(a, aClone, "equals: same ID and same Graph");
        assertEquals(a.hashCode(), aClone.hashCode(), "hashCode: equal nodes have equal hashCodes");

        assertTrue(!a.equals(aDifferentGraph), "equals: same ID but different Graph is not equal");
        assertTrue(!a.equals(b), "equals: different IDs not equal");
        assertTrue(!a.equals(null), "equals: not equal to null");
    }
}
