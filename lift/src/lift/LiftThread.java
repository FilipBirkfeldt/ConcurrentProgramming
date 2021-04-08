package lift;

public class LiftThread extends Thread {
	private LiftMonitor liftMon;
	private LiftView liftView;
	private int to;
	private int from;

	public LiftThread(LiftMonitor liftMon, LiftView liftView) {
		this.liftMon = liftMon;
		this.liftView = liftView;
		this.to = 0;
		this.from = 0;
	}

	@Override
	public void run() {
		while (true) {
			try {
				to = this.liftMon.runElevator(this.liftView, to, from);
				if (to != from) {
					liftView.moveLift(from, to);
				}
				from = to;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}
}
