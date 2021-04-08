package client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class StatusWindow extends JFrame {

    private static final Font HEADING_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Dimension WINDOW_SIZE = new Dimension(900, 600);
    private static final Dimension PANEL_SIZE = new Dimension(100, 500);

    private final JPanel workListPanel = new ConvenientPanel();
    private final JPanel progressListPanel = new ConvenientPanel();
    private final JProgressBar totalProgressBar = new JProgressBar();

    // -----------------------------------------------------------------------

    public StatusWindow() {
        super("Network Surveillance Console");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(WINDOW_SIZE);

        /*
         * Top: headings and total progress bar
         */
        JPanel top = new JPanel(new GridLayout(2, 2, 15, 0));
        top.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(top, BorderLayout.NORTH);

        JLabel leftPanelHeader = new JLabel("WORK LIST: intercepted messages", JLabel.CENTER);
        leftPanelHeader.setFont(HEADING_FONT);
        top.add(leftPanelHeader);

        JLabel rightPanelHeader = new JLabel("PROGRESS LIST: code-breaking tasks", JLabel.CENTER);
        rightPanelHeader.setFont(HEADING_FONT);
        top.add(rightPanelHeader);

        top.add(new JLabel("")); // empty slot
        top.add(totalProgressBar);

        /*
         * Center: worklist + progress panels
         */
        JPanel center = new JPanel(new GridLayout(1, 2, 5, 0));
        add(center, BorderLayout.CENTER);
        Border panelBorder = BorderFactory.createEtchedBorder();

        // worklist panel, center-left
        workListPanel.setLayout(new BoxLayout(workListPanel, BoxLayout.PAGE_AXIS));
        JScrollPane leftScrollPane = new JScrollPane(workListPanel);
        leftScrollPane.setPreferredSize(PANEL_SIZE);
        leftScrollPane.setBorder(panelBorder);
        center.add(leftScrollPane);

        // progress panel, center-right
        progressListPanel.setLayout(new BoxLayout(progressListPanel, BoxLayout.PAGE_AXIS));
        JScrollPane rightScrollPane = new JScrollPane(progressListPanel);
        rightScrollPane.setPreferredSize(PANEL_SIZE);
        rightScrollPane.setBorder(panelBorder);
        center.add(rightScrollPane);

        // prepare the main progress bar 
        totalProgressBar.addChangeListener(e -> totalProgressBar.setString(totalProgressBar.getMaximum() == 0 ? "" : ("TOTAL: " + (int) (100 * totalProgressBar.getPercentComplete()) + "%")));
        totalProgressBar.setStringPainted(true);
        totalProgressBar.setMaximum(0);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // -----------------------------------------------------------------------

    public JPanel getWorkList() {
        return workListPanel;
    }

    // -----------------------------------------------------------------------

    public JPanel getProgressList() {
        return progressListPanel;
    }

    // -----------------------------------------------------------------------

    public JProgressBar getProgressBar() {
        return totalProgressBar;
    }

    // -----------------------------------------------------------------------

    /** Enable run-time checks for Swing threading errors. */
    public void enableErrorChecks() {
        RepaintManager.setCurrentManager(new ThreadConfinementChecker());
    }
}
