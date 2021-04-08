package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private WashingMessage m = null;
	private int a = 0;

	public TemperatureController(WashingIO io) {
		this.io = io;
		// TODO
	}

	@Override
	public void run() {
		try {
			while (true) {
				WashingMessage n = receiveWithTimeout(60000 / Settings.SPEEDUP);
				WashingMessage m2 = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);

				if (n != null) {
					m = n;
					a = 0;
				}

				if (m != null) {

					if (m.getCommand() == WashingMessage.TEMP_IDLE) {
						io.heat(false);
					}

					long dt = 1;
					boolean heat = false;
					// Temperature set
					while (m.getCommand() == WashingMessage.TEMP_SET) {

						Long t1 = System.currentTimeMillis();

						double reqTemp = m.getValue();
						double currTemp = io.getTemperature();
						double waterLevel = io.getWaterLevel();
						// System.out.println(currTemp + "Current heat");
						if (currTemp < (reqTemp - 2 + 0.2 * (currTemp - 20) * Math.pow(2.38, -4)) && !heat
								&& io.getWaterLevel() > 0) {
							// System.out.println(currTemp + "heat true");
							io.heat(true);
							heat = true;
						}

						double levelTemp = reqTemp
								- 0.2 * Settings.SPEEDUP * 2000 / (waterLevel * 4.184 * Math.pow(10, 3));
						// System.out.println(levelTemp +" + " + (long) Math.multiplyExact(dt,
						// Settings.SPEEDUP/1000));

						if ((currTemp > levelTemp) && heat) {
							// System.out.println(currTemp + "heat false");
							io.heat(false);
							heat = false;
						}

						n = receiveWithTimeout(10000 / Settings.SPEEDUP);

						if (n != null) {
							if (n.getValue() != 0) {
								a = 0;
							}
							m = n;

						}

						if (a == 0 && (currTemp > (reqTemp - 2) && reqTemp != 0)) {
							System.out.println("HEAT SKICKAR");
							m.getSender().send(m2);
							a++;
						}

						Long t2 = System.currentTimeMillis();
						dt = Math.addExact(t2, -t1);
						// System.out.println(dt);
					}
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
