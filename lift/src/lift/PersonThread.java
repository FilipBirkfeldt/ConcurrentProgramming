package lift;

public class PersonThread extends Thread {

	private Passenger passenger;
	private LiftMonitor liftMon;

	public PersonThread(Passenger passenger, LiftMonitor liftMon) {
		this.passenger = passenger;
		this.liftMon = liftMon;
	}

	@Override
	public void run() {
		int fromFloor = this.passenger.getStartFloor();
		int toFloor = this.passenger.getDestinationFloor();
		this.passenger.begin();       // Börja gå 
		
		try {
			this.liftMon.passEnter(fromFloor, toFloor);
			this.passenger.enterLift(); 
			this.liftMon.enterElevator(fromFloor, toFloor);
			this.liftMon.passExit(toFloor);
			this.passenger.exitLift();
			this.liftMon.exitElevator(toFloor);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.passenger.end(); 
		

	}

}
