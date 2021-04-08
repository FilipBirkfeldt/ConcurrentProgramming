package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WashingProgram2 extends ActorThread<WashingMessage> {

	//private WashingIO io;
	private SafetyController io; 
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;
	

	public WashingProgram2(SafetyController io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
		
	}

	@Override
	public void run() {
		try {

			// låser
			io.lock(true);

			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
			WashingMessage ack3 = receive();

			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
			WashingMessage ack7 = receive();
			System.out.println("washing program 1 got temp" + ack7);

			// WashingMessage ack4 = receive();
			// System.out.println("washing program 1 got heat" + ack4);

			// Instruct SpinController to rotate barrel slowly, back and forth
			// // Expect an
			System.out.println("I WP1");
			// acknowledgment in response. System.out.println("setting
			// SPIN_SLOW...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			WashingMessage ack1 = receive();
			System.out.println("washing program 1 got spin" + ack1);

			// Spin for five simulated minutes (one minute == 60000
			// milliseconds)
			Thread.sleep(20 * 60000 / Settings.SPEEDUP);

			// Instruct SpinController to stop spin barrel spin.
			// Expect an acknowledgment in response. System.out.println("setting
			// SPIN_OFF...");

			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));

			WashingMessage ack2 = receive();
			System.out.println("washing program 1 got spinstop" + ack2);

			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 0));

			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			WashingMessage ack5 = receive();

			// MAIN-WASH 30 minuts - 60degreeees celcius ;)
			// fill
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
			WashingMessage ack20 = receive();

			// temp-set 60
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 60));
			WashingMessage ack21 = receive();
			System.out.println("washing program 1 got temp" + ack7);

			// Spin-Slow
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			WashingMessage ack22 = receive();
			System.out.println("washing program 1 got spin" + ack21);

			Thread.sleep(30 * 60000 / Settings.SPEEDUP);

			// Instruct SpinController to stop spin barrel spin.
			// Expect an acknowledgment in response. System.out.println("setting
			// SPIN_OFF...");

			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));

			WashingMessage ack23 = receive();
			System.out.println("washing program 1 got spinstop" + ack23);

			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 0));

			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			WashingMessage ack25 = receive();

			System.out.println("washing program 1 got Drained " + ack25);

			for (int i = 0; i < 5; i++) {
				water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
				WashingMessage ack8 = receive();

				spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
				WashingMessage ack11 = receive();

				Thread.sleep(2 * 60000 / Settings.SPEEDUP);

				spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
				WashingMessage ack12 = receive();

				water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
				WashingMessage ack10 = receive();
			}
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			io.drain(true);
			spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
			WashingMessage ack15 = receive();
			Thread.sleep(5 * 60000 / Settings.SPEEDUP);
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			WashingMessage ack10 = receive();
			io.drain(false);
			System.out.println("Nu ör den öppen ");
			io.lock(false);

			// Now that the barrel has stopped, it is safe to open the hatch.

		} catch (InterruptedException e) {

			// If we end up here, it means the program was interrupt()'ed:
			// set all controllers to idle

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			System.out.println("washing program terminated");
		}
	}
}
