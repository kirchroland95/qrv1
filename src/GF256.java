public class GF256 {
	// Galois Field with 256 elements
    // The irreducible polynomial used for QR codes (x^8 + x^4 + x^3 + x^2 + 1)
    // In binary: 1 0001 1101, which is 285 in decimal.
    private static final int RS_GENERATOR_POLY = 0b100011101; 

    // Look-up tables for quick multiplication and division
    public static final int[] LOG_TABLE = new int[256];
    public static final int[] EXP_TABLE = new int[256];

    static {
        // Initialize the log/exp tables using the irreducible polynomial.
        int x = 1;
        for (int i = 0; i < 256; i++) {
            EXP_TABLE[i] = x;
            if (i < 255) {
                LOG_TABLE[x] = i;
            }
            x <<= 1;
            // If the highest bit (256) is set, XOR with the generator polynomial
            if (x > 255) {
                x ^= RS_GENERATOR_POLY;
            }
        }
    }

    // --- Core Field Operations ---

    // Addition/Subtraction: XOR operation in GF(2^8)
    public static int add(int a, int b) {
        return a ^ b;
    }

    // Multiplication: Uses the LOG/EXP tables for fast lookups.
    public static int multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        // In GF(2^8), a * b = exp(log(a) + log(b)) mod 255
        int logSum = LOG_TABLE[a] + LOG_TABLE[b];
        // The result is based on the exponent table index modulo 255
        return EXP_TABLE[logSum % 255]; 
    }

    // Division: a / b = a * (1/b) = exp(log(a) - log(b)) mod 255
    public static int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero in GF256");
        }
        if (a == 0) {
            return 0;
        }
        // log(a) - log(b) may be negative, so add 255 before modulo 255
        int logDiff = LOG_TABLE[a] - LOG_TABLE[b];
        int index = (logDiff + 255) % 255;
        return EXP_TABLE[index];
    }
}