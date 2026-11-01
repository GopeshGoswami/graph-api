package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import m1graphs2026.Graph;
import m1graphs2026.UndirectedGraph;

/**
 * Unit tests for DOT Import and Export operations (Milestone 8, Reqs 63, 64, 65).
 */
public final class GraphDotTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private GraphDotTest() { }

    public static void main(String[] args) {
        System.out.println("=== Starting Graph DOT Export Tests ===");

        testToDotStringDirected();
        testToDotStringUndirected();
        testToDotFileDefaultExtension();
        testToDotFileCustomExtension();
        testToDotFileUndirected();
        testToDotFileNestedDirectory();
        testToDotFileErrorHandling();
        testFromDotFileFig1();
        testFromDotFileWeightedAndSemicolons();
        testFromDotFileUndirected();
        testFromDotFileRoundTrip();
        testFromDotFileCommentsAndWhitespace();
        testFromDotFileIsolatedNodes();
        testFromDotFileErrorHandling();

        System.out.println("=== Test Results: " + testsPassed + "/" + testsRun + " passed ===");
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("GRAPH DOT IMPORT/EXPORT TESTS COMPLIANT AND VERIFIED!");
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

    public static void testToDotStringDirected() {
        System.out.println("\nTesting toDotString() for directed graphs:");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph fig1 = new Graph(fig1SA);

        String dot = fig1.toDotString();
        assertTrue(dot.startsWith("digraph {\n\trankdir=LR\n"), "DOT starts with 'digraph {\\n\\trankdir=LR\\n'");
        assertTrue(dot.endsWith("}\n"), "DOT ends with '}\\n'");

        // All edges of Fig 1
        assertTrue(dot.contains("\t1 -> 2\n"), "DOT contains 1 -> 2");
        assertTrue(dot.contains("\t1 -> 4\n"), "DOT contains 1 -> 4");
        assertTrue(dot.contains("\t3 -> 6\n"), "DOT contains 3 -> 6");
        assertTrue(dot.contains("\t4 -> 2\n"), "DOT contains 4 -> 2");
        assertTrue(dot.contains("\t4 -> 3\n"), "DOT contains 4 -> 3");
        assertTrue(dot.contains("\t4 -> 5\n"), "DOT contains 4 -> 5");
        assertTrue(dot.contains("\t4 -> 8\n"), "DOT contains 4 -> 8");
        assertTrue(dot.contains("\t6 -> 4\n"), "DOT contains 6 -> 4");
        assertTrue(dot.contains("\t6 -> 7\n"), "DOT contains 6 -> 7");
        assertTrue(dot.contains("\t7 -> 3\n"), "DOT contains 7 -> 3");
        assertTrue(dot.contains("\t8 -> 7\n"), "DOT contains 8 -> 7");

        // Weighted edge:
        Graph weighted = new Graph();
        weighted.addEdge(1, 2, 42);
        String weightedDot = weighted.toDotString();
        assertTrue(weightedDot.contains("\t1 -> 2 [label=42, len=42]\n"),
                "Weighted edge contains [label=42, len=42]");
    }

    public static void testToDotStringUndirected() {
        System.out.println("\nTesting toDotString() for undirected graphs:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(2, 3, 15);

        String dot = g.toDotString();
        assertTrue(dot.startsWith("graph {\n\trankdir=LR\n"), "Undirected DOT starts with 'graph {\\n\\trankdir=LR\\n'");
        assertTrue(dot.contains("\t1 -- 2\n"), "Undirected unweighted edge contains '--'");
        assertTrue(dot.contains("\t2 -- 3 [label=15, len=15]\n"), "Undirected weighted edge formatted correctly");
        assertTrue(!dot.contains("->"), "Undirected DOT does not contain '->'");
    }

    public static void testToDotFileDefaultExtension() {
        System.out.println("\nTesting toDotFile(String fileName) with default extension (.gv):");
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(2, 3, 7);

        String baseName = "bin/test_dot_default";
        Path expectedFile = Path.of(baseName + ".gv");

        try {
            Files.deleteIfExists(expectedFile);
            g.toDotFile(baseName);

            assertTrue(Files.exists(expectedFile), "Default extension .gv file was created");
            String content = Files.readString(expectedFile);
            assertEquals(g.toDotString(), content, "File content matches toDotString()");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(expectedFile);
            } catch (IOException ignored) { }
        }
    }

    public static void testToDotFileCustomExtension() {
        System.out.println("\nTesting toDotFile(String fileName, String extension):");
        Graph g = new Graph();
        g.addEdge(10, 20);

        String baseName = "bin/test_dot_custom";
        Path fileDot = Path.of(baseName + ".dot");
        Path fileTxt = Path.of(baseName + ".txt");

        try {
            Files.deleteIfExists(fileDot);
            Files.deleteIfExists(fileTxt);

            // Extension with leading dot
            g.toDotFile(baseName, ".dot");
            assertTrue(Files.exists(fileDot), "File with .dot extension created");
            assertEquals(g.toDotString(), Files.readString(fileDot), "Content matches .dot file");

            // Extension without leading dot
            g.toDotFile(baseName, "txt");
            assertTrue(Files.exists(fileTxt), "File with txt extension (auto-prefixed with dot) created");
            assertEquals(g.toDotString(), Files.readString(fileTxt), "Content matches .txt file");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(fileDot);
                Files.deleteIfExists(fileTxt);
            } catch (IOException ignored) { }
        }
    }

    public static void testToDotFileUndirected() {
        System.out.println("\nTesting toDotFile on UndirectedGraph:");
        UndirectedGraph g = new UndirectedGraph();
        g.addEdge(1, 2);
        g.addEdge(2, 3);

        String baseName = "bin/test_undirected_dot";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.deleteIfExists(file);
            g.toDotFile(baseName);

            assertTrue(Files.exists(file), "Undirected .gv file was created");
            String content = Files.readString(file);
            assertEquals(g.toDotString(), content, "File content matches UndirectedGraph toDotString()");
            assertTrue(content.contains("graph {\n\trankdir=LR\n"), "Content has graph {");
            assertTrue(content.contains("--"), "Content has -- edge operator");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testToDotFileNestedDirectory() {
        System.out.println("\nTesting toDotFile in nested non-existent directory:");
        Graph g = new Graph();
        g.addEdge(1, 2);

        String baseName = "bin/nested/test/dir/sample";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.deleteIfExists(file);
            g.toDotFile(baseName);

            assertTrue(Files.exists(file), "File in nested directory was created successfully");
            assertEquals(g.toDotString(), Files.readString(file), "Content matches");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
                Files.deleteIfExists(Path.of("bin/nested/test/dir"));
                Files.deleteIfExists(Path.of("bin/nested/test"));
                Files.deleteIfExists(Path.of("bin/nested"));
            } catch (IOException ignored) { }
        }
    }

    public static void testToDotFileErrorHandling() {
        System.out.println("\nTesting toDotFile error handling:");
        Graph g = new Graph();

        boolean threwFileNameNull = false;
        try {
            g.toDotFile(null);
        } catch (IllegalArgumentException e) {
            threwFileNameNull = true;
        }
        assertTrue(threwFileNameNull, "toDotFile(null) throws IllegalArgumentException");

        boolean threwExtNull = false;
        try {
            g.toDotFile("bin/test", null);
        } catch (IllegalArgumentException e) {
            threwExtNull = true;
        }
        assertTrue(threwExtNull, "toDotFile(name, null) throws IllegalArgumentException");
    }

    public static void testFromDotFileFig1() {
        System.out.println("\nTesting fromDotFile with Figure 4 DOT file (PDF Section 4.2):");
        String fig4Dot = """
                # DOT representation for the directed graph of Fig. 1
                digraph figureOne {
                1 -> 2
                1 -> 4
                3 -> 6
                4 -> 2
                4 -> 3
                4 -> 5
                4 -> 8
                6 -> 4
                6 -> 7
                7 -> 3
                8 -> 7
                }
                """;

        String baseName = "bin/test_fig4";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.writeString(file, fig4Dot);
            Graph g = Graph.fromDotFile(baseName);

            assertEquals(8, g.nbNodes(), "Fig 4 has 8 nodes");
            assertEquals(11, g.nbEdges(), "Fig 4 has 11 edges");

            int[] expectedSA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
            int[] actualSA = g.toSuccessorArray();
            assertEquals(expectedSA.length, actualSA.length, "SA length matches");
            for (int i = 0; i < expectedSA.length; i++) {
                assertEquals(expectedSA[i], actualSA[i], "SA element at " + i + " matches");
            }
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileWeightedAndSemicolons() {
        System.out.println("\nTesting fromDotFile with weighted edges and semicolons:");
        String dotContent = """
                digraph WeightedGraph {
                  1 -> 2 [label=5, len=5];
                  2 -> 3 [label=-10, len=-10];
                  3 -> 1;
                  1 -> 1 [label=99, len=99];
                }
                """;

        String baseName = "bin/test_weighted_dot";
        Path file = Path.of(baseName + ".dot");

        try {
            Files.writeString(file, dotContent);
            Graph g = Graph.fromDotFile(baseName, ".dot");

            assertEquals(3, g.nbNodes(), "Weighted graph has 3 nodes");
            assertEquals(4, g.nbEdges(), "Weighted graph has 4 edges");
            assertTrue(g.hasSelfLoops(), "Weighted graph has self-loop");

            assertTrue(g.existsEdge(1, 2), "Edge 1 -> 2 exists");
            assertEquals(Integer.valueOf(5), g.getEdges(1, 2).get(0).getWeight(), "Edge 1 -> 2 weight is 5");

            assertTrue(g.existsEdge(2, 3), "Edge 2 -> 3 exists");
            assertEquals(Integer.valueOf(-10), g.getEdges(2, 3).get(0).getWeight(), "Edge 2 -> 3 weight is -10");

            assertTrue(g.existsEdge(3, 1), "Edge 3 -> 1 exists");
            assertTrue(!g.getEdges(3, 1).get(0).isWeighted(), "Edge 3 -> 1 is unweighted");

            assertTrue(g.existsEdge(1, 1), "Self-loop edge 1 -> 1 exists");
            assertEquals(Integer.valueOf(99), g.getEdges(1, 1).get(0).getWeight(), "Self-loop weight is 99");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileUndirected() {
        System.out.println("\nTesting fromDotFile with UndirectedGraph syntax:");
        String undirectedDot = """
                graph G {
                  1 -- 2;
                  2 -- 3 [label=15, len=15];
                  3 -- 4;
                  1 -- 4;
                }
                """;

        String baseName = "bin/test_undirected_import";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.writeString(file, undirectedDot);

            // Import via Graph.fromDotFile
            Graph g = Graph.fromDotFile(baseName);
            assertTrue(g instanceof UndirectedGraph, "Imported graph is an instance of UndirectedGraph");
            assertEquals(4, g.nbNodes(), "Undirected graph has 4 nodes");
            assertEquals(4, g.nbEdges(), "Undirected graph has 4 edges");

            // Import via UndirectedGraph.fromDotFile
            UndirectedGraph ug = UndirectedGraph.fromDotFile(baseName);
            assertEquals(4, ug.nbNodes(), "UndirectedGraph.fromDotFile has 4 nodes");
            assertEquals(4, ug.nbEdges(), "UndirectedGraph.fromDotFile has 4 edges");
            assertEquals(2, ug.degree(1), "Node 1 has degree 2");
            assertEquals(2, ug.inDegree(1), "inDegree equals degree in undirected graph");
            assertEquals(2, ug.outDegree(1), "outDegree equals degree in undirected graph");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileRoundTrip() {
        System.out.println("\nTesting round-trip export -> import -> comparison:");
        int[] fig1SA = {2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0, 0, 4, 7, 0, 3, 0, 7, 0};
        Graph original = new Graph(fig1SA);

        String baseName = "bin/test_round_trip";
        Path file = Path.of(baseName + ".gv");

        try {
            original.toDotFile(baseName);
            Graph imported = Graph.fromDotFile(baseName);

            assertEquals(original.nbNodes(), imported.nbNodes(), "Round-trip preserves node count");
            assertEquals(original.nbEdges(), imported.nbEdges(), "Round-trip preserves edge count");
            assertEquals(original.getAllNodes().toString(), imported.getAllNodes().toString(), "Preserves nodes");
            assertEquals(original.getAllEdges().toString(), imported.getAllEdges().toString(), "Preserves edges");
            assertEquals(original.toDotString(), imported.toDotString(), "Preserves exact DOT string");
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileCommentsAndWhitespace() {
        System.out.println("\nTesting fromDotFile with mixed comments and whitespace:");
        String content = """
                /*
                 * Multi-line block comment
                 * describing this graph
                 */
                digraph Commented {
                  // Single line comment
                  1 -> 2; # Inline hash comment
                  /* Inline block comment */ 2 -> 3;

                  // Blank lines above and below

                  3 -> 1; // Trailing comment
                }
                """;

        String baseName = "bin/test_comments";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.writeString(file, content);
            Graph g = Graph.fromDotFile(baseName);

            assertEquals(3, g.nbNodes(), "Comments parsed correctly: 3 nodes");
            assertEquals(3, g.nbEdges(), "Comments parsed correctly: 3 edges");
            assertTrue(g.existsEdge(1, 2), "Edge 1 -> 2 exists");
            assertTrue(g.existsEdge(2, 3), "Edge 2 -> 3 exists");
            assertTrue(g.existsEdge(3, 1), "Edge 3 -> 1 exists");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileIsolatedNodes() {
        System.out.println("\nTesting fromDotFile with isolated nodes:");
        String content = """
                digraph Isolated {
                  1;
                  2;
                  3;
                  4;
                  1 -> 2;
                }
                """;

        String baseName = "bin/test_isolated";
        Path file = Path.of(baseName + ".gv");

        try {
            Files.writeString(file, content);
            Graph g = Graph.fromDotFile(baseName);

            assertEquals(4, g.nbNodes(), "Isolated graph has 4 nodes");
            assertEquals(1, g.nbEdges(), "Isolated graph has 1 edge");
            assertTrue(g.getNode(3) != null, "Node 3 exists");
            assertTrue(g.getNode(4) != null, "Node 4 exists");
            assertTrue(g.holdsNode(g.getNode(3)), "g holds node 3");
            assertTrue(g.holdsNode(g.getNode(4)), "g holds node 4");
            assertEquals(0, g.degree(3), "Node 3 has degree 0");
            assertEquals(0, g.degree(4), "Node 4 has degree 0");
        } catch (IOException e) {
            throw new AssertionError("IO error in test: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) { }
        }
    }

    public static void testFromDotFileErrorHandling() {
        System.out.println("\nTesting fromDotFile error handling:");

        boolean threwNullName = false;
        try {
            Graph.fromDotFile(null);
        } catch (IllegalArgumentException e) {
            threwNullName = true;
        }
        assertTrue(threwNullName, "fromDotFile(null) throws IllegalArgumentException");

        boolean threwNullExt = false;
        try {
            Graph.fromDotFile("someFile", null);
        } catch (IllegalArgumentException e) {
            threwNullExt = true;
        }
        assertTrue(threwNullExt, "fromDotFile(name, null) throws IllegalArgumentException");

        boolean threwNonExistent = false;
        try {
            Graph.fromDotFile("bin/non_existent_file_xyz");
        } catch (IllegalArgumentException e) {
            threwNonExistent = true;
        }
        assertTrue(threwNonExistent, "Non-existent DOT file throws IllegalArgumentException");
    }
}
