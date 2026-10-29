import m1graphs2026.Edge;
import m1graphs2026.Graph;
import m1graphs2026.Node;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Small Swing interface demonstrating the first ten Node API methods. */
public final class GraphDemoGUI {
    private final Graph graph = new Graph();
    private final GraphCanvas canvas = new GraphCanvas();
    private final JTextArea details = new JTextArea();
    private final JTextField nodeIdInput = new JTextField(5);
    private final JTextField fromInput = new JTextField(4);
    private final JTextField toInput = new JTextField(4);
    private Integer selectedNodeId = null;

    private GraphDemoGUI() {
        canvas.setGraph(graph);
        loadSampleGraph();

        JFrame frame = new JFrame("M1 Graphs — First 10 Node API Methods");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JPanel controls = new JPanel();
        controls.add(new JLabel("New node ID:"));
        controls.add(nodeIdInput);
        JButton addNodeButton = new JButton("Add node");
        addNodeButton.addActionListener(event -> addNodeFromInput());
        controls.add(addNodeButton);

        controls.add(new JLabel("Edge from:"));
        controls.add(fromInput);
        controls.add(new JLabel("to:"));
        controls.add(toInput);
        JButton addEdgeButton = new JButton("Add edge");
        addEdgeButton.addActionListener(event -> addEdgeFromInput());
        controls.add(addEdgeButton);

        JButton sampleButton = new JButton("Load sample");
        sampleButton.addActionListener(event -> loadSampleGraph());
        controls.add(sampleButton);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearGraph());
        controls.add(clearButton);

        canvas.setPreferredSize(new Dimension(720, 560));
        canvas.setBorder(BorderFactory.createTitledBorder("Directed graph — click a node to inspect it"));

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        details.setLineWrap(true);
        details.setWrapStyleWord(false);
        details.setText("Select a node in the drawing to inspect its methods.");
        JScrollPane detailsScroll = new JScrollPane(details);
        detailsScroll.setPreferredSize(new Dimension(365, 560));
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Node API output"));

        frame.add(controls, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(detailsScroll, BorderLayout.EAST);
        frame.setMinimumSize(new Dimension(1080, 650));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.setSelectionHandler(nodeId -> {
            selectedNodeId = nodeId;
            refreshDetails();
        });
        selectFirstNode();
    }

    private void loadSampleGraph() {
        graph.clear();
        // The graph example from the assignment's successor-array example.
        Graph sample = new Graph(2, 4, 0, 0, 6, 0, 2, 3, 5, 8, 0,
                0, 4, 7, 0, 3, 0, 7, 0);
        for (Node node : sample.getAllNodes()) {
            graph.addNode(node.getId());
        }
        for (Edge edge : sample.getAllEdges()) {
            graph.addEdge(edge.from().getId(), edge.to().getId());
        }
        selectFirstNode();
        refreshView();
    }

    private void addNodeFromInput() {
        try {
            int id = Integer.parseInt(nodeIdInput.getText().trim());
            if (!graph.addNode(id)) {
                showError("Node " + id + " already exists.");
                return;
            }
            selectedNodeId = id;
            nodeIdInput.setText("");
            refreshView();
        } catch (NumberFormatException exception) {
            showError("Enter a valid integer for the node ID.");
        }
    }

