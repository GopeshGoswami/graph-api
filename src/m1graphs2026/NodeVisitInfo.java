package m1graphs2026;

import java.util.Objects;

public class NodeVisitInfo {

    private NodeColor color;
    private Node predecessor;
    private Integer discovery;
    private Integer finished;

    public NodeVisitInfo() {
        this(NodeColor.WHITE, null, null, null);
    }
   
    public NodeVisitInfo(NodeColor color) {
        this(color, null, null, null);
    }
   
    public NodeVisitInfo(NodeColor color, Node predecessor, Integer discovery, Integer finished) {
        this.color = color;
        this.predecessor = predecessor;
        this.discovery = discovery;
        this.finished = finished;
    }
   
    public NodeColor getColor() {
        return color;
    }
    
    public void setColor(NodeColor color) {
        this.color = color;
    }
    
    public Node getPredecessor() {
        return predecessor;
    }
    
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }
    
    public Integer getDiscovery() {
        return discovery;
    }
    
    public void setDiscovery(Integer discovery) {
        this.discovery = discovery;
    }
    
    public Integer getDiscoveryTime() {
        return getDiscovery();
    }
    
    public void setDiscoveryTime(Integer discovery) {
        setDiscovery(discovery);
    }
    
    public Integer getFinished() {
        return finished;
    }
    
    public void setFinished(Integer finished) {
        this.finished = finished;
    }
    
    public Integer getFinishedTime() {
        return getFinished();
    }
    
    public void setFinishedTime(Integer finished) {
        setFinished(finished);
    }
    
    public Integer getFinishTime() {
        return getFinished();
    }
    
    public void setFinishTime(Integer finished) {
        setFinished(finished);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NodeVisitInfo other)) return false;
        return color == other.color
                && Objects.equals(predecessor, other.predecessor)
                && Objects.equals(discovery, other.discovery)
                && Objects.equals(finished, other.finished);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(color, predecessor, discovery, finished);
    }
    
    @Override
    public String toString() {
        return "NodeVisitInfo{"
                + "color=" + color
                + ", predecessor=" + (predecessor != null ? predecessor.getId() : "null")
                + ", discovery=" + discovery
                + ", finished=" + finished
                + '}';
    }
}
