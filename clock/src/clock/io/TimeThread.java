package clock.io;

import java.util.concurrent.Semaphore; 

public class TimeThread extends Thread{
	
	private ClockOutput clockOutput; 
	private MonitorThreadHandler monThread; 
	
	public TimeThread(ClockOutput clockOutput, MonitorThreadHandler monThread) {
		this.clockOutput = clockOutput; 
		this.monThread = monThread; 
	}
	
	@Override
	public void run() {
		
			long t0 = System.currentTimeMillis();
	
			
			
		while(true) {
			long now = System.currentTimeMillis(); 
			int time = monThread.getTime(); 
			System.out.print(time); 
			int h = (time/10000)%24; 
			int m = (time/100)-(h*100)*60; 
			int s = (((time) - (h * 10000) - (m * 100)) % 60) + 1; 
		
			if (s > 59) {
				s = 0;
				m++;
			}
			if (m > 59) {
				m = 0;
				h++;
			}

			if (h > 23) {
				h = 0;
			}
			
			monThread.setTime(h*10000+m*100+s);
			clockOutput.displayTime(h, m,s); 
			
			try {
				Thread.sleep(1000 - (now - t0) % 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
