package rsa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import rsa.impl.RSA;

public class Factorizer {

    private static final int INTERVAL = 1;
    
    /**
     * Breaks the RSA-encrypted message in code, by factorizing the integer n.
     * Progress is reported continually using the ProgressTracker callback
     * interface.
     * 
     * NOTE: you are not expected to modify this code (but you are welcome to
     * if you wish). This file is only provided for your reference.
     * 
     * @return  the decrypted (plaintext) message
     */
    public static String crack(String code, BigInteger n, ProgressTracker tracker) throws InterruptedException {
        List<BigInteger> primeFactors = new ArrayList<BigInteger>();

        BigInteger two = new BigInteger("2");
        BigInteger sqrt = Factorizer.sqrt(n);

        while (n.mod(two).equals(BigInteger.ZERO)) {
            primeFactors.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger f = BigInteger.valueOf(3);
            BigInteger million = BigInteger.valueOf(1000000);
            int lastReport = 0;
            long tStart = System.currentTimeMillis();
            long nextCheck = tStart;
            while (f.compareTo(sqrt) <= 0) {
                if (Thread.interrupted()) {
                    throw new InterruptedException("crack() was cancelled");
                }
                if (n.mod(f).equals(BigInteger.ZERO)) {
                    primeFactors.add(f);
                    n = n.divide(f);
                } else {
                    f = f.add(two);
                }
                if (System.currentTimeMillis() >= nextCheck) {
                    int ppm = f.multiply(million).divide(sqrt).intValue();
                    tracker.onProgress(ppm - lastReport);
                    lastReport = ppm;
                    nextCheck += INTERVAL;
                }
            }
            primeFactors.add(n);
            tracker.onProgress(1000000 - lastReport);
        }

        BigInteger p = primeFactors.get(0);
        BigInteger q = primeFactors.get(1);

        return new RSA(p, q).decrypt(code);
    }

    // -----------------------------------------------------------------------

    private static BigInteger sqrt(BigInteger x) {
        // Babylonian method for computing square root:
        // https://en.wikipedia.org/wiki/Methods_of_computing_square_roots#Babylonian_method
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        while (true) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2)) {
                return div.compareTo(div2) < 0 ? div : div2;
            }
            div2 = div;
            div = y;
        }
    }

}
