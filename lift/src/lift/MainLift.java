package lift;

import java.util.Random;

import lift.LiftView;
import lift.Passenger;

public class MainLift {

	public static void main(String[] args) throws InterruptedException {

		LiftView liftView = new LiftView();
		Random rand = new Random();

		LiftMonitor liftMon = new LiftMonitor();
		LiftThread liftThread = new LiftThread(liftMon, liftView);
		liftThread.start();

		for (int i = 0; i < 5; i++) {
			Passenger passenger = liftView.createPassenger();
			PersonThread personThread = new PersonThread(passenger, liftMon);
			personThread.start();
			Thread.sleep(rand.nextInt(2000));
		}

	}

}
