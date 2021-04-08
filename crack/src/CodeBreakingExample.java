import java.math.BigInteger;

import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreakingExample {

    public static void main(String[] args) throws InterruptedException {

        // 'N' from a public key, and an encrypted message.
        BigInteger n = new BigInteger("11389242492645491");
        String code = "7021848273806764.10767140054779016.8863740657844774."
                + "3069973477837254.5946158752146824.746881505851720.35443100729859."
                + "5887222754060477.7572030531385751.5912432895335928.1200318206572774."
                + "6893065302890971.9833416677161978.9603090348675751.6134445596547525."
                + "2966583181272697.7097311438706347.7640840724004198.11383156201129748."
                + "7223116496279419.7814018043021301.5064640675735253.6812260345065772."
                + "7681415330788952.9833416677161978.3627051942518446.3889298415207323."
                + "3575700274762082.11389112854379991.2248425516142505.3116328692105989."
                + "2009262387505676.9833416677161978.3466837559934675.7406943932065539."
                + "2675887715107546.772120050104796.8781296269284421.7894392890492765."
                + "11007155148195570.8023132150964243.6451626075108528.3573772917703579.2427156306635175.";
        ProgressTracker tracker = new Tracker();
        String plaintext = Factorizer.crack(code, n, tracker);

        System.out.println("\nDecryption complete. The message is:\n\n  " + plaintext);
    }

    // -----------------------------------------------------------------------

    /** ProgressTracker: reports how far factorization has progressed */ 
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            System.out.println("progress = " + totalProgress + "/1000000");
        }
    }
}
