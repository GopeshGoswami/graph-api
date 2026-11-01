package tests;

import m1graphs2026.Graph;
import m1graphs2026.Node;

/** Small command-line checks for the first ten Node methods. */
public final class FirstTenSmokeTest {
    private FirstTenSmokeTest() { }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(1, 2); // parallel edge
        graph.addEdge(2, 2); // self-loop

        Node one = graph.getNode(1);
        Node two = graph.getNode(2);

        check(one.getId() == 1, "getId");
        check(one.getGraph() == graph, "getGraph");
        check(one.getName() == null, "getName for unnamed node");
        check(one.getSuccessors().size() == 1, "unique successors");
        check(one.getSuccessorsMulti().size() == 2, "multi successors");
        check(one.adjacent(two), "adjacent(Node)");
        check(one.adjacent(2), "adjacent(int)");
        check(two.inDegree() == 3, "in-degree including parallel edges and loop");
        check(two.outDegree() == 1, "out-degree including loop");
        check(two.degree() == 4, "degree = in + out");
        check(one.getOutEdges().size() == 2, "outgoing edges");

        System.out.println("All first-ten smoke checks passed.");
    }

    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError("Check failed: " + label);
    }
}
