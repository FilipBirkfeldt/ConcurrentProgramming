package client.view;

import java.awt.Component;

import javax.swing.JPanel;

/** A panel that ensures revalidate() and repaint() are called as needed. */
@SuppressWarnings("serial")
class ConvenientPanel extends JPanel {
    @Override
    public Component add(Component c) {
        Component result = super.add(c);
        revalidate();
        repaint();
        return result;
    }

    @Override
    public void remove(Component c) {
        super.remove(c);
        revalidate();
        repaint();
    }
}
