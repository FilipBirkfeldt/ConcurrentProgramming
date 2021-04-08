import javax.swing.SwingUtilities;

import chat.client.ChatWindow;
import chat.client.Twit;

public class ChatWithTwit {
    public static void main(String[] args) throws Exception {
        // create an imaginary friend to talk to, generating 100 messages
        // with (up to) 30 seconds between them
        new Twit("filip", 100, 30000).start();

        // then open the chat window
        SwingUtilities.invokeAndWait(() -> new ChatWindow().login());
    }
}
