package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram3 extends ActorThread<WashingMessage> {

    private SafetyController io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram3(SafetyController io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("washing program 3 started");
            
            // Switch off heating
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            
            // Wait for temperature controller to acknowledge 
            //WashingMessage ack1 = receive();
            //System.out.println("got " + ack1);

            // Drain barrel, which may take some time. To ensure the barrel
            // is drained before we continue, an acknowledgment is required.
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            WashingMessage ack2 = receive();  // wait for acknowledgment
            System.out.println("got _____ " + ack2);

            // Now that the barrel is drained, we can turn off water regulation.
            // For the WATER_IDLE order, the water level regulator will not send
            // any acknowledgment.
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            
            

            // Switch off spin. We expect an acknowledgment, to ensure
            // the hatch isn't opened while the barrel is spinning.
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            WashingMessage ack3 = receive();  // wait for acknowledgment
            System.out.println("got " + ack3);

            // Unlock hatch
            io.lock(false);
            
            System.out.println("washing program 3 finished");
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
