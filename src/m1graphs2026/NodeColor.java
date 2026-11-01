package m1graphs2026;

/**
 * Enumeration of visit colors used during graph traversals such as Depth-First Search (DFS).
 * <ul>
 *   <li>{@link #WHITE}: unvisited node.</li>
 *   <li>{@link #GRAY}: currently being explored (in the active recursion/traversal stack).</li>
 *   <li>{@link #BLACK}: fully explored (all outgoing edges examined).</li>
 * </ul>
 */
public enum NodeColor {
    /** Node is not yet visited. */
    WHITE,

    /** Node is currently being visited and on the recursion stack. */
    GRAY,

    /** Node and all its reachable subtree have been fully explored. */
    BLACK;
}