    private void addEdgeFromInput() {
        try {
            int fromId = Integer.parseInt(fromInput.getText().trim());
            int toId = Integer.parseInt(toInput.getText().trim());
            // This demo's addEdge(id,id) creates either endpoint if missing.
            graph.addEdge(fromId, toId);
            selectedNodeId = fromId;
            fromInput.setText("");
            toInput.setText("");
            refreshView();
        } catch (NumberFormatException exception) {
            showError("Enter integer IDs in both edge fields.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void clearGraph() {
        graph.clear();
        selectedNodeId = null;
        refreshView();
    }

    private void selectFirstNode() {
        List<Node> nodes = graph.getAllNodes();
        selectedNodeId = nodes.isEmpty() ? null : nodes.get(0).getId();
    }

    private void refreshView() {
        canvas.repaint();
        refreshDetails();
    }

    private void refreshDetails() {
        if (selectedNodeId == null || graph.getNode(selectedNodeId) == null) {
            details.setText("No node selected.\n\nAdd a node or load the sample graph.");
            canvas.setSelectedId(null);
            return;
        }
        Node node = graph.getNode(selectedNodeId);
        canvas.setSelectedId(selectedNodeId);
        StringBuilder text = new StringBuilder();
        text.append("Graph summary\n")
            .append("-------------\n")
            .append("Nodes: ").append(graph.nbNodes()).append('\n')
            .append("Edges: ").append(graph.nbEdges()).append(" (parallel edges counted)\n\n")
            .append("Selected node: ").append(node.getId()).append("\n\n")
            .append("1. getId()\n   ").append(node.getId()).append("\n\n")
            .append("2. getGraph()\n   Returns this graph object\n\n")
            .append("3. getName()\n   ").append(node.getName() == null ? "null (unnamed)" : node.getName()).append("\n\n")
            .append("4. getSuccessors()\n   ").append(node.getSuccessors()).append("\n\n")
            .append("5. getSuccessorsMulti()\n   ").append(node.getSuccessorsMulti()).append("\n\n")
            .append("6. adjacent(Node/int)\n");
        List<Node> allNodes = graph.getAllNodes();
        if (allNodes.size() <= 1) {
            text.append("   No other nodes to compare\n");
        } else {
            for (Node other : allNodes) {
                if (!other.equals(node)) {
                    text.append("   adjacent(").append(other.getId()).append(") = ")
                        .append(node.adjacent(other.getId())).append('\n');
                }
            }
        }
        text.append("\n7. inDegree()\n   ").append(node.inDegree()).append("\n\n")
            .append("8. outDegree()\n   ").append(node.outDegree()).append("\n\n")
            .append("9. degree() = in + out\n   ").append(node.degree()).append("\n\n")
            .append("10. getOutEdges()\n   ").append(node.getOutEdges()).append("\n\n")
            .append("Outgoing edges are listed in ID order.\n")
            .append("Self-loops count once in-degree and once out-degree.\n")
            .append("Parallel edges count separately for degree and in successorsMulti().\n");
        details.setText(text.toString());
        details.setCaretPosition(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Input problem", JOptionPane.WARNING_MESSAGE);
    }

    @SuppressWarnings("serial")
    private static final class GraphCanvas extends JPanel {
        private Graph graph;
        private Integer selectedId;
        private java.util.function.Consumer<Integer> selectionHandler;
        private final Map<Integer, Point> positions = new HashMap<>();
        private static final int NODE_RADIUS = 22;

        GraphCanvas() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    Integer nearestId = null;
                    double nearestDistance = Double.MAX_VALUE;
                    for (Map.Entry<Integer, Point> entry : positions.entrySet()) {
                        double distance = entry.getValue().distance(event.getPoint());
                        if (distance <= NODE_RADIUS + 5 && distance < nearestDistance) {
                            nearestId = entry.getKey();
                            nearestDistance = distance;
                        }
                    }
                    if (nearestId != null && selectionHandler != null) {
                        selectionHandler.accept(nearestId);
                    }
                }
            });
        }

