package com.qrv1;

/**
 * Reed-Solomon Error Correction Encoder
 * Implements Reed-Solomon encoding for QR code error correction
 */
class ReedSolomonEncoder {
    private int eccCount;
    private int[] generator;
    
    public ReedSolomonEncoder(int eccCount) {
        this.eccCount = eccCount;
        this.generator = generatePolynomial(eccCount);
    }
    
    public byte[] encode(byte[] data) {
        int[] message = new int[data.length + eccCount];
        
        // Copy data to message
        for (int i = 0; i < data.length; i++) {
            message[i] = data[i] & 0xFF;
        }
        
        // Perform polynomial division
        for (int i = 0; i < data.length; i++) {
            int coefficient = message[i];
            if (coefficient != 0) {
                for (int j = 0; j < generator.length; j++) {
                    message[i + j] ^= gfMultiply(generator[j], coefficient);
                }
            }
        }
        
        // Extract error correction bytes
        byte[] ecc = new byte[eccCount];
        for (int i = 0; i < eccCount; i++) {
            ecc[i] = (byte) message[data.length + i];
        }
        
        return ecc;
    }
    
    private int[] generatePolynomial(int degree) {
        int[] poly = new int[degree + 1];
        poly[0] = 1;
        
        for (int i = 0; i < degree; i++) {
            // Multiply by (x - α^i)
            for (int j = poly.length - 1; j > 0; j--) {
                poly[j] = gfMultiply(poly[j], gfPow(2, i)) ^ poly[j - 1];
            }
            poly[0] = gfMultiply(poly[0], gfPow(2, i));
        }
        
        return poly;
    }
    
    private int gfMultiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return gfExp[(gfLog[a] + gfLog[b]) % 255];
    }
    
    private int gfPow(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result = gfMultiply(result, base);
        }
        return result;
    }
    
    // Galois Field tables for GF(256)
    private static final int[] gfExp = generateExpTable();
    private static final int[] gfLog = generateLogTable();
    
    private static int[] generateExpTable() {
        int[] exp = new int[256];
        int x = 1;
        for (int i = 0; i < 255; i++) {
            exp[i] = x;
            x <<= 1;
            if (x >= 256) {
                x ^= 0x11d; // x^8 + x^4 + x^3 + x^2 + 1
            }
        }
        exp[255] = exp[0];
        return exp;
    }
    
    private static int[] generateLogTable() {
        int[] log = new int[256];
        for (int i = 0; i < 255; i++) {
            log[gfExp[i]] = i;
        }
        return log;
    }
}
