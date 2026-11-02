# M1 Graphs 2026 — Graph API in Java

**Authors:**
- **Hunain Pervaiz**
- **Gopesh Goswami**

**Course:** Master 1 — Graph Algorithms & Object-Oriented Modeling  
**Academic Year:** 2025–2026  

---

## 1. Project Overview

This project implements a modular, high-performance Graph API in Java conforming to the M1 Graphs specification. It provides comprehensive data structures and algorithms for both **directed** and **undirected** graphs, supporting:
- Simple graphs, multigraphs (parallel edges), and self-loops.
- Unweighted and weighted edges.
- Standard graph representations: Adjacency Lists (`adjEdList`), Successor Arrays, and Adjacency Matrices.
- Graph traversals: Breadth-First Search (BFS) and Depth-First Search (DFS) with node visit information (`NodeVisitInfo`), timestamps, and edge classification (`TREE`, `BACK`, `FORWARD`, `CROSS`).
- Graph operations: Graph transpose/reverse, graph simplification, transitive closure, and random graph generation (Erdős–Rényi, DAGs, connected graphs).
- Complete DOT/Graphviz import and export (`toDotString`, `toDotFile`, `fromDotFile`).

---

## 2. Package Architecture

In strict adherence to the project guidelines, the core API is encapsulated inside the `m1graphs2026` package, while executable test runners containing `main()` are located outside the package.


graph-api/
├── README.md
├── doc/                          # Generated HTML Javadoc documentation
│   └── index.html
├── bin/                          # Compiled bytecode output (.class)
├── src/
│   ├── Main.java                 # Entry point (outside package m1graphs2026)
│   ├── TestGraphsPW2.java        # Comprehensive test harness (outside package)
│   ├── isolatedNodes.gv          # DOT file: isolated nodes & simple edge
│   ├── simpleGraph.gv            # DOT file: standard simple directed graph
│   ├── multiGraph.gv             # DOT file: directed multigraph with self-loops
│   ├── weightedSimpleGraph.gv    # DOT file: directed weighted simple graph
│   ├── weightedMultiGraph.gv     # DOT file: directed weighted multigraph
│   ├── undirWeightedMultiGraph.gv# DOT file: undirected weighted multigraph
│   └── m1graphs2026/             # Core API package
│       ├── Node.java             # Node representation, degrees, and successors
│       ├── Edge.java             # Directed/undirected weighted edges
│       ├── Graph.java            # Core directed graph and algorithms
│       ├── UndirectedGraph.java  # Undirected graph specialization
│       ├── NodeColor.java        # Traversal visit state (WHITE, GREY, BLACK)
│       ├── NodeVisitInfo.java    # Traversal metadata (times, predecessor)
│       └── EdgeVisitType.java    # DFS edge classification
└── tests/                        # Modular unit & regression test suites
    ├── EdgeMethodsTest.java
    ├── FirstTenSmokeTest.java
    ├── GraphDotTest.java
    ├── GraphEdgeOperationsTest.java
    ├── GraphNodeManagementTest.java
    ├── GraphRepresentationsTest.java
    ├── GraphTraversalTest.java
    ├── NodeMethodsTest.java
    ├── RandomGraphTest.java
    └── UndirectedGraphTest.java


---

## 3. Compilation & Execution Instructions

### Prerequisites
- **Java Development Kit (JDK) 17** or higher.
- Standard terminal (PowerShell, bash, zsh). No external libraries or build tools (Maven/Gradle) are required.

### 3.1. Compilation
From the project root directory (`graph-api/`), compile the core package, root runners, and test suites into `bin/`:

**On Linux / macOS:**
```bash
# Create bin directory if needed
mkdir -p bin

# Compile package m1graphs2026 and root entry points
javac -d bin src/m1graphs2026/*.java src/*.java

# (Optional) Compile unit test suite
javac -d bin -cp bin -sourcepath "src:tests" tests/*.java
```

**On Windows (PowerShell / Command Prompt):**
```powershell
# Create bin directory if needed
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }

# Compile package m1graphs2026 and root entry points
javac -d bin src/m1graphs2026/*.java src/*.java

# (Optional) Compile unit test suite
javac -d bin -cp bin -sourcepath "src;tests" tests/*.java
```

---

### 3.2. Running the Program

#### Running the Main Entry Point
You can execute either `Main` or `TestGraphsPW2`:
```bash
java -cp bin Main
```
*or directly:*
```bash
java -cp bin TestGraphsPW2
```

#### Running the Unit Test Suites
Each test suite in `tests/` features its own verification `main()` method:
```bash
java -cp bin tests.GraphDotTest
java -cp bin tests.UndirectedGraphTest
java -cp bin tests.GraphTraversalTest
java -cp bin tests.GraphRepresentationsTest
java -cp bin tests.GraphEdgeOperationsTest
java -cp bin tests.GraphNodeManagementTest
java -cp bin tests.NodeMethodsTest
java -cp bin tests.RandomGraphTest
```

---

## 4. What is Tested by `main()` (`TestGraphsPW2.java` / `Main.java`)

The primary `main()` method executes a 5-part verification suite covering all major milestones of the practical work:

