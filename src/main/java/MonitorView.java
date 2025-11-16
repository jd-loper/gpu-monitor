import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MonitorView extends JFrame {
    private JTextArea textArea;
    private JButton pauseButton;
    private JButton copyButton;
    private JPanel chartPanel;
    private JPanel tempChart;

    public MonitorView() {
        setTitle("GPU Monitor");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setBackground(new Color(40, 42, 54));
        textArea.setForeground(new Color(80, 250, 123));
        textArea.setCaretColor(new Color(40, 42, 54));
        textArea.setEditable(false);
        UIManager.put("Component.focusWidth", 0);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JLabel label = new JLabel("NVIDIA GPU Monitor", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setPreferredSize(new Dimension(450, 400));

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(scrollPane);
        mainPanel.add(chartPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        copyButton = new JButton("Copy to Clipboard");
        buttonPanel.add(copyButton);
        copyButton.putClientProperty("JButton.buttonType", "roundRect");

        pauseButton = new JButton("Pause");
        buttonPanel.add(pauseButton);
        pauseButton.putClientProperty("JButton.buttonType", "roundRect");

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setStatsText(String stats) {
        this.textArea.setText(stats);
        this.textArea.setCaretPosition(0);
    }

    public void setChartPanel(JPanel panel) {
        this.tempChart = panel;
        this.chartPanel.add(tempChart);

    }

    public void setPauseButtonText(String text) {
        this.pauseButton.setText(text);
    }

    public void addCopyListener(ActionListener listener) {
        this.copyButton.addActionListener(listener);
    }

    public void addPauseListener(ActionListener listener) {
        this.pauseButton.addActionListener(listener);
    }
}
