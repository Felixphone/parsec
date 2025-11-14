package launcher;

import engine.engine.EngineCore;
import tracerUtils.GeneralDialogue;

import javax.swing.*;
import java.awt.*;

public class GameLoadingScreen extends GeneralDialogue {

    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;
    private JLabel loadingText;
    private JScrollPane logScroll;
    private JTextArea logArea;
    private JButton toggleButton;
    private boolean logVisible = false;

    public GameLoadingScreen() {
        super("Parsec - loading game", "Launching game", EngineCore.getLogger());

        // === Window ===

        setTitle("PhoenixJGL - Launching game");

        // === Center Panel ===
        JPanel center = new JPanel();
        center.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 5, 20);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;

        // Logo
        JLabel logo = new JLabel("Parsec", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(logo, gbc);

        gbc.insets = new Insets(5, 20, 5, 20);

        // Title
        JLabel title = new JLabel("Launching game", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridy++;
        center.add(title, gbc);

        // Loading text
        loadingText = new JLabel("Initializing...", SwingConstants.CENTER);
        loadingText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        center.add(loadingText, gbc);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setForeground(new Color(95, 210, 255));
        progressBar.setBackground(new Color(40, 40, 70));
        //progressBar.setBorderPainted(false);
        gbc.insets = new Insets(5, 40, 5, 40);
        progressBar.setMinimumSize(new Dimension(600, 20));
        progressBar.setPreferredSize(new Dimension(600, 20));
        progressBar.setMaximumSize(new Dimension(600, 20));
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        center.add(progressBar, gbc);
        gbc.insets = new Insets(5, 10, 5, 10);

        // Toggle button
        toggleButton = new JButton("Show Details");
        toggleButton.addActionListener(e -> toggleDetails());
        toggleButton.setFocusPainted(false);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        center.add(toggleButton, gbc);

        // Log area (hidden initially)
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        logScroll = new JScrollPane(logArea);
        logScroll.setVisible(false);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // Log area takes remaining space when visible
        center.add(logScroll, gbc);


        hideDialogueBanner(true);
        setMainContent(center);
        setFooterContent(new JPanel());

        // === Loading Simulation ===
        timer = new Timer(60, e -> {
            progress++;
            progressBar.setValue(progress);
            String msg = switch (progress / 20) {
                case 0 -> "Loading resources...";
                case 1 -> "Installing assets...";
                case 2 -> "Setting up game world...";
                case 3 -> "Finalizing configuration...";
                default -> "Completing setup...";
            };
            loadingText.setText(msg);
            addLog(msg);

            if (progress >= 100) {
                timer.stop();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        GameLoadingScreen frame = new GameLoadingScreen();
        frame.open();
    }

    @Override
    protected void onUpdate() {
        progress++;
        progressBar.setValue(progress);
        String msg = switch (progress / 20) {
            case 0 -> "Loading resources...";
            case 1 -> "Installing assets...";
            case 2 -> "Setting up game world...";
            case 3 -> "Finalizing configuration...";
            default -> "Completing setup...";
        };
        loadingText.setText(msg);
        addLog(msg);

        if (progress >= 100) {
            timer.stop();
        }
    }

    private void toggleDetails() {
        logVisible = !logVisible;
        logScroll.setVisible(logVisible);

        toggleButton.setText(logVisible ? "Hide Details" : "Show Details");
    }

    private void addLog(String msg) {
        logArea.append("• " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
