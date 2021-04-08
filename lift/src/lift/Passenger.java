package lift;

public interface Passenger {
    /** @return the floor (0..6) the passenger starts at */
    int getStartFloor();

    /** @return the floor (0..6) the passenger is going to */
    int getDestinationFloor();

    /** Animate the passenger's walk, on the entry floor, to the lift */
    void begin();

    /** Animate the passenger's walk from the entry floor into the lift */
    void enterLift();

    /** Animate the passenger's walk from the lift to the exit floor */
    void exitLift();

    /** Animate the passenger's walk, on the exit floor, out of view */
    void end();
}
