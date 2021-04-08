import javax.swing.SwingUtilities;

import chat.client.ChatWindow;

public class Chat {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> new ChatWindow().login());
    }
}
