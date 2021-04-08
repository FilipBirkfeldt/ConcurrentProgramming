package wash.control;

import actor.ActorThread;

/**
 * Class used for messaging
 * - from washing programs to spin controller (SPIN_xxx)
 * - from washing programs to temperature controller (TEMP_xxx)
 * - from washing programs to water controller (WATER_xxx)
 * - from controllers to washing programs (ACKNOWLEDGMENT)
 */
public class WashingMessage {

    // available commands

    public static final int SPIN_OFF       = 1; // stop barrel from rotating
    public static final int SPIN_SLOW      = 2; // rotate barrel slowly, alternating left/right
    public static final int SPIN_FAST      = 3; // rotate barrel fast

    public static final int TEMP_IDLE      = 4; // stop heater, and stop temperature regulation
    public static final int TEMP_SET       = 5; // regulate temperature to given value (degrees C)

    public static final int WATER_IDLE     = 6; // stop filling/draining, and stop water level regulation
    public static final int WATER_FILL     = 7; // fill water to given value (liters)
    public static final int WATER_DRAIN    = 8; // drain water from barrel
    
    public static final int ACKNOWLEDGMENT = 9; // reply: indicates that a previous order was fulfilled
    
    // -----------------------------------------------------------------------
    
    private final ActorThread<WashingMessage> sender;
    private final int command;
    private final double value;
        
    // -----------------------------------------------------------------------
    
    /**
     * @param sender   the thread that sent the message
     * @param command  a command, such as SPIN_FAST or WATER_DRAIN
     * @param value    an optional value, such as a water level for WATER_FILL, or a temperature for TEMP_SET 
     */
    public WashingMessage(ActorThread<WashingMessage> sender, int command, double value) {
        this.sender = sender;
        this.command = command;
        this.value = value;
        
        if (command < SPIN_OFF || command > ACKNOWLEDGMENT) {
            throw new IllegalArgumentException("invalid WashingMessage order: " + command);
        }
    }
    
    public WashingMessage(ActorThread<WashingMessage> sender, int command) {
        this(sender, command, 0);
        
        if (command == TEMP_SET || command == WATER_FILL) {
            throw new IllegalArgumentException("invalid WashingMessage command: " + command + ", needs value");
        }
    }
    
    public ActorThread<WashingMessage> getSender() {
        return sender;
    }
    
    public int getCommand() {
        return command;
    }
    
    public double getValue() {
        return value;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (command) {
        case SPIN_OFF:
            sb.append("SPIN_OFF");
            break;
        case SPIN_FAST:
            sb.append("SPIN_FAST");
            break;
        case SPIN_SLOW:
            sb.append("SPIN_SLOW");
            break;

        case TEMP_IDLE:
            sb.append("TEMP_IDLE");
            break;
        case TEMP_SET:
            sb.append("TEMP_SET:");
            sb.append(value);
            break;

        case WATER_IDLE:
            sb.append("WATER_IDLE");
            break;
        case WATER_FILL:
            sb.append("WATER_FILL:");
            sb.append(value);
            break;
        case WATER_DRAIN:
            sb.append("WATER_DRAIN");
            break;
        case ACKNOWLEDGMENT:
            sb.append("ACKNOWLEDGMENT");
            break;
        }

        sb.append(" from ");
        sb.append(sender == null ? "**null**" : sender.getClass().getSimpleName());

        return sb.toString();
    }
}
