package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

	// FRÅGOR
	// HEAT, FILL, DRAIN, LOCK, SPIN-OFF, SPIN-Left, SPIN-RIGHT, SPIN-Fast
	// a) which thread would send it?
	// Lock-main, FILL&DRAIN - WaterController , HEAT-TempController, ,
	// SPINs-SpinController
	// b) Which thread should receive it?
	// Ett tvättprogramm, t.ex WP3
	// c) Does the order need an acknowledgement?
	// Nej, trådarna löser det själva.
	// Låsa innan vi fyller med vatten

	public static void main(String[] args) throws InterruptedException {

		WashingProgram3 wp3 = null;
		WashingProgram1 wp1 = null;
//		WashingProgram2 wp2 = null;

		WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

		WashingIO io = sim.startSimulation();
		SafetyController io_test = new SafetyController(io); 

		TemperatureController temp = new TemperatureController(io);
		WaterController water = new WaterController(io);
		SpinController spin = new SpinController(io);

		temp.start();
		water.start();
		spin.start();

		ActorThread<WashingMessage> wp = null; 
		
		while (true) {
			int n = io.awaitButton();
			System.out.println("user selected program " + n);

			if (n == 1) {
				wp = new WashingProgram1(io_test, temp, water, spin);
				wp.start();
			}

			if (n == 2) {
				wp = new WashingProgram2(io_test, temp, water, spin);
				wp.start();
			}

			if (n == 3) {
				wp = new WashingProgram3(io_test, temp, water, spin);
				wp.start();
			}

			if (n == 0 && wp!=null) {
				if (wp.isAlive()) {
					wp.interrupt();
				}
			}

		}

		// REFLEKTIONE MED BOM
		//R1. Which threads exist in your solution?
		// Main, ActorThread, eventQueue, wp och Controllers, safety ingen tråd.  
		
		// R2. How do the threads communicate? Is there any shared data?
		// send/ recieve. 
		// ActorThread tar hand om meddelande. IO tar hand om getWaterlevel, temperatur, awaitButton
		
		// R3. How is the physical model reflected in the timing of your threads?
		// SPEEDUP, zleep (fråga) 
		
		// R4. What period did you select for WaterController?
		// What could the downside of a too long or too short period be?
		// 1 sekund. Om för lång -> ej reglera bra. OM för kort -> tar för mkt av CPU 
		
		// R5. Do you use any BlockingQueue in your solution? How?
		// Trådarna väntar på varandra med recieve, inbyggt i ActorThread. 
		
		// R6. How do you use Java’s interruption facility (interrupt(), InterruptedException)?
		// När vi stänger av WashingProgram(1,2,3) -> till catch o sätter allt till IDLE 
		
		//R7. How do you ensure that the machine never heats unless there is water in it?
		// Med en safetyController som kollar alla requirements för specifika krav. 
		
		//R8. Suppose a washing program ends by turning the heat off and draining the machine of water. 
		//The heat is turned off by sending a WashingMessage to TemperatureController.
		
		//How can you ensure that the heat has indeed been turned off before the washing program continues (and starts the drain pump)?
		
		// Nu väntar trådarna på varandra och därmed lugnt. Men för att verkligen försäkra sig om detta skulle det gå 
		// att implementera det efterfrågade i SafetyController. 
	}
};
