package wash.io;

/** Input/Output (IO) for our washing machine */
public interface WashingIO {

    double MAX_WATER_LEVEL = 20;   // barrel volume (liters)
    
    int SPIN_IDLE          = 1;    // barrel not rotating
    int SPIN_LEFT          = 2;    // barrel rotating slowly, left
    int SPIN_RIGHT         = 3;    // barrel rotating slowly, right
    int SPIN_FAST          = 4;    // barrel rotating fast

    // ===== Control signals (output) =====

    /** Turn heater on (true) or off (false) */
    void heat(boolean on);

    /** Set input valve to open (true) or closed (false) */
    void fill(boolean on);

    /** Turn drain pump on (true) or off (false) */
    void drain(boolean on);

    /** Set hatch to locked (true) or unlocked (false) */
    void lock(boolean locked);
    
    /** @param spinMode  one of SPIN_IDLE, SPIN_LEFT, SPIN_RIGHT, SPIN_FAST */
    void setSpinMode(int spinMode);

    // ===== Sensor signals (input) ======
    
    /** Blocks until a program button (0, 1, 2, 3) is pressed */
    int awaitButton() throws InterruptedException;
    
    /** @return water level, in range 0 .. MAX_WATER_LEVEL liters */
    double getWaterLevel();
    
    /** @return temperature, in degrees Celsius */
    double getTemperature();
}
