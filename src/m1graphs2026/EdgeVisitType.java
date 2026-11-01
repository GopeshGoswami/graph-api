package m1graphs2026;

/**
 * Classification of edges encountered during Depth-First Search (DFS) graph traversal.
 * <ul>
 *   <li>{@link #TREE}: edge leading to an unvisited (WHITE) node in the DFS tree.</li>
 *   <li>{@link #BACKWARD}: edge leading to an active ancestor (GRAY) node (indicates a cycle).</li>
 *   <li>{@link #FORWARD}: nontree edge leading from an ancestor to a finished descendant (BLACK) node.</li>
 *   <li>{@link #CROSS}: edge leading to a finished (BLACK) node in a different branch or subtree.</li>
 * </ul>
 */
public enum EdgeVisitType {
    /** Tree edge leading to an unvisited node discovered during DFS. */
    TREE,

    /** Backward edge leading to an active ancestor node on the DFS call stack. */
    BACKWARD,

    /** Forward edge leading from an ancestor to a completed descendant node. */
    FORWARD,

    /** Cross edge leading between different branches of the DFS forest. */
    CROSS;
}
