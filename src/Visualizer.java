import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class Visualizer {
    private Graph graph = new Graph();
    private GraphPanel graphPanel;
    private Timer dfsTimer;
    private LinkedList<String> dfsPath;

    public Visualizer() {
        Point positionA = new Point(300, 100);
        Point positionB = new Point((int) ((Math.random() * 300)+300), (int) (Math.random() * 400)+100);
        Point positionC = new Point((int) ((Math.random() * 300)+300), (int) (Math.random() * 400)+100);
        Point positionD = new Point((int) ((Math.random() * 300)+300), (int) (Math.random() * 400)+100);
        Point positionE = new Point((int) (Math.random() * 300), (int) (Math.random() * 400)+100);
        Point positionF = new Point((int) (Math.random() * 300), (int) (Math.random() * 400)+100);

        graph.addVertex(new Vertex("A", positionA));
        graph.addVertex(new Vertex("B", positionB));
        graph.addVertex(new Vertex("C", positionC));
        graph.addVertex(new Vertex("D", positionD));
        graph.addVertex(new Vertex("E", positionE));
        graph.addVertex(new Vertex("F", positionF));

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("D", "A");
        graph.addEdge("A", "E");
        graph.addEdge("E", "F");
        graph.addEdge("F", "B");
        graph.addEdge("B", "D");

        graphPanel = new GraphPanel(graph);

        dfsPath = new LinkedList<>();
        dfsTimer = new Timer(1000, e -> animateDFS());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Visualizer().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        JFrame window = new JFrame();

        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize(new Dimension(600, 500));
        westPanel.setLayout(new BorderLayout());
        westPanel.setBackground(Color.BLUE);

        JPanel eastPanelCenter = new JPanel();
        eastPanelCenter.setLayout(new BoxLayout(eastPanelCenter, BoxLayout.PAGE_AXIS));

        JButton dfsButton = new JButton("DFS");
        dfsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDFS();
            }
        });

        JButton bfsButton = new JButton("BFS");
        bfsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preformBFS();
            }
        });

        JButton reloadButton = new JButton("Reload");
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.reload();
            }
        });
        eastPanelCenter.add(dfsButton);
        eastPanelCenter.add(bfsButton);
        eastPanelCenter.add(reloadButton);
        westPanel.add(graphPanel, BorderLayout.CENTER);

        window.setSize(800, 500);
        window.setLayout(new BorderLayout());

        window.setResizable(true);
        window.add(westPanel, BorderLayout.WEST);
        window.add(eastPanelCenter);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private void performDFS() {
        graphPanel.clearNodeColors();
        dfsPath.clear();
        graph.depthFirstSearch("A", "D", dfsPath);
        System.out.println("DFS Path: " + dfsPath);
        dfsTimer.start();
    }

    private void animateDFS() {
        if (!dfsPath.isEmpty()) {
            String vertexToHighlight = dfsPath.remove(0);
            graphPanel.highlightVertex(vertexToHighlight);
            graphPanel.repaint();
        } else {
            dfsTimer.stop();
        }
        System.out.println(dfsPath);
    }

    private void preformBFS(){
        graphPanel.clearNodeColors();
        dfsPath.clear();
        graph.breadthFirstSearch("A", "D", dfsPath);

        if (!dfsPath.isEmpty()) {
            animateBFS(dfsPath);
        } else {
            System.out.println("No path w/ BFS.");
        }
    }

    private void animateBFS(LinkedList<String> bfsPath) {
        Timer bfsTimer = new Timer(1000, e -> {
            if (!bfsPath.isEmpty()) {
                String vertexToHighlight = bfsPath.remove();
                graphPanel.highlightVertex(vertexToHighlight);
                graphPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        bfsTimer.start();
    }


    private static class GraphPanel extends JPanel {
        private Graph graph;
        private List<String> highlightedVertices;

        public GraphPanel(Graph graph) {
            this.graph = graph;
            setPreferredSize(new Dimension(600, 500));
            highlightedVertices = new ArrayList<>();
        }

        public void highlightVertex(String vertex) {
            highlightedVertices.add(vertex);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            double scale = 0.8;
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(scale, scale);

            for (Vertex v : graph.vertices.values()) {
                Point position = v.position;

                for (Vertex neighbor : v.neighbors) {
                    Point neighborPosition = neighbor.position;

                    if (neighborPosition != null) {

                        g.drawLine(position.x + 15, position.y + 15, neighborPosition.x + 15, neighborPosition.y + 15);

                        int midX = (position.x + neighborPosition.x) / 2;
                        int midY = (position.y + neighborPosition.y) / 2;

                        g.setColor(Color.BLACK);
                        int weight = calculateWeight(v.label, neighbor.label);
                        g.drawString(Integer.toString(weight), midX, midY);
                    }
                }

                if (highlightedVertices.contains(v.label)) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }

                g.fillOval(position.x, position.y, 30, 30);

                g.setColor(Color.BLACK);
                g.drawString(v.label, position.x + 10, position.y + 20);
            }
        }

        private int calculateWeight(String label1, String label2) {
            Vertex v1 = graph.vertices.get(label1);
            Vertex v2 = graph.vertices.get(label2);

            if (v1 != null && v2 != null) {
                int dx = v2.position.x - v1.position.x;
                int dy = v2.position.y - v1.position.y;

                // using euclidean distance
                // https://www.cuemath.com/euclidean-distance-formula/
                double distance = Math.sqrt(dx * dx + dy * dy);

                return (int) (distance / 10);
            }

            return 1;
        }

        public void clearNodeColors() {
            highlightedVertices.clear();
            repaint();

        }

        public void reload() {
            for (Vertex v : graph.vertices.values()) {
                int maxX = 600;
                int maxY = 500;
                int minX = 0;
                int minY = 0;

                int newX = (int) (Math.random() * (maxX - minX) + minX);
                int newY = (int) (Math.random() * (maxY - minY) + minY);

                v.position = new Point(newX, newY);
            }

            clearNodeColors();
            repaint();
        }
    }
}

