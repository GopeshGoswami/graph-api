package tests;

import m1graphs2026.Edge;
import m1graphs2026.Graph;
import m1graphs2026.Node;

import java.util.List;

/**
 * Tests for Graph Node Management operations (Milestone 3).
 */
public final class GraphNodeManagementTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private GraphNodeManagementTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Graph Node Management Tests ===");

        testRemoveNode();
        testLargestAndSmallestNodeId();
        testSuccessorsInGraph();
        testDegreeOverloads();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("GRAPH NODE MANAGEMENT TESTS COMPLIANT AND VERIFIED!");
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

    public static void testRemoveNode() {
        System.out.println("\nTesting removeNode(Node) and removeNode(int) (Req 26):");
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.addEdge(3, 2);
        g.addEdge(2, 2); // self-loop on 2

        assertEquals(3, g.nbNodes(), "Initial node count is 3");
        assertEquals(5, g.nbEdges(), "Initial edge count is 5");

        Node n2 = g.getNode(2);
        assertTrue(n2 != null, "Node 2 exists before removal");

        // Remove node 2 by Node reference
        boolean removed = g.removeNode(n2);
        assertTrue(removed, "removeNode(Node) returns true for existing node");
        assertEquals(2, g.nbNodes(), "Node count is 2 after removal");
        assertTrue(!g.usesNode(2), "usesNode(2) is false");
        assertEquals(null, g.getNode(2), "getNode(2) is null");

        // Incident edges to 2 were:
        // Outgoing: 2->3, 2->2
        // Incoming: 1->2, 3->2, 2->2
        // Remaining edges should only be: 1->3
        assertEquals(1, g.nbEdges(), "Only 1 edge remains (1->3) after removing node 2");
        List<Edge> remainingEdges = g.getAllEdges();
        assertEquals(1, remainingEdges.size(), "getAllEdges() has 1 edge");
        assertEquals(1, remainingEdges.get(0).from().getId(), "Remaining edge from is 1");
        assertEquals(3, remainingEdges.get(0).to().getId(), "Remaining edge to is 3");

        // Attempting to remove already removed node returns false
        assertTrue(!g.removeNode(n2), "Removing already removed node returns false");
        assertTrue(!g.removeNode(2), "removeNode(int) for absent ID returns false");

        // Edge case: null node
        assertTrue(!g.removeNode((Node) null), "removeNode(null) returns false");

        // Edge case: foreign node from another graph
        Graph foreignGraph = new Graph();
        foreignGraph.addNode(1);
        Node foreignNode = foreignGraph.getNode(1);
        assertTrue(!g.removeNode(foreignNode), "removeNode with foreign node returns false");

        // Remove node 1 by integer ID
        boolean removed1 = g.removeNode(1);
        assertTrue(removed1, "removeNode(1) returns true");
        assertEquals(1, g.nbNodes(), "Node count is 1 after removing 1");
        assertEquals(0, g.nbEdges(), "Edge count is 0 after removing 1");
    }

    public static void testLargestAndSmallestNodeId() {
        System.out.println("\nTesting largestNodeId and smallestNodeId (Reqs 28, 29):");
        Graph g = new Graph();
        g.addNode(10);
        g.addNode(3);
        g.addNode(42);
        g.addNode(7);

        assertEquals(42, g.largestNodeId(), "Req 28: largestNodeId is 42");
        assertEquals(3, g.smallestNodeId(), "Req 29: smallestNodeId is 3");

        // After removing largest node
        g.removeNode(42);
        assertEquals(10, g.largestNodeId(), "largestNodeId updates to 10 after removing 42");

        // After removing smallest node
        g.removeNode(3);
        assertEquals(7, g.smallestNodeId(), "smallestNodeId updates to 7 after removing 3");

        // Single node graph
        Graph singleNode = new Graph();
        singleNode.addNode(5);
        assertEquals(5, singleNode.largestNodeId(), "Single node largestNodeId is 5");
        assertEquals(5, singleNode.smallestNodeId(), "Single node smallestNodeId is 5");

        // Empty graph must throw NoSuchElementException
        Graph empty = new Graph();
        boolean threwLargestOnEmpty = false;
        try {
            empty.largestNodeId();
        } catch (java.util.NoSuchElementException e) {
            threwLargestOnEmpty = true;
        }
        assertTrue(threwLargestOnEmpty, "largestNodeId() throws NoSuchElementException on empty graph");

        boolean threwSmallestOnEmpty = false;
        try {
            empty.smallestNodeId();
        } catch (java.util.NoSuchElementException e) {
            threwSmallestOnEmpty = true;
        }
        assertTrue(threwSmallestOnEmpty, "smallestNodeId() throws NoSuchElementException on empty graph");
    }

    public static void testSuccessorsInGraph() {
        System.out.println("\nTesting Graph getSuccessors and getSuccessorsMulti (Reqs 30, 31):");
        Graph g = new Graph();
        // Add edges: 1->3, 1->2, 1->2 (parallel), 1->1 (self-loop)
        g.addEdge(1, 3);
        g.addEdge(1, 2);
        g.addEdge(1, 2);
        g.addEdge(1, 1);
        g.addNode(4); // isolated node

        Node n1 = g.getNode(1);
        Node n4 = g.getNode(4);

        // Req 30: getSuccessors(Node) and getSuccessors(int) - unique sorted
        List<Node> succNode = g.getSuccessors(n1);
        assertEquals(3, succNode.size(), "Req 30: unique successors count (1, 2, 3)");
        assertEquals(1, succNode.get(0).getId(), "succNode[0] is 1 (self-loop)");
        assertEquals(2, succNode.get(1).getId(), "succNode[1] is 2");
        assertEquals(3, succNode.get(2).getId(), "succNode[2] is 3");

        List<Node> succId = g.getSuccessors(1);
        assertEquals(3, succId.size(), "Req 30: getSuccessors(int id) returns same size");
        assertEquals(succNode, succId, "getSuccessors(Node) equals getSuccessors(int)");

        // Req 31: getSuccessorsMulti(Node) and getSuccessorsMulti(int) - with duplicates, sorted
        List<Node> multiNode = g.getSuccessorsMulti(n1);
        assertEquals(4, multiNode.size(), "Req 31: multi successors count is 4");
        assertEquals(1, multiNode.get(0).getId(), "multiNode[0] is 1");
        assertEquals(2, multiNode.get(1).getId(), "multiNode[1] is 2");
        assertEquals(2, multiNode.get(2).getId(), "multiNode[2] is 2 (parallel)");
        assertEquals(3, multiNode.get(3).getId(), "multiNode[3] is 3");

        List<Node> multiId = g.getSuccessorsMulti(1);
        assertEquals(4, multiId.size(), "Req 31: getSuccessorsMulti(int id) returns same size");
        assertEquals(multiNode, multiId, "getSuccessorsMulti(Node) equals getSuccessorsMulti(int)");

        // Isolated node
        assertEquals(0, g.getSuccessors(n4).size(), "Isolated node has 0 unique successors");
        assertEquals(0, g.getSuccessorsMulti(n4).size(), "Isolated node has 0 multi successors");
        assertEquals(0, g.getSuccessors(4).size(), "getSuccessors(4) has 0 unique successors");
        assertEquals(0, g.getSuccessorsMulti(4).size(), "getSuccessorsMulti(4) has 0 multi successors");

        // Non-existent node ID
        assertEquals(0, g.getSuccessors(999).size(), "getSuccessors(999) on absent ID returns empty list");
        assertEquals(0, g.getSuccessorsMulti(999).size(), "getSuccessorsMulti(999) on absent ID returns empty list");
    }

    public static void testDegreeOverloads() {
        System.out.println("\nTesting Graph inDegree, outDegree, and degree overloads (Reqs 33, 34, 35):");
        Graph g = new Graph();
        // Graph structure around node 2:
        // Incoming to 2: 1->2, 1->2 (parallel)
        // Outgoing from 2: 2->3
        // Self-loop on 2: 2->2 (1 incoming, 1 outgoing)
        // Isolated node: 4
        g.addEdge(1, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 2);
        g.addNode(4);

        Node n2 = g.getNode(2);
        Node n4 = g.getNode(4);

        // Req 33: inDegree(Node) vs inDegree(int)
        assertEquals(3, g.inDegree(n2), "Req 33: inDegree(Node) counts parallel edges and self-loop");
        assertEquals(3, g.inDegree(2), "Req 33: inDegree(int) returns same as inDegree(Node)");

        // Req 34: outDegree(Node) vs outDegree(int)
        assertEquals(2, g.outDegree(n2), "Req 34: outDegree(Node) counts outgoing edge and self-loop");
        assertEquals(2, g.outDegree(2), "Req 34: outDegree(int) returns same as outDegree(Node)");

        // Req 35: degree(Node) vs degree(int)
        assertEquals(5, g.degree(n2), "Req 35: degree(Node) is inDegree + outDegree (3 + 2 = 5)");
        assertEquals(5, g.degree(2), "Req 35: degree(int) is inDegree + outDegree (3 + 2 = 5)");

        // Isolated node: 4
        assertEquals(0, g.inDegree(n4), "Isolated node inDegree(Node) is 0");
        assertEquals(0, g.inDegree(4), "Isolated node inDegree(4) is 0");
        assertEquals(0, g.outDegree(n4), "Isolated node outDegree(Node) is 0");
        assertEquals(0, g.outDegree(4), "Isolated node outDegree(4) is 0");
        assertEquals(0, g.degree(n4), "Isolated node degree(Node) is 0");
        assertEquals(0, g.degree(4), "Isolated node degree(4) is 0");

        // Non-existent ID: 999
        assertEquals(0, g.inDegree(999), "inDegree(999) on absent ID returns 0");
        assertEquals(0, g.outDegree(999), "outDegree(999) on absent ID returns 0");
        assertEquals(0, g.degree(999), "degree(999) on absent ID returns 0");
    }
}
