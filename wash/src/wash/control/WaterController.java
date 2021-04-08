package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private WashingMessage m = null;
	private long dt = 1;
	private int a = 0;
	private boolean drain = false;
	private boolean fill = false;
	private WashingMessage m2;
	private int checkOne = 0;
	private SafetyController sC = new SafetyController(io);

	public WaterController(WashingIO io) {
		this.io = io;
		m2 = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Long t1 = System.currentTimeMillis();
				WashingMessage n = receiveWithTimeout(1000 / Settings.SPEEDUP);

				if (n != null) {
					m = n;
					a = 0;
				}

				if (m != null) {

					// Water fill
					if (m.getCommand() == WashingMessage.WATER_FILL) { // && drain == false
						io.drain(false);
						double reqWater = m.getValue();
						// System.out.println(reqWater);
						double currWater = io.getWaterLevel();

						if ((currWater) - 0.1 * dt / 1000 < reqWater) {
							io.fill(true);
							fill = true;
							// System.out.println("fill true");
						} else {
							io.fill(false);
							fill = false;
							if (a == 0 && currWater > 10) {
								System.out.println("Water fill skickar");
								m.getSender().send(m2);
								a++;

							}
						}
						checkOne = 0;
						// System.out.println(dt);
					}

					// Water drain
					if (m.getCommand() == WashingMessage.WATER_DRAIN) { // && fill == false

						if (m.getCommand() == WashingMessage.WATER_DRAIN) {
							drain = true;
							double currWater = io.getWaterLevel();
							io.drain(true);
							// System.out.println(currWater + " draining");
							if (currWater <= 0) {
								// drain = false;

								if (checkOne == 0) {
									m.getSender().send(m2);
									System.out.println("Vår nya skickar");
									checkOne++;

								}
							}

						}
					}

					if (m.getCommand() == WashingMessage.WATER_IDLE) {
						io.drain(false);
						io.fill(false);
						checkOne = 0;
					}
					Long t2 = System.currentTimeMillis();
					dt = (t2 - t1);

				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}
}
