package chat.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import chat.client.ChatLog;
import chat.client.ServerControl;
import chat.client.Twit;

class ChatTest {

    @BeforeEach
    void setUp(TestInfo info) throws Exception {
        ServerControl.restartServer(info.getDisplayName());
    }

    @Test
    void testOneTwit() throws InterruptedException {
        final int NBR_MESSAGES  = 5;     // number of messages from each client
        final int MESSAGE_DELAY = 100;   // maximal delay between messages 

        Twit t = new Twit("twit", NBR_MESSAGES, MESSAGE_DELAY);
        t.start();

        ChatLog.expect(1, NBR_MESSAGES);
    }

    @Test
    void testTwoTwits() throws InterruptedException {
        final int NBR_TWITS     = 2;     // number of clients
        final int NBR_MESSAGES  = 5;     // number of messages from each client
        final int MESSAGE_DELAY = 100;   // maximal delay between messages 

        for (int i = 1; i <= NBR_TWITS; i++) {
            Twit t = new Twit("twit" + i, NBR_MESSAGES, MESSAGE_DELAY);
            t.start();
        }

        ChatLog.expect(NBR_TWITS, NBR_TWITS * NBR_MESSAGES);
    }
    
    @Test
    void testThreeTwits() throws InterruptedException {
        final int NBR_TWITS     = 12;     // number of clients
        final int NBR_MESSAGES  = 10000;     // number of messages from each client
        final int MESSAGE_DELAY = 0;   // maximal delay between messages 

        for (int i = 1; i <= NBR_TWITS; i++) {
            Twit t = new Twit("twit" + i, NBR_MESSAGES, MESSAGE_DELAY);
            t.start();
        }

        ChatLog.expect(NBR_TWITS, NBR_TWITS * NBR_MESSAGES);
    }
}