        void setGraph(Graph graph) { this.graph = graph; }
        void setSelectedId(Integer selectedId) { this.selectedId = selectedId; repaint(); }
        void setSelectionHandler(java.util.function.Consumer<Integer> handler) { this.selectionHandler = handler; }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (graph == null || graph.nbNodes() == 0) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("Graph is empty — add a node or load the sample.", 25, 35);
                positions.clear();
                return;
            }
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            calculatePositions();
            drawEdges(g2);
            drawNodes(g2);
            g2.dispose();
        }

        private void calculatePositions() {
            positions.clear();
            List<Node> nodes = graph.getAllNodes();
            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
            double radius = Math.max(45.0, Math.min(getWidth(), getHeight()) * 0.36);
            for (int i = 0; i < nodes.size(); i++) {
                double angle = -Math.PI / 2.0 + 2.0 * Math.PI * i / nodes.size();
                int x = (int) Math.round(centerX + radius * Math.cos(angle));
                int y = (int) Math.round(centerY + radius * Math.sin(angle));
                positions.put(nodes.get(i).getId(), new Point(x, y));
            }
        }

        private void drawEdges(Graphics2D g2) {
            List<Edge> edges = graph.getAllEdges();
            Map<String, Integer> parallelIndex = new HashMap<>();
            for (Edge edge : edges) {
                Point from = positions.get(edge.from().getId());
                Point to = positions.get(edge.to().getId());
                if (from == null || to == null) continue;
                String key = edge.from().getId() + ":" + edge.to().getId();
                int offsetIndex = parallelIndex.getOrDefault(key, 0);
                parallelIndex.put(key, offsetIndex + 1);
                g2.setColor(new Color(90, 100, 115));
                g2.setStroke(new BasicStroke(1.7f));
                if (from.equals(to)) {
                    int x = from.x - 13;
                    int y = from.y - NODE_RADIUS - 20 - offsetIndex * 7;
                    g2.drawArc(x, y, 27 + offsetIndex * 7, 30 + offsetIndex * 7, 20, 300);
                    drawArrowHead(g2, from.x + 9, from.y - NODE_RADIUS + 1, -0.3, 1.0);
                } else {
                    double dx = to.x - from.x;
                    double dy = to.y - from.y;
                    double length = Math.max(1.0, Math.hypot(dx, dy));
                    double ux = dx / length;
                    double uy = dy / length;
                    double startX = from.x + ux * NODE_RADIUS;
                    double startY = from.y + uy * NODE_RADIUS;
                    double endX = to.x - ux * (NODE_RADIUS + 4);
                    double endY = to.y - uy * (NODE_RADIUS + 4);
                    if (offsetIndex == 0) {
                        g2.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
                    } else {
                        double perpX = -uy;
                        double perpY = ux;
                        double shift = (offsetIndex % 2 == 1 ? 1 : -1) * ((offsetIndex + 1) / 2.0) * 15;
                        double controlX = (startX + endX) / 2.0 + perpX * shift;
                        double controlY = (startY + endY) / 2.0 + perpY * shift;
                        QuadCurve2D curve = new QuadCurve2D.Double(startX, startY, controlX, controlY, endX, endY);
                        g2.draw(curve);
                    }
                    drawArrowHead(g2, endX, endY, ux, uy);
                    if (edge.isWeighted()) {
                        g2.setColor(new Color(40, 50, 60));
                        g2.drawString(String.valueOf(edge.getWeight()), (int) ((startX + endX) / 2), (int) ((startY + endY) / 2) - 5);
                    }
                }
            }
        }

        private void drawArrowHead(Graphics2D g2, double x, double y, double ux, double uy) {
            double size = 9.0;
            double baseX = x - ux * size;
            double baseY = y - uy * size;
            double perpX = -uy;
            double perpY = ux;
            int[] xs = {(int) x, (int) (baseX + perpX * size * 0.55), (int) (baseX - perpX * size * 0.55)};
            int[] ys = {(int) y, (int) (baseY + perpY * size * 0.55), (int) (baseY - perpY * size * 0.55)};
            g2.fillPolygon(xs, ys, 3);
        }

        private void drawNodes(Graphics2D g2) {
            for (Node node : graph.getAllNodes()) {
                Point point = positions.get(node.getId());
                if (point == null) continue;
                boolean selected = selectedId != null && selectedId == node.getId();
                g2.setColor(selected ? new Color(255, 224, 140) : new Color(220, 235, 250));
                g2.fillOval(point.x - NODE_RADIUS, point.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g2.setColor(selected ? new Color(160, 95, 0) : new Color(45, 75, 110));
                g2.setStroke(new BasicStroke(selected ? 2.8f : 1.6f));
                g2.drawOval(point.x - NODE_RADIUS, point.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                String label = Integer.toString(node.getId());
                int labelWidth = g2.getFontMetrics().stringWidth(label);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(label, point.x - labelWidth / 2, point.y + 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphDemoGUI app = new GraphDemoGUI();
            app.canvas.setGraph(app.graph);
            app.refreshView();
        });
    }
}
