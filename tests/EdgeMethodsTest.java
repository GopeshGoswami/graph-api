package tests;

import m1graphs2026.Edge;
import m1graphs2026.Graph;
import m1graphs2026.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comprehensive verification of all Edge constructors, methods 14 through 20,
 * and Comparable/equals contracts specified in PDF Sections 3.3 and 5.2.
 */
public final class EdgeMethodsTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private EdgeMethodsTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Comprehensive Edge API Tests ===");

        testConstructorsAndConstraints();
        testEdgeEndpointsAndSymmetric();
        testSelfLoop();
        testWeights();
        testMultiEdge();
        testMultiTierOrdering();
        testEqualsAndHashCode();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL EDGE API TESTS COMPLIANT AND VERIFIED!");
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

    private static void testConstructorsAndConstraints() {
        System.out.println("\nTesting Edge constructors & constraint enforcement (Sec 3.3):");
        Graph g1 = new Graph();
        g1.addNode(1);
        g1.addNode(2);
        Node n1 = g1.getNode(1);
        Node n2 = g1.getNode(2);

        Graph g2 = new Graph();
        g2.addNode(1);
        Node foreignNode = g2.getNode(1);

        // Valid Node constructors
        Edge eNodeUnweighted = new Edge(n1, n2);
        assertEquals(n1, eNodeUnweighted.from(), "Edge(Node, Node) from");
        assertEquals(n2, eNodeUnweighted.to(), "Edge(Node, Node) to");
        assertEquals(null, eNodeUnweighted.getWeight(), "Edge(Node, Node) unweighted");

        Edge eNodeWeighted = new Edge(n1, n2, 7);
        assertEquals(Integer.valueOf(7), eNodeWeighted.getWeight(), "Edge(Node, Node, Integer) weight 7");

        // Valid ID constructors
        Edge eIdUnweighted = new Edge(1, 2, g1);
        assertEquals(n1, eIdUnweighted.from(), "Edge(int, int, Graph) resolves from");
        assertEquals(n2, eIdUnweighted.to(), "Edge(int, int, Graph) resolves to");

        Edge eIdWeighted = new Edge(1, 2, g1, -3);
        assertEquals(Integer.valueOf(-3), eIdWeighted.getWeight(), "Edge(int, int, Graph, Integer) negative weight");

        // Constraint: null endpoints
        assertThrowsIllegalArgument(() -> new Edge(null, n2), "Reject null from node");
        assertThrowsIllegalArgument(() -> new Edge(n1, null), "Reject null to node");
        assertThrowsIllegalArgument(() -> new Edge(null, null), "Reject both null nodes");

        // Constraint: different graphs
        assertThrowsIllegalArgument(() -> new Edge(n1, foreignNode), "Reject nodes from different graphs");

        // Constraint: null graph in ID constructors
        assertThrowsIllegalArgument(() -> new Edge(1, 2, null), "Reject null graph in ID constructor");
        assertThrowsIllegalArgument(() -> new Edge(1, 2, null, 5), "Reject null graph in weighted ID constructor");

        // Edge(int, int, Graph) auto-creates nodes in graph if not present (required by TestGraphsPW2)
        Edge eAutoCreated = new Edge(1, 999, g1);
        assertEquals(999, eAutoCreated.to().getId(), "Auto-creates node 999 if not present");
        assertTrue(g1.holdsNode(999), "Graph holds node 999");
    }

    private static void testEdgeEndpointsAndSymmetric() {
        System.out.println("\nTesting from, to, and getSymmetric (Methods 14, 15, 16):");
        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);

        Edge e = new Edge(n1, n2, 15);
        assertEquals(n1, e.from(), "Method 14: from() returns source node");
        assertEquals(n2, e.to(), "Method 15: to() returns target node");

        Edge sym = e.getSymmetric();
        assertEquals(n2, sym.from(), "Method 16: getSymmetric() reverses from");
        assertEquals(n1, sym.to(), "Method 16: getSymmetric() reverses to");
        assertEquals(Integer.valueOf(15), sym.getWeight(), "Method 16: getSymmetric() preserves weight");

        Edge unweighted = new Edge(n1, n2);
        Edge unweightedSym = unweighted.getSymmetric();
        assertEquals(null, unweightedSym.getWeight(), "Method 16: symmetric of unweighted edge is unweighted");
    }

    private static void testSelfLoop() {
        System.out.println("\nTesting isSelfLoop (Method 17):");
        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);

        Edge loop = new Edge(n1, n1);
        assertTrue(loop.isSelfLoop(), "Method 17: loop from 1 to 1 isSelfLoop() is true");

        Edge nonLoop = new Edge(n1, n2);
        assertTrue(!nonLoop.isSelfLoop(), "Method 17: edge 1 to 2 isSelfLoop() is false");
    }

    private static void testWeights() {
        System.out.println("\nTesting isWeighted and getWeight (Methods 19, 20):");
        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);

        Edge unweighted = new Edge(n1, n2, null);
        assertTrue(!unweighted.isWeighted(), "Method 19: null weight isWeighted() is false");
        assertEquals(null, unweighted.getWeight(), "Method 20: null weight getWeight() is null");

        Edge zeroWeight = new Edge(n1, n2, 0);
        assertTrue(zeroWeight.isWeighted(), "Method 19: weight 0 isWeighted() is true");
        assertEquals(Integer.valueOf(0), zeroWeight.getWeight(), "Method 20: weight 0 returned correctly");

        Edge negativeWeight = new Edge(n1, n2, -42);
        assertTrue(negativeWeight.isWeighted(), "Method 19: negative weight isWeighted() is true");
        assertEquals(Integer.valueOf(-42), negativeWeight.getWeight(), "Method 20: negative weight returned correctly");

        Edge positiveWeight = new Edge(n1, n2, 100);
        assertTrue(positiveWeight.isWeighted(), "Method 19: positive weight isWeighted() is true");
        assertEquals(Integer.valueOf(100), positiveWeight.getWeight(), "Method 20: positive weight returned correctly");
    }

    private static void testMultiEdge() {
        System.out.println("\nTesting isMultiEdge (Method 18):");
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(1, 2); // parallel
        g.addEdge(2, 3); // simple

        List<Edge> edges1to2 = g.getEdges(g.getNode(1), g.getNode(2));
        assertTrue(edges1to2.size() == 2, "Graph holds 2 parallel edges from 1 to 2");
        assertTrue(edges1to2.get(0).isMultiEdge(), "Method 18: first parallel edge isMultiEdge() is true");
        assertTrue(edges1to2.get(1).isMultiEdge(), "Method 18: second parallel edge isMultiEdge() is true");

        List<Edge> edges2to3 = g.getEdges(g.getNode(2), g.getNode(3));
        assertTrue(edges2to3.size() == 1, "Graph holds 1 edge from 2 to 3");
        assertTrue(!edges2to3.get(0).isMultiEdge(), "Method 18: unique edge isMultiEdge() is false");
    }

    private static void testMultiTierOrdering() {
        System.out.println("\nTesting multi-tier Comparable<Edge> ordering (Sec 3.3):");
        Graph g = new Graph();
        for (int i = 1; i <= 8; i++) g.addNode(i);

        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);
        Node n3 = g.getNode(3);
        Node n4 = g.getNode(4);

        Edge e1_2_null = new Edge(n1, n2);
        Edge e1_2_w0 = new Edge(n1, n2, 0);
        Edge e1_2_w5 = new Edge(n1, n2, 5);
        Edge e1_3_null = new Edge(n1, n3);
        Edge e2_1_null = new Edge(n2, n1);

        // Tier 1: Source node ID order
        assertTrue(e1_2_null.compareTo(e2_1_null) < 0, "Source node ID 1 < 2");
        assertTrue(e2_1_null.compareTo(e1_2_null) > 0, "Source node ID 2 > 1");

        // Tier 2: Target node ID order when sources equal
        assertTrue(e1_2_null.compareTo(e1_3_null) < 0, "Target node ID 2 < 3 when source is 1");
        assertTrue(e1_3_null.compareTo(e1_2_null) > 0, "Target node ID 3 > 2 when source is 1");

        // Tier 3: Weight order when endpoints equal (unweighted < weighted)
        assertTrue(e1_2_null.compareTo(e1_2_w0) < 0, "Unweighted precedes weighted (null < 0)");
        assertTrue(e1_2_w0.compareTo(e1_2_w5) < 0, "Weight 0 < 5");
        assertTrue(e1_2_w5.compareTo(e1_2_w0) > 0, "Weight 5 > 0");

        // Reproduce PDF Page 3 Fig 1 enumeration example:
        // (1,2), (1,4), (3,6), (4,2), (4,3), (4,5), (4,8), (6,4), (6,7), (7,3), (8,7)
        Graph fig1 = new Graph(2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0);
        List<Edge> sortedEdges = fig1.getAllEdges();
        assertEquals(11, sortedEdges.size(), "Fig 1 has 11 edges");

        String expectedOrder = "[1->2, 1->4, 3->6, 4->2, 4->3, 4->5, 4->8, 6->4, 6->7, 7->3, 8->7]";
        assertEquals(expectedOrder, sortedEdges.toString(), "Edge sorting exactly matches PDF Section 3.3 enumeration");
    }

    private static void testEqualsAndHashCode() {
        System.out.println("\nTesting equals and hashCode:");
        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        Node n1 = g.getNode(1);
        Node n2 = g.getNode(2);

        Edge e1 = new Edge(n1, n2, 10);
        Edge e1Clone = new Edge(n1, n2, 10);
        Edge eDifferentWeight = new Edge(n1, n2, 20);
        Edge eUnweighted = new Edge(n1, n2, null);
        Edge eReverse = new Edge(n2, n1, 10);

        assertEquals(e1, e1Clone, "equals: same endpoints and same weight");
        assertEquals(e1.hashCode(), e1Clone.hashCode(), "hashCode: equal edges have equal hashCodes");

        assertTrue(!e1.equals(eDifferentWeight), "equals: different weights not equal");
        assertTrue(!e1.equals(eUnweighted), "equals: weighted vs unweighted not equal");
        assertTrue(!e1.equals(eReverse), "equals: reversed endpoints not equal");
        assertTrue(!e1.equals(null), "equals: not equal to null");
        assertTrue(!e1.equals("NotAnEdge"), "equals: not equal to other type");
    }

    private static void assertThrowsIllegalArgument(Runnable runnable, String label) {
        testsRun++;
        try {
            runnable.run();
            System.err.println("  [FAIL] Expected IllegalArgumentException for: " + label);
            throw new AssertionError("Expected IllegalArgumentException for: " + label);
        } catch (IllegalArgumentException e) {
            testsPassed++;
            System.out.println("  [PASS] Correctly rejected: " + label);
        }
    }
}
