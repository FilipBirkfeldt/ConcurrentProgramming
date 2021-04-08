package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigInteger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * A panel that constructs a single item in the progress list. It displays
 * information about a single message to break, including its N (key), message
 * text (encrypted initially, readable when done) and its current progress.
 */
@SuppressWarnings("serial")
public class ProgressItem extends ConvenientPanel {

    private final JProgressBar progressBar;
    private final JTextArea textArea;

    private static final Font MESSAGE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private static final Dimension MESSAGE_SIZE = new Dimension(200, 80);

    /** Create the panel, displaying the integer _n_ and the encrypted message _code_. */
    public ProgressItem(BigInteger n, String code) {
        setBorder(BorderFactory.createTitledBorder("N=" + n + " (" + n.bitLength() + " bits)"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        add(main);

        textArea = new JTextArea(code);
        textArea.setFont(MESSAGE_FONT);
        textArea.setPreferredSize(MESSAGE_SIZE);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        main.add(textArea, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMaximum(1000000);
        progressBar.setValue(0);
        main.add(progressBar, BorderLayout.SOUTH);
    }

    /** Access this item's progress bar. */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /** Access this item's text area. */
    public JTextArea getTextArea() {
        return textArea;
    }

    @Override
    /** Ensure this item doesn't expand vertically, only horizontally. */
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
