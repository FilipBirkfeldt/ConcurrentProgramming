package clock.io;

/** Output signals to clock hardware. */
public interface ClockOutput {

    /** Display the given time on the display, for example (15, 2, 37) for
        15 hours, 2 minutes and 37 seconds since midnight. */
    void displayTime(int hours, int mins, int secs);

    /** Indicate on the display whether the alarm is on or off. */
    void setAlarmIndicator(boolean on);

    /** Signal the alarm. (In the emulator, only a visual alarm is given.) */
    void alarm();
}
