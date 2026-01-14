public class ReedSolomonEncoder {

    // Creates the generator polynomial (Index 0 = x^0 ... Index N = x^N)
    private static int[] generatePolynomial(int ecWords) {
        int[] poly = {1};
        for (int i = 0; i < ecWords; i++) {
            int a_i = GF256.EXP_TABLE[i];
            int[] nextPoly = new int[poly.length + 1];
            for (int j = 0; j < poly.length; j++) {
                nextPoly[j + 1] = GF256.add(nextPoly[j + 1], poly[j]); 
                nextPoly[j] = GF256.add(nextPoly[j], GF256.multiply(poly[j], a_i));
            }
            poly = nextPoly;
        }
        return poly;
    }

    public static byte[] calculateEcBlocks(byte[] dataCodewords, int ecWords) {
        int[] generator = generatePolynomial(ecWords);
        int[] remainder = new int[ecWords];

        for (byte dataByte : dataCodewords) {
            int data = dataByte & 0xFF; 
            int quotientCoefficient = GF256.add(remainder[0], data); 

            // Shift remainder left
            for (int i = 0; i < ecWords - 1; i++) {
                remainder[i] = remainder[i + 1];
            }
            remainder[ecWords - 1] = 0; 

            if (quotientCoefficient != 0) {
                for (int i = 0; i < ecWords; i++) {
                    // *** THE FIX IS HERE ***
                    // We must read the generator in reverse order to match the remainder
                    int genIndex = ecWords - 1 - i;
                    
                    int term = GF256.multiply(quotientCoefficient, generator[genIndex]);
                    remainder[i] = GF256.add(remainder[i], term);
                }
            }
        }

        // Output result
        byte[] ecBlocks = new byte[ecWords];
        for (int i = 0; i < ecWords; i++) {
            ecBlocks[i] = (byte) remainder[i];
        }
        return ecBlocks;
    }
    
    /**
     * Converts a byte array (like ECC blocks) into a continuous string of 
     * '0's and '1's, ensuring each byte is padded to 8 bits.
     */
    public static String bytesToBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 1. Convert to unsigned integer (0-255)
            int unsignedInt = b & 0xFF;
            
            // 2. Get binary string (e.g., "1010100")
            String binary = Integer.toBinaryString(unsignedInt);
            
            // 3. Pad with leading zeros to 8 bits
            String paddedBinary = String.format("%8s", binary).replace(' ', '0');
            
            sb.append(paddedBinary);
        }
        return sb.toString();
    }
}