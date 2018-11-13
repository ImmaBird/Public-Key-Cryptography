import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

public class dh {
    // q
    public static BigInteger getPrime(int numBits) {
        return BigInteger.probablePrime(numBits, new SecureRandom());
    }

    // XA
    public static BigInteger getPrivateComponent(BigInteger q) {
        BigDecimal randomDecimal = new BigDecimal(new SecureRandom().nextDouble());
        return new BigDecimal(q).multiply(randomDecimal).toBigInteger();
    }

    // YA
    public static BigInteger getPublicComponent(BigInteger a, BigInteger xa, BigInteger q) {
        return a.modPow(xa, q);
    }

    // K
    public static BigInteger getKey(BigInteger yb, BigInteger xa, BigInteger q) {
        return yb.modPow(xa, q);
    }
}