package actor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ActorThreadTest {
    
    @Test
    void testBidirectional() throws InterruptedException {
        checkMainPrints(ExampleBidirectional::main,
                "ClientThread sending request\n" + 
                "request received by FibonacciThread\n" + 
                "received result fib(14) = 377\n" + 
                "FibonacciThread terminated\n");
    }

    @Test
    void testProducerConsumer() throws InterruptedException {
        checkMainPrints(ExampleProducerConsumer::main,
                "consumer eagerly awaiting messages...\n" + 
                "received [ole]\n" + 
                "received [dole]\n" + 
                "received [doff]\n" + 
                "all done\n");
    }

    @Test
    void testMessagingWithTimeout() throws InterruptedException {
        checkMainPrints(ExampleMessagingWithTimeout::main,
                "consumer eagerly awaiting messages...\n" + 
                "received [ole]\n" + 
                "received [dole]\n" + 
                "received [null]\n" + 
                "received [doff]\n" + 
                "all done\n");
    }

    // -----------------------------------------------------------------------
    
    /** Helper interface for making lambdas, for a main function that throws InterruptedException */
    private interface InterruptibleMain {
        void invoke(String[] args) throws InterruptedException;
    }
    
    /** Helper method: run a main method in another class, and check the printed output. */ 
    private void checkMainPrints(InterruptibleMain main, String expectedOutput) throws InterruptedException {
        PrintStream sysout = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bos, true));
            main.invoke(new String[] {});
        } finally {
            System.setOut(sysout);
        
            // make sure line feeds are always represented as "\n",
            // regardless of what the system uses
            String actualOutput = bos.toString().replace(System.getProperty("line.separator"), "\n");
            assertEquals(expectedOutput, actualOutput);
        }
    }
}
