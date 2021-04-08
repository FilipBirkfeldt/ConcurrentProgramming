package actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {

	private LinkedBlockingQueue blockingQueue = new LinkedBlockingQueue<M>();

	/** Called by another thread, to send a message to this thread. */
	public void send(M message) {
		// TODO: implement this method (one or a few lines)
		blockingQueue.offer(message); 
	}

	/** Returns the first message in the queue, or blocks if none available. */
	protected M receive() throws InterruptedException {
		// TODO: implement this method (one or a few lines)
//		while(this.blockingQueue.isEmpty()) {
//			this.wait();
//		}
		return (M) blockingQueue.take();  
	}

	/**
	 * Returns the first message in the queue, or blocks up to 'timeout'
	 * milliseconds if none available. Returns null if no message is obtained within
	 * 'timeout' milliseconds.
	 */
	protected M receiveWithTimeout(long timeout) throws InterruptedException {
		// TODO: implement this method (one or a few lines)
		return (M) blockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}
}