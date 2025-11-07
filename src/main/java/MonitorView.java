import javax.swing.*;
import java.awt.*;

public class MonitorView extends JFrame {
    private JTextArea textArea;
    private JButton pauseButton;
    private JButton copyButton;

    public MonitorView() {
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setBackground(new Color(40, 42, 54));
        textArea.setForeground(new Color(80, 250, 123));
        textArea.setCaretColor(new Color(40, 42, 54));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JLabel label = new JLabel("NVIDIA GPU Monitor", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton copyButton = new JButton("Copy to Clipboard");
        buttonPanel.add(copyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JButton pauseButton = new JButton("Pause");
        buttonPanel.add(pauseButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
