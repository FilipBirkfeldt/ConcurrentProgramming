package rsa;

public interface ProgressTracker {

    /**
     * Reports the progress made since the last report, in ppm (parts per
     * million). That is, if delta is 1 then one millionth of the total progress
     * has been made, or 0.0001%.
     * 
     * @param ppmDelta     Progress made since last report, in ppm.
     */
    public void onProgress(int ppmDelta);
}
