package actor.test;

import actor.ActorThread;

public class ExampleMessagingWithTimeout {

    private Producer p = new Producer();
    private Consumer c = new Consumer();
    
    class Producer extends Thread {
        
        @Override
        public void run() {
            try {
                Thread.sleep(300);
                c.send("ole");
                Thread.sleep(300);
                c.send("dole");
                Thread.sleep(1000);
                c.send("doff");
            } catch (InterruptedException e) {
                // not expected to happen
                throw new Error(e);
            }
        }
    }
    
    class Consumer extends ActorThread<String> {

        @Override
        public void run() {
            try {
                System.out.println("consumer eagerly awaiting messages...");
                for (int k = 0; k < 3; k++) {
                    String s = receiveWithTimeout(500);
                    System.out.println("received [" + s + "]");
                }
                String s = receiveWithTimeout(2000);
                System.out.println("received [" + s + "]");
            } catch (InterruptedException e) {
                // not expected to happen
                throw new Error(e);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ExampleMessagingWithTimeout app = new ExampleMessagingWithTimeout();
        
        app.p.start();
        app.c.start();
        
        app.p.join();
        app.c.join();
        System.out.println("all done");
    }
}
