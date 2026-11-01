package m1graphs2026;

import java.util.Objects;

/**
 * Encapsulates visit information collected for a graph node during Depth-First Search (DFS) traversal.
 * Stores the node's visit {@link NodeColor}, its predecessor node in the DFS tree,
 * its discovery timestamp, and its finished timestamp.
 */
public class NodeVisitInfo {

    private NodeColor color;
    private Node predecessor;
    private Integer discovery;
    private Integer finished;

    /**
     * Constructs a default {@code NodeVisitInfo} with color {@link NodeColor#WHITE}
     * and null predecessor and timestamps.
     */
    public NodeVisitInfo() {
        this(NodeColor.WHITE, null, null, null);
    }
    //——————

    /**
     * Constructs a {@code NodeVisitInfo} with the specified color and null predecessor and timestamps.
     *
     * @param color the initial visit color of the node
     */
    public NodeVisitInfo(NodeColor color) {
        this(color, null, null, null);
    }
    //——————

    /**
     * Constructs a {@code NodeVisitInfo} with full visit details.
     *
     * @param color the visit color of the node
     * @param predecessor the predecessor node in the traversal, or {@code null} if none
     * @param discovery the discovery timestamp, or {@code null} if not yet discovered
     * @param finished the finish timestamp, or {@code null} if not yet finished
     */
    public NodeVisitInfo(NodeColor color, Node predecessor, Integer discovery, Integer finished) {
        this.color = color;
        this.predecessor = predecessor;
        this.discovery = discovery;
        this.finished = finished;
    }
    //——————

    /**
     * Gets the current visit color of the node.
     *
     * @return the {@link NodeColor}
     */
    public NodeColor getColor() {
        return color;
    }
    //——————

    /**
     * Sets the visit color of the node.
     *
     * @param color the new {@link NodeColor}
     */
    public void setColor(NodeColor color) {
        this.color = color;
    }
    //——————

    /**
     * Gets the predecessor node in the traversal tree.
     *
     * @return the predecessor {@link Node}, or {@code null} if root or unvisited
     */
    public Node getPredecessor() {
        return predecessor;
    }
    //——————

    /**
     * Sets the predecessor node in the traversal tree.
     *
     * @param predecessor the predecessor {@link Node}
     */
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }
    //——————

    /**
     * Gets the discovery timestamp of the node.
     *
     * @return the discovery timestamp, or {@code null} if unvisited
     */
    public Integer getDiscovery() {
        return discovery;
    }
    //——————

    /**
     * Sets the discovery timestamp of the node.
     *
     * @param discovery the discovery timestamp
     */
    public void setDiscovery(Integer discovery) {
        this.discovery = discovery;
    }
    //——————

    /**
     * Gets the discovery timestamp of the node (alias for {@link #getDiscovery()}).
     *
     * @return the discovery timestamp, or {@code null} if unvisited
     */
    public Integer getDiscoveryTime() {
        return getDiscovery();
    }
    //——————

    /**
     * Sets the discovery timestamp of the node (alias for {@link #setDiscovery(Integer)}).
     *
     * @param discovery the discovery timestamp
     */
    public void setDiscoveryTime(Integer discovery) {
        setDiscovery(discovery);
    }
    //——————

    /**
     * Gets the finish timestamp of the node.
     *
     * @return the finish timestamp, or {@code null} if unfinished
     */
    public Integer getFinished() {
        return finished;
    }
    //——————

    /**
     * Sets the finish timestamp of the node.
     *
     * @param finished the finish timestamp
     */
    public void setFinished(Integer finished) {
        this.finished = finished;
    }
    //——————

    /**
     * Gets the finish timestamp of the node (alias for {@link #getFinished()}).
     *
     * @return the finish timestamp, or {@code null} if unfinished
     */
    public Integer getFinishedTime() {
        return getFinished();
    }
    //——————

    /**
     * Sets the finish timestamp of the node (alias for {@link #setFinished(Integer)}).
     *
     * @param finished the finish timestamp
     */
    public void setFinishedTime(Integer finished) {
        setFinished(finished);
    }
    //——————

    /**
     * Gets the finish timestamp of the node (alias for {@link #getFinished()}).
     *
     * @return the finish timestamp, or {@code null} if unfinished
     */
    public Integer getFinishTime() {
        return getFinished();
    }
    //——————

    /**
     * Sets the finish timestamp of the node (alias for {@link #setFinished(Integer)}).
     *
     * @param finished the finish timestamp
     */
    public void setFinishTime(Integer finished) {
        setFinished(finished);
    }
    //——————

    /**
     * Indicates whether another object is equal to this visit information.
     *
     * @param obj object to compare
     * @return {@code true} if all visit information fields are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NodeVisitInfo other)) return false;
        return color == other.color
                && Objects.equals(predecessor, other.predecessor)
                && Objects.equals(discovery, other.discovery)
                && Objects.equals(finished, other.finished);
    }
    //——————

    /**
     * Returns a hash code value for this visit information.
     *
     * @return a hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, predecessor, discovery, finished);
    }
    //——————

    /**
     * Returns a string representation of this visit information.
     *
     * @return a string describing the color, predecessor, discovery, and finished timestamps
     */
    @Override
    public String toString() {
        return "NodeVisitInfo{"
                + "color=" + color
                + ", predecessor=" + (predecessor != null ? predecessor.getId() : "null")
                + ", discovery=" + discovery
                + ", finished=" + finished
                + '}';
    }
    //——————
}
