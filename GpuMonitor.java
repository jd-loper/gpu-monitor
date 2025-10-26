import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GpuMonitor extends JFrame {
    private JTextArea textArea;
    private Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public GpuMonitor() {
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        displayArea = new JTextArea();
        displayArea = setEditable(false);
        displayArea = setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayArea = setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}