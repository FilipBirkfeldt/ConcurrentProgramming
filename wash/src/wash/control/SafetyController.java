package wash.control;

import wash.io.WashingIO;

public class SafetyController {

	private WashingIO io;
	private boolean drainOn;
	private boolean fillOn;
	private boolean isLocked;

	public SafetyController(WashingIO io) {
		this.io = io;
	}

	public void heat(boolean heat) {
		if (io.getWaterLevel() > 0) {
			io.heat(heat);
		} else if (heat == false) {
			io.heat(heat);
		}
	}

	public void fill(boolean on) {
		if (drainOn == false) {
			fillOn = on;
			io.fill(on);
		}
	}

	public void drain(boolean on) {
		if (fillOn == false) {
			drainOn = on;
			io.drain(on);
		}
	}

	public void lock(boolean locked) {
		if (io.getWaterLevel() <= 0) {
			io.lock(locked);
			isLocked = locked;
		}
	}

	public void setSpinMode(int spinMode) {
		if (isLocked == true) {
			if (spinMode == 4 && io.getWaterLevel() < 2) {
				io.setSpinMode(spinMode);
			} else {
				io.setSpinMode(spinMode);
			}
		}
	}

	public int awaitButton() throws InterruptedException {
		return io.awaitButton();
	}

	public double getWaterLevel() {
		return io.getWaterLevel();
	}

	public double getTemperature() {
		return io.getTemperature();
	}

}
