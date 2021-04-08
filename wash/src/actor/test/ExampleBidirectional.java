package actor.test;

import actor.ActorThread;

public class ExampleBidirectional {

    private ClientThread    ct = new ClientThread();
    private FibonacciThread ft = new FibonacciThread();
    
    class ClientThread extends ActorThread<Integer> {
        
        @Override
        public void run() {
            try {
                System.out.println("ClientThread sending request");
                ft.send(14);
                int reply = receive();
                System.out.println("received result fib(14) = " + reply);
                
            } catch (InterruptedException e) {
                // not expected to happen
                throw new Error(e);
            }
        }
    }
    
    class FibonacciThread extends ActorThread<Integer> {

        @Override
        public void run() {
            try {
                while (true) {
                    
                    int n = receive();
                    System.out.println("request received by FibonacciThread");
                    
                    int f2 = 0;
                    int f1 = 1;
                    for (int k = 2; k <= n; k++) {
                        int s = f2 + f1;
                        f2 = f1;
                        f1 = s;
                        Thread.sleep(100);
                    }
                    ct.send(f1);

                }
            } catch (InterruptedException e) {
                System.out.println("FibonacciThread terminated");
            }
        }
    }

    
    public static void main(String[] args) throws InterruptedException {
        ExampleBidirectional app = new ExampleBidirectional();
        
        app.ct.start();
        app.ft.start();
        
        app.ct.join();
        app.ft.interrupt();
        app.ft.join();
    }

}
