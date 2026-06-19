import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

public class WelchPowellApp extends JFrame {

    // Theme Configuration
    private static final Color BG_FOREST = new Color(13, 43, 26);
    private static final Color BG_CANOPY = new Color(22, 61, 38);
    private static final Color TEXT_COLOR = new Color(240, 234, 216);
    private static final Color ACCENT_AMBER = new Color(232, 160, 32);
    
    // Dynamic Palette List (Users can add more)
    private List<Color> paletteList = new ArrayList<>(Arrays.asList(
        new Color(232, 93, 74),   // Red
        new Color(74, 158, 237),  // Blue
        new Color(240, 192, 64),  // Yellow
        new Color(107, 196, 106), // Light Green
        new Color(196, 106, 184), // Purple
        new Color(240, 120, 64),  // Orange
        new Color(64, 196, 196),  // Cyan
        new Color(240, 64, 128)   // Pink
    ));

    // State Data
    private List<String> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Map<String, Integer> coloring = new HashMap<>();
    private String processLogText = "Run the algorithm to see the process here.";

    // UI Components
    private JTextField nodeInput;
    private JComboBox<String> edgeFromBox;
    private JComboBox<String> edgeToBox;
    private GraphCanvas canvas;
    private JTextArea resultArea;

    public WelchPowellApp() {
        setTitle("Welch-Powell Graph Coloring");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // --- LEFT PANEL (Controls) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BG_CANOPY);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        leftPanel.setPreferredSize(new Dimension(340, 0));

        // Presets
        addSectionLabel(leftPanel, "⚡ LOAD PRESETS");
        JPanel presetPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        presetPanel.setOpaque(false);
        presetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        presetPanel.add(createStyledButton("🦁 Zoo Layout", e -> loadPreset("zoo"), false));
        presetPanel.add(createStyledButton("🔵 Simple Graph", e -> loadPreset("simple"), false));
        presetPanel.add(createStyledButton("⬡ Mini Petersen", e -> loadPreset("petersen"), false));
        leftPanel.add(presetPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nodes Input
        addSectionLabel(leftPanel, "① NODE / ANIMAL LIST");
        JPanel nodePanel = new JPanel(new BorderLayout(5, 0));
        nodePanel.setOpaque(false);
        nodePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        nodeInput = new JTextField();
        JButton btnAddNode = createStyledButton("+ Add", e -> addNode(), true);
        nodePanel.add(nodeInput, BorderLayout.CENTER);
        nodePanel.add(btnAddNode, BorderLayout.EAST);
        leftPanel.add(nodePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Edges Input
        addSectionLabel(leftPanel, "② CONFLICT RELATIONS (EDGES)");
        JPanel edgePanel = new JPanel(new GridLayout(3, 1, 0, 5));
        edgePanel.setOpaque(false);
        edgePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        edgeFromBox = new JComboBox<>();
        edgeToBox = new JComboBox<>();
        JButton btnAddEdge = createStyledButton("+ Add Conflict", e -> addEdge(), true);
        edgePanel.add(edgeFromBox);
        edgePanel.add(edgeToBox);
        edgePanel.add(btnAddEdge);
        leftPanel.add(edgePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Custom Colors
        addSectionLabel(leftPanel, "③ CUSTOMIZATION");
        JButton btnAddColor = createStyledButton("🎨 Add Custom Color", e -> addCustomColor(), false);
        btnAddColor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        leftPanel.add(btnAddColor);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Execution Actions
        JButton btnRun = createStyledButton("▶ RUN ALGORITHM", e -> runWelchPowell(), true);
        btnRun.setBackground(ACCENT_AMBER);
        btnRun.setForeground(BG_FOREST);
        btnRun.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        leftPanel.add(btnRun);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton btnViewProcess = createStyledButton("🔍 View Process", e -> showProcessDialog(), false);
        btnViewProcess.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        leftPanel.add(btnViewProcess);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnReset = createStyledButton("↺ Reset All", e -> resetAll(), false);
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        leftPanel.add(btnReset);

        // --- RIGHT PANEL (Canvas & Output) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        canvas = new GraphCanvas();
        canvas.setBackground(BG_FOREST);
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(BG_CANOPY);
        resultArea.setForeground(TEXT_COLOR);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollResult = new JScrollPane(resultArea);
        scrollResult.setPreferredSize(new Dimension(0, 180));
        scrollResult.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_AMBER));

        rightPanel.add(canvas, BorderLayout.CENTER);
        rightPanel.add(scrollResult, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    // --- DATA LOGIC ---

    private void addNode() {
        String name = nodeInput.getText().trim();
        if (!name.isEmpty() && !nodes.contains(name)) {
            nodes.add(name);
            nodeInput.setText("");
            updateEdgeCombos();
            canvas.repaint();
        }
    }

    private void addEdge() {
        String a = (String) edgeFromBox.getSelectedItem();
        String b = (String) edgeToBox.getSelectedItem();
        if (a != null && b != null && !a.equals(b)) {
            boolean exists = edges.stream().anyMatch(e -> 
                (e.a.equals(a) && e.b.equals(b)) || (e.a.equals(b) && e.b.equals(a))
            );
            if (!exists) {
                edges.add(new Edge(a, b));
                coloring.clear(); 
                canvas.repaint();
            }
        }
    }

    private void updateEdgeCombos() {
        edgeFromBox.removeAllItems();
        edgeToBox.removeAllItems();
        for (String node : nodes) {
            edgeFromBox.addItem(node);
            edgeToBox.addItem(node);
        }
    }

    private void addCustomColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose a Custom Color", ACCENT_AMBER);
        if (newColor != null) {
            paletteList.add(newColor);
            JOptionPane.showMessageDialog(this, "New color added successfully!", "Color Added", JOptionPane.INFORMATION_MESSAGE);
            if (!coloring.isEmpty()) canvas.repaint(); 
        }
    }

    // --- WELCH-POWELL ALGORITHM ---
    
    private void runWelchPowell() {
        if (nodes.isEmpty()) return;
        coloring.clear();
        StringBuilder log = new StringBuilder("=== WELCH-POWELL ALGORITHM PROCESS ===\n\n");

        // 1. Calculate Degree & Build Adjacency List (For detailed logging)
        Map<String, Integer> degree = new HashMap<>();
        Map<String, List<String>> adjacencyList = new HashMap<>();
        
        for (String n : nodes) {
            degree.put(n, 0);
            adjacencyList.put(n, new ArrayList<>());
        }
        
        for (Edge e : edges) {
            degree.put(e.a, degree.get(e.a) + 1);
            degree.put(e.b, degree.get(e.b) + 1);
            adjacencyList.get(e.a).add(e.b);
            adjacencyList.get(e.b).add(e.a);
        }

        log.append("[Step 1] Calculate Node Degrees (Counting connections):\n");
        for (String n : nodes) {
            log.append("- ").append(n).append(" connects to: [")
               .append(String.join(", ", adjacencyList.get(n)))
               .append("] -> Degree = ").append(degree.get(n)).append("\n");
        }
        log.append("\n");

        // 2. Sort Descending
        List<String> sortedNodes = new ArrayList<>(nodes);
        sortedNodes.sort((n1, n2) -> degree.get(n2) - degree.get(n1));
        
        log.append("[Step 2] Sort Nodes by Degree (Descending):\n");
        for (int i = 0; i < sortedNodes.size(); i++) {
            String n = sortedNodes.get(i);
            log.append((i + 1)).append(". ").append(n).append(" (Degree: ").append(degree.get(n)).append(")\n");
        }
        log.append("\n");

        // 3. Coloring (First-Fit)
        log.append("[Step 3] Assigning Colors (Enclosures):\n");
        for (String node : sortedNodes) {
            if (coloring.containsKey(node)) continue;

            Set<Integer> forbidden = new HashSet<>();
            for (Edge e : edges) {
                if (e.a.equals(node) && coloring.containsKey(e.b)) forbidden.add(coloring.get(e.b));
                if (e.b.equals(node) && coloring.containsKey(e.a)) forbidden.add(coloring.get(e.a));
            }

            int c = 0;
            while (forbidden.contains(c)) c++;
            coloring.put(node, c);
            log.append("Assigned Color/Enclosure ").append(c + 1).append(" to: ").append(node).append("\n");

            for (String other : sortedNodes) {
                if (coloring.containsKey(other)) continue;
                Set<Integer> otherForbidden = new HashSet<>();
                for (Edge e : edges) {
                    if (e.a.equals(other) && coloring.containsKey(e.b)) otherForbidden.add(coloring.get(e.b));
                    if (e.b.equals(other) && coloring.containsKey(e.a)) otherForbidden.add(coloring.get(e.a));
                }
                if (!otherForbidden.contains(c)) {
                    coloring.put(other, c);
                    log.append("  -> Also assigned Color/Enclosure ").append(c + 1).append(" to non-adjacent node: ").append(other).append("\n");
                }
            }
        }
        
        processLogText = log.toString();

        // Print Final Result to Bottom Panel
        StringBuilder summary = new StringBuilder("=== FINAL RESULTS ===\n");
        int maxColor = coloring.values().stream().max(Integer::compare).orElse(0);
        summary.append("Total Enclosures Needed: ").append(maxColor + 1).append("\n\n");
        
        for (int i = 0; i <= maxColor; i++) {
            summary.append("Enclosure ").append(i + 1).append(": ");
            List<String> group = new ArrayList<>();
            for (String n : nodes) {
                if (coloring.get(n) == i) group.add(n);
            }
            summary.append(String.join(", ", group)).append("\n");
        }

        resultArea.setText(summary.toString());
        canvas.repaint();
    }

    private void showProcessDialog() {
        JTextArea logArea = new JTextArea(processLogText);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(650, 450)); // Diperlebar agar perhitungan ketetanggaan muat
        
        JOptionPane.showMessageDialog(this, scrollPane, "Algorithm Process Steps", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- HELPER & PRESETS ---

    private void resetAll() {
        nodes.clear();
        edges.clear();
        coloring.clear();
        updateEdgeCombos();
        resultArea.setText("");
        processLogText = "Run the algorithm to see the process here.";
        canvas.repaint();
    }

    private void loadPreset(String name) {
        resetAll();
        if (name.equals("zoo")) {
            String[] n = {"Lion", "Deer", "Zebra", "Tiger", "Mouse Deer", "Elephant", "Crocodile"};
            nodes.addAll(Arrays.asList(n));
            edges.add(new Edge("Lion", "Deer")); edges.add(new Edge("Lion", "Zebra"));
            edges.add(new Edge("Lion", "Tiger")); edges.add(new Edge("Lion", "Mouse Deer"));
            edges.add(new Edge("Tiger", "Deer")); edges.add(new Edge("Tiger", "Mouse Deer"));
            edges.add(new Edge("Tiger", "Zebra")); edges.add(new Edge("Crocodile", "Deer"));
            edges.add(new Edge("Crocodile", "Mouse Deer")); edges.add(new Edge("Zebra", "Deer"));
        } else if (name.equals("simple")) {
            String[] n = {"A", "B", "C", "D", "E"};
            nodes.addAll(Arrays.asList(n));
            edges.add(new Edge("A", "B")); edges.add(new Edge("A", "C"));
            edges.add(new Edge("B", "C")); edges.add(new Edge("B", "D"));
            edges.add(new Edge("C", "E")); edges.add(new Edge("D", "E"));
        } else if (name.equals("petersen")) {
            String[] n = {"V1", "V2", "V3", "V4", "V5", "V6"};
            nodes.addAll(Arrays.asList(n));
            edges.add(new Edge("V1","V2")); edges.add(new Edge("V2","V3"));
            edges.add(new Edge("V3","V4")); edges.add(new Edge("V4","V5"));
            edges.add(new Edge("V5","V1")); edges.add(new Edge("V1","V3"));
            edges.add(new Edge("V2","V4")); edges.add(new Edge("V3","V5"));
            edges.add(new Edge("V4","V1")); edges.add(new Edge("V5","V2"));
            edges.add(new Edge("V1","V6")); edges.add(new Edge("V3","V6"));
        }
        updateEdgeCombos();
        canvas.repaint();
    }

    private void addSectionLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(ACCENT_AMBER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private JButton createStyledButton(String text, java.awt.event.ActionListener action, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(isPrimary ? ACCENT_AMBER : BG_FOREST);
        btn.setForeground(isPrimary ? BG_FOREST : TEXT_COLOR);
        btn.addActionListener(action);
        return btn;
    }

    // --- CANVAS GRAPH DRAWING ---
    
    private class GraphCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (nodes.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r = (int) (Math.min(cx, cy) * 0.7);
            
            // Map Positions
            Map<String, Point> posMap = new HashMap<>();
            for (int i = 0; i < nodes.size(); i++) {
                double angle = (2 * Math.PI * i / nodes.size()) - (Math.PI / 2);
                int x = (int) (cx + r * Math.cos(angle));
                int y = (int) (cy + r * Math.sin(angle));
                posMap.put(nodes.get(i), new Point(x, y));
            }

            // Draw Edges
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(45, 74, 56));
            for (Edge e : edges) {
                Point p1 = posMap.get(e.a);
                Point p2 = posMap.get(e.b);
                if (p1 != null && p2 != null) {
                    g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                }
            }

            // Draw Nodes
            int nodeRadius = 25;
            for (String n : nodes) {
                Point p = posMap.get(n);
                if (p == null) continue;

                boolean hasColor = coloring.containsKey(n);
                
                // Fetch color from the dynamic palette list
                Color nodeColor;
                if (hasColor) {
                    int colorIndex = coloring.get(n) % paletteList.size();
                    nodeColor = paletteList.get(colorIndex);
                } else {
                    nodeColor = new Color(30, 94, 56);
                }

                g2.setColor(nodeColor);
                g2.fill(new Ellipse2D.Double(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2));
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(hasColor ? 3 : 1));
                g2.draw(new Ellipse2D.Double(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2));

                // Determine contrast text color (black/white) based on background luminance
                double luminance = (0.299 * nodeColor.getRed() + 0.587 * nodeColor.getGreen() + 0.114 * nodeColor.getBlue()) / 255;
                g2.setColor(luminance > 0.5 ? Color.BLACK : Color.WHITE);
                
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                
                String label = n;
                if (fm.stringWidth(n) > nodeRadius * 2 - 4) {
                    label = n.substring(0, Math.min(n.length(), 3)) + ".";
                }
                
                int textX = p.x - (fm.stringWidth(label) / 2);
                int textY = p.y + (fm.getAscent() / 2) - 2;
                g2.drawString(label, textX, textY);
            }
        }
    }

    private static class Edge {
        String a, b;
        public Edge(String a, String b) { this.a = a; this.b = b; }
    }

    // --- MAIN EXECUTION ---
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new WelchPowellApp().setVisible(true);
        });
    }
}