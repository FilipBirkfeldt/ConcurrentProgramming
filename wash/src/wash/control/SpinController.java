package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

	// TODO: add attributes
	private WashingIO io;
	private WashingMessage m = null;
	private int enGang = 0;
	private int enGang1 = 0;
	private WashingMessage m2;

	public SpinController(WashingIO io) {
		this.io = io;
		m2 = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);

	}

	@Override
	public void run() {
		try {

			// ... TODO ...

			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage n = receiveWithTimeout(60000 / Settings.SPEEDUP);

				if (n != null) {

					m = n;
					if ((m.getCommand() == WashingMessage.SPIN_SLOW || m.getCommand() == WashingMessage.SPIN_FAST)
							&& enGang == 0) {

						System.out.println("SPIN skickar");
						enGang++;
						m.getSender().send(m2);
					}
				}

				if (m != null) {
					System.out.println("got " + m);

					if (m.getCommand() == WashingMessage.SPIN_SLOW) {
						io.setSpinMode(io.SPIN_LEFT);
						Thread.sleep(60000 / Settings.SPEEDUP);
						io.setSpinMode(io.SPIN_RIGHT);
						enGang1 = 0;

					}
					// if m is null, it means a minute passed and no message was
					// received

					if (m.getCommand() == WashingMessage.SPIN_FAST) {
						io.setSpinMode(io.SPIN_FAST);
						enGang1 = 0;
					}

					if (m.getCommand() == WashingMessage.SPIN_OFF) {
						enGang = 0;
						io.setSpinMode(io.SPIN_IDLE);

						if (enGang1 == 0) {
							m.getSender().send(m2);
							System.out.println("SPINOFF skickar");
							enGang1++;
						}

					}

				}
			}
			// ... TODO ...

		} catch (

		InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
