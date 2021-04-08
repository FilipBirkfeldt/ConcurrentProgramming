package clock.io;

import java.util.concurrent.Semaphore;

/** Input signals from clock hardware. */
public interface ClockInput {

    // constants for user choices
    int CHOICE_SET_TIME     = 1; // user set new clock time
    int CHOICE_SET_ALARM    = 2; // user set new alarm time
    int CHOICE_TOGGLE_ALARM = 3; // user pressed both buttons simultaneously

    /** @return semaphore signaled on user input (via hardware interrupt) */
    Semaphore getSemaphore();

    /** @return an item of user input (available only when semaphore is signaled) */
    UserInput getUserInput();

    // -----------------------------------------------------------------------
    
    /** An item of input, entered by the user. */
    interface UserInput {
        /**
         * Returns
         *   CHOICE_SET_TIME if the user entered a new clock time,
         *   CHOICE_SET_ALARM if the user entered a new alarm time, or
         *   CHOICE_TOGGLE_ALARM if the user pressed both simultaneously
         *     (to turn the alarm on/off).
         */
        int getChoice();
    
        /**
         * These methods return the most recently set time (clock time or alarm time).
         * 
         * If getChoice() returns CHOICE_SET_TIME, these return the time the user entered.
         * If getChoice() returns CHOICE_SET_ALARM, these return the alarm time the user entered.
         * If getChoice() returns CHOICE_TOGGLE_ALARM, these return an invalid value.
         */
        int getHours();
        int getMinutes();
        int getSeconds();
    }
}