1. **Part 1 — Unweighted Directed Graphs**:
   - Dynamic node and edge creation from successor sequences.
   - Node addition (`addNode`), edge addition (`addEdge`), and edge objects (`new Edge(u, v, g)`).
   - In-degree, out-degree, and total degree calculations for simple edges, parallel edges, and self-loops.
   - Cascaded deletion: removing nodes (`removeNode`) and ensuring all incident in-edges and out-edges are properly cleared.
   - Verification of behavior when querying non-existent nodes and absent edges.

2. **Part 2 — Reading Graphs from DOT Files**:
   - Loading and parsing digraph files: `isolatedNodes.gv`, `simpleGraph.gv`, and `multiGraph.gv`.
   - Distinguishing unique successors (`getSuccessors()`) versus multigraph successors (`getSuccessorsMulti()`).
   - Preservation of self-loops and parallel edges during import.

3. **Part 3 — Weighted Directed Graphs**:
   - Reading weighted digraphs from `weightedSimpleGraph.gv` and `weightedMultiGraph.gv`.
   - Parsing attribute blocks (e.g., `[label=4, len=4]`).
   - Correct accumulation of total graph weight across all edges.

4. **Part 4 — Undirected Unweighted Graphs**:
   - Instantiation of `UndirectedGraph` and symmetric edge manipulation (`u -- v`).
   - Verifying that degree calculations, incident edge collections, and neighbor sets are symmetric.
   - Successor array and symmetric adjacency matrix export.
   - Testing graph reversal invariance: verifying that $\text{Reverse}(G) = G$ for undirected graphs.
   - Depth-First Search (DFS) and Breadth-First Search (BFS) traversals on disconnected multigraphs with self-loops.
   - Transitive closure computation on undirected components.

5. **Part 5 — Undirected Weighted Graphs**:
   - Reading undirected weighted graphs from `undirWeightedMultiGraph.gv`.
   - Parsing `--` edge syntax with weight attributes.
   - Total edge weight calculation and verification of single vs. multi-successor lists.

---

## 5. Description of Specific DOT Files (`src/*.gv`)

The project includes six dedicated `.gv` test files in `src/` designed to validate distinct graph topologies and DOT parser edge cases:

| File Name | Graph Type | Characteristics & Test Purpose |
| :--- | :--- | :--- |
| `isolatedNodes.gv` | Directed | Contains single nodes declared on individual lines (`1`, `4`, `5`, `8`) alongside an edge (`2 -> 3`). Tests parser handling of nodes without any incident edges. |
| `simpleGraph.gv` | Directed | Unweighted simple graph (6 nodes, 9 edges). Standard reference graph used for baseline connectivity and degree checks. |
| `multiGraph.gv` | Directed | Multigraph with parallel edges (e.g., `2 -> 4` twice, `3 -> 1` twice, `6 -> 5` twice) and self-loops (`4 -> 4`, `6 -> 6`). Tests multi-successor ordering and loop degree contributions. |
| `weightedSimpleGraph.gv` | Directed | Simple graph with integer weights specified via `[label=W, len=W]` (sum of weights = 27). Tests attribute regex extraction and weight assignments. |
| `weightedMultiGraph.gv` | Directed | Multigraph with distinct weights on parallel edges and self-loops (sum of weights = 117). Tests multiple edge instances between identical endpoints with varying weights. |
| `undirWeightedMultiGraph.gv` | Undirected | Undirected graph using `graph { ... }` header and `--` edges with weight attributes. Tests undirected syntax parsing, symmetric adjacency creation, and weight summation (sum = 117). |

---

## 6. Implementation Notes & Technical Solutions

During development, several key design challenges were addressed:

- **Robust DOT Parsing (`Graph.fromDotFile`)**:
  - The parser flexibly supports inline comments (`//`, `#`, `/* ... */`), optional trailing semicolons, rankdir statements (`rankdir=LR`), isolated node statements, and attribute brackets (`[label=X, len=Y]`).
  - **Path Resolution**: When looking up DOT files, `fromDotFile` checks both the current execution directory (`./`) and the `src/` directory, allowing commands to run seamlessly regardless of working directory.
- **Undirected Graph Consistency (`UndirectedGraph`)**:
  - In `UndirectedGraph`, an edge between $u$ and $v$ is stored symmetrically in `adjEdList` for fast local neighbor queries ($u \to v$ and $v \to u$), but `nbEdges()` and `getAllEdges()` normalize undirected edges such that each physical edge is counted and listed exactly once (with canonical orientation $u \le v$).
  - Self-loops are properly counted in degree calculations (a self-loop contributes 2 to the undirected node degree).
- **Encapsulation & Immutability**:
  - All accessor methods returning lists (e.g., `getSuccessors()`, `getOutEdges()`, `getAllNodes()`) return defensive copies, preventing callers from corrupting the internal graph state.
- **Traversals & Visit Metadata**:
  - Node discovery time, finishing time, and predecessor pointers are stored in decoupled `NodeVisitInfo` objects rather than polluting the core `Node` class, ensuring thread safety and reusability across multiple traversal invocations.

---

## 7. Javadoc Documentation

Comprehensive Javadoc documentation has been generated and is available in the `doc/` directory.  
To view the full API documentation, open:
```text
doc/index.html
```
in any modern web browser.
