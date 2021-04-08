package lift;

import java.util.ArrayList;

public class LiftMonitor {
	private int floor;
	private boolean moving;
	private int direction;
	private ArrayList<Integer> waitEntry;
	private ArrayList<Integer> waitExit;
	private int load;
	private int walkin;

	public LiftMonitor() {
		this.waitEntry = new ArrayList<Integer>();
		this.waitExit = new ArrayList<Integer>();
		this.direction = 1;
	}

	public synchronized int runElevator(LiftView liftView, int from, int to) throws InterruptedException {
		walkin = 0;
		if (!waitEntry.isEmpty() || !waitExit.isEmpty()) {
			moving = false;
			floor = from;
			if (floor == 6) {
				this.direction = -1;
			} else if (floor == 0) {
				this.direction = 1;
			}

			if (waitEntry.contains(floor)) {
				liftView.openDoors(floor);
				notifyAll();
				while ((waitEntry.contains(floor) && waitExit.size() < 4)) {
					wait();
				}
				liftView.closeDoors();
			}
			to = to + this.direction;
			moving = true;
		}
		return to;
	}

	public synchronized void passExit(int toFloor) throws InterruptedException {
		while (toFloor != this.floor || moving) {
			wait();
		}
		this.walkin--;
	}

	public synchronized void passEnter(int fromFloor, int toFloor) throws InterruptedException {
		this.waitEntry.add(fromFloor);
		while ((fromFloor != this.floor || this.waitExit.size() + this.walkin > 3)) {
			wait();
		}
		walkin++;
		notifyAll();
	}

	public synchronized void enterElevator(int fromFloor, int toFloor) {
		walkin--;
		this.waitExit.add(toFloor);
		waitEntry.remove(this.waitEntry.indexOf(fromFloor));
		notifyAll(); 
	}
	
	public synchronized void exitElevator(int toFloor) throws InterruptedException {
		waitExit.remove(waitExit.indexOf(toFloor));
		this.walkin++; 
		notifyAll(); 
	}

}
