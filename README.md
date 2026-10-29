# M1 Graphs — first 10 Node API methods (reference project)

This is a standalone Java 17 learning example based on the attached `Graph API in Java` specification. It implements the first ten `Node` API items and only the supporting `Graph`/`Edge` operations needed for them. It is **not** a complete implementation of every method used by `TestGraphsPW2.java`.

## Files

- `src/m1graphs2026/Node.java` — the first ten Node methods, equality/hash code, and ordering.
- `src/m1graphs2026/Edge.java` — directed edge objects, optional integer weight, self-loop/multi-edge helpers, and predictable ordering.
- `src/m1graphs2026/Graph.java` — adjacent edge lists plus the node/edge operations needed to exercise the Node methods.
- `src/GraphDemoGUI.java` — Swing GUI to add nodes/edges, click a node, and inspect method results.

The graphical window is a learning companion for the example classes. Keep it separate from your original `TestGraphsPW2.java`, because that tester invokes later assignment methods that this reference has not yet implemented.

## Run on macOS or Linux

Open Terminal in this folder and run these commands one by one:

```bash
# Check that the Java compiler is available.
java -version
javac -version

# Remove old compiled classes, if any.
rm -rf out

# Compile all source files against Java 17 APIs.
mkdir -p out
javac --release 17 -Xlint:all -d out $(find src -name '*.java')

# Open the graphical application.
java -cp out GraphDemoGUI
```

No Maven project or external Java libraries are required. A desktop environment is required for the Swing GUI; the application cannot display a window in a headless terminal.

## Try these cases in the GUI

1. Click node `1` and look at `getSuccessors()`, `getSuccessorsMulti()`, the degrees, and `getOutEdges()`.
2. Add the edge `1 -> 2` again. The unique successors list should still contain node 2 once, while the multi-successors list will contain node 2 twice.
3. Add `2 -> 2`. This is a self-loop: node 2's in-degree and out-degree each increase by one, so the directed total degree increases by two.
4. Add an isolated node, for example ID `12`. Its successor lists and degrees should be empty/zero until edges are added.
5. Clear the graph, then add a single self-loop to test the empty-graph case and the self-loop case independently.

## Scope and interpretation notes

- For the directed graph in this example, `degree()` is defined as `inDegree() + outDegree()`.
- `adjacent(u)` checks for an outgoing edge from this node to `u` (direction matters).
- A zero in the successor-array constructor terminates the current node's successor group; the constructor's input describes nodes with IDs `1..N`.
- Methods return new lists so callers cannot accidentally change the internal edge storage by editing a returned list.
- The map used for the graph representation is named `adjEdList`, as in the project specification.
