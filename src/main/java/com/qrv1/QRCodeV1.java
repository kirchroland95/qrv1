package com.qrv1;

/**
 * QR Code Version 1 Generator
 * Generates QR codes with version 1 (21x21 modules)
 */
public class QRCodeV1 {
    
    public static final int SIZE = 21; // Version 1 QR code is 21x21 modules
    
    private boolean[][] matrix;
    private String data;
    private ErrorCorrectionLevel errorCorrectionLevel;
    
    public enum ErrorCorrectionLevel {
        L(0b01, 7),  // Low - 7% error correction
        M(0b00, 15), // Medium - 15% error correction
        Q(0b11, 25), // Quartile - 25% error correction
        H(0b10, 30); // High - 30% error correction
        
        private final int bits;
        private final int percentage;
        
        ErrorCorrectionLevel(int bits, int percentage) {
            this.bits = bits;
            this.percentage = percentage;
        }
        
        public int getBits() {
            return bits;
        }
        
        public int getPercentage() {
            return percentage;
        }
    }
    
    public enum EncodingMode {
        NUMERIC(0b0001, 10),      // Numeric mode
        ALPHANUMERIC(0b0010, 9),  // Alphanumeric mode
        BYTE(0b0100, 8);          // Byte mode
        
        private final int bits;
        private final int characterCountBits;
        
        EncodingMode(int bits, int characterCountBits) {
            this.bits = bits;
            this.characterCountBits = characterCountBits;
        }
        
        public int getBits() {
            return bits;
        }
        
        public int getCharacterCountBits() {
            return characterCountBits;
        }
    }
    
    public QRCodeV1(String data) {
        this(data, ErrorCorrectionLevel.M);
    }
    
    public QRCodeV1(String data, ErrorCorrectionLevel errorCorrectionLevel) {
        this.data = data;
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.matrix = new boolean[SIZE][SIZE];
        generate();
    }
    
    private void generate() {
        // Initialize matrix
        clearMatrix();
        
        // Add finder patterns (position detection patterns)
        addFinderPatterns();
        
        // Add separators
        addSeparators();
        
        // Add timing patterns
        addTimingPatterns();
        
        // Add dark module
        addDarkModule();
        
        // Encode data
        EncodingMode mode = detectEncodingMode(data);
        byte[] encodedData = encodeData(data, mode);
        byte[] errorCorrectionBytes = generateErrorCorrection(encodedData);
        
        // Place data and error correction
        placeData(encodedData, errorCorrectionBytes);
        
        // Apply best mask pattern
        int bestMask = selectBestMask();
        applyMask(bestMask);
        
        // Add format information
        addFormatInformation(errorCorrectionLevel, bestMask);
    }
    
    private void clearMatrix() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = false;
            }
        }
    }
    
    private void addFinderPatterns() {
        // Top-left finder pattern
        addFinderPattern(0, 0);
        // Top-right finder pattern
        addFinderPattern(0, SIZE - 7);
        // Bottom-left finder pattern
        addFinderPattern(SIZE - 7, 0);
    }
    
    private void addFinderPattern(int row, int col) {
        // Outer 7x7 black border
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                boolean isBlack = (i == 0 || i == 6 || j == 0 || j == 6 || 
                                  (i >= 2 && i <= 4 && j >= 2 && j <= 4));
                matrix[row + i][col + j] = isBlack;
            }
        }
    }
    
    private void addSeparators() {
        // Top-left separator
        for (int i = 0; i < 8; i++) {
            matrix[7][i] = false;
            matrix[i][7] = false;
        }
        // Top-right separator
        for (int i = 0; i < 8; i++) {
            matrix[7][SIZE - 8 + i] = false;
            matrix[i][SIZE - 8] = false;
        }
        // Bottom-left separator
        for (int i = 0; i < 8; i++) {
            matrix[SIZE - 8][i] = false;
            matrix[SIZE - 8 + i][7] = false;
        }
    }
    
    private void addTimingPatterns() {
        for (int i = 8; i < SIZE - 8; i++) {
            matrix[6][i] = (i % 2 == 0);
            matrix[i][6] = (i % 2 == 0);
        }
    }
    
    private void addDarkModule() {
        matrix[4 * 1 + 9][8] = true;
    }
    
    private EncodingMode detectEncodingMode(String data) {
        if (data.matches("\\d+")) {
            return EncodingMode.NUMERIC;
        } else if (data.matches("[0-9A-Z $%*+\\-./:]+")) {
            return EncodingMode.ALPHANUMERIC;
        } else {
            return EncodingMode.BYTE;
        }
    }
    
    private byte[] encodeData(String data, EncodingMode mode) {
        BitBuilder bits = new BitBuilder();
        
        // Add mode indicator
        bits.append(mode.getBits(), 4);
        
        // Add character count
        bits.append(data.length(), mode.getCharacterCountBits());
        
        // Add data
        switch (mode) {
            case NUMERIC:
                encodeNumeric(data, bits);
                break;
            case ALPHANUMERIC:
                encodeAlphanumeric(data, bits);
                break;
            case BYTE:
                encodeByte(data, bits);
                break;
        }
        
        // Add terminator
        bits.append(0, Math.min(4, getDataCapacity() - bits.length()));
        
        // Pad to byte boundary
        while (bits.length() % 8 != 0) {
            bits.append(0, 1);
        }
        
        // Add padding bytes
        byte[] padding = {(byte) 0b11101100, (byte) 0b00010001};
        int padIndex = 0;
        while (bits.length() < getDataCapacity()) {
            bits.append(padding[padIndex % 2] & 0xFF, 8);
            padIndex++;
        }
        
        return bits.toByteArray();
    }
    
    private void encodeNumeric(String data, BitBuilder bits) {
        for (int i = 0; i < data.length(); i += 3) {
            int chunkSize = Math.min(3, data.length() - i);
            String chunk = data.substring(i, i + chunkSize);
            int value = Integer.parseInt(chunk);
            
            if (chunkSize == 3) {
                bits.append(value, 10);
            } else if (chunkSize == 2) {
                bits.append(value, 7);
            } else {
                bits.append(value, 4);
            }
        }
    }
    
    private void encodeAlphanumeric(String data, BitBuilder bits) {
        String alphanumericChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";
        
        for (int i = 0; i < data.length(); i += 2) {
            if (i + 1 < data.length()) {
                int val1 = alphanumericChars.indexOf(data.charAt(i));
                int val2 = alphanumericChars.indexOf(data.charAt(i + 1));
                bits.append(val1 * 45 + val2, 11);
            } else {
                int val = alphanumericChars.indexOf(data.charAt(i));
                bits.append(val, 6);
            }
        }
    }
    
    private void encodeByte(String data, BitBuilder bits) {
        byte[] bytes = data.getBytes();
        for (byte b : bytes) {
            bits.append(b & 0xFF, 8);
        }
    }
    
    private int getDataCapacity() {
        // Data capacity in bits for version 1
        switch (errorCorrectionLevel) {
            case L: return 152;
            case M: return 128;
            case Q: return 104;
            case H: return 72;
            default: return 128;
        }
    }
    
    private byte[] generateErrorCorrection(byte[] data) {
        int eccCount = getErrorCorrectionCount();
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(eccCount);
        return encoder.encode(data);
    }
    
    private int getErrorCorrectionCount() {
        // Error correction codewords for version 1
        switch (errorCorrectionLevel) {
            case L: return 7;
            case M: return 10;
            case Q: return 13;
            case H: return 17;
            default: return 10;
        }
    }
    
    private void placeData(byte[] data, byte[] errorCorrection) {
        byte[] combined = new byte[data.length + errorCorrection.length];
        System.arraycopy(data, 0, combined, 0, data.length);
        System.arraycopy(errorCorrection, 0, combined, data.length, errorCorrection.length);
        
        int bitIndex = 0;
        int totalBits = combined.length * 8;
        
        // Place data in zigzag pattern from bottom-right
        for (int col = SIZE - 1; col > 0; col -= 2) {
            if (col == 6) col--; // Skip timing column
            
            for (int i = 0; i < SIZE; i++) {
                int row = (((col / 2) % 2) == 0) ? (SIZE - 1 - i) : i;
                
                // Place bit in right column
                if (bitIndex < totalBits && !isReserved(row, col)) {
                    int byteIndex = bitIndex / 8;
                    int bitPosition = 7 - (bitIndex % 8);
                    boolean bit = ((combined[byteIndex] >> bitPosition) & 1) == 1;
                    matrix[row][col] = bit;
                    bitIndex++;
                }
                
                // Place bit in left column
                if (bitIndex < totalBits && !isReserved(row, col - 1)) {
                    int byteIndex = bitIndex / 8;
                    int bitPosition = 7 - (bitIndex % 8);
                    boolean bit = ((combined[byteIndex] >> bitPosition) & 1) == 1;
                    matrix[row][col - 1] = bit;
                    bitIndex++;
                }
            }
        }
    }
    
    private boolean isReserved(int row, int col) {
        // Finder patterns and separators
        if ((row < 9 && col < 9) || (row < 9 && col >= SIZE - 8) || (row >= SIZE - 8 && col < 9)) {
            return true;
        }
        // Timing patterns
        if (row == 6 || col == 6) {
            return true;
        }
        // Format information areas
        if ((row == 8 && (col < 9 || col >= SIZE - 8)) || (col == 8 && (row < 9 || row >= SIZE - 7))) {
            return true;
        }
        return false;
    }
    
    private int selectBestMask() {
        int bestMask = 0;
        int lowestPenalty = Integer.MAX_VALUE;
        
        for (int mask = 0; mask < 8; mask++) {
            boolean[][] testMatrix = copyMatrix();
            applyMaskPattern(testMatrix, mask);
            int penalty = calculatePenalty(testMatrix);
            
            if (penalty < lowestPenalty) {
                lowestPenalty = penalty;
                bestMask = mask;
            }
        }
        
        return bestMask;
    }
    
    private void applyMask(int mask) {
        applyMaskPattern(matrix, mask);
    }
    
    private void applyMaskPattern(boolean[][] matrix, int pattern) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isReserved(row, col) && shouldInvert(row, col, pattern)) {
                    matrix[row][col] = !matrix[row][col];
                }
            }
        }
    }
    
    private boolean shouldInvert(int row, int col, int pattern) {
        switch (pattern) {
            case 0: return (row + col) % 2 == 0;
            case 1: return row % 2 == 0;
            case 2: return col % 3 == 0;
            case 3: return (row + col) % 3 == 0;
            case 4: return (row / 2 + col / 3) % 2 == 0;
            case 5: return (row * col) % 2 + (row * col) % 3 == 0;
            case 6: return ((row * col) % 2 + (row * col) % 3) % 2 == 0;
            case 7: return ((row + col) % 2 + (row * col) % 3) % 2 == 0;
            default: return false;
        }
    }
    
    private int calculatePenalty(boolean[][] matrix) {
        int penalty = 0;
        
        // Rule 1: Adjacent modules in row/column
        penalty += calculatePenaltyRule1(matrix);
        
        // Rule 2: Block of modules
        penalty += calculatePenaltyRule2(matrix);
        
        // Rule 3: Specific patterns
        penalty += calculatePenaltyRule3(matrix);
        
        // Rule 4: Balance of dark/light modules
        penalty += calculatePenaltyRule4(matrix);
        
        return penalty;
    }
    
    private int calculatePenaltyRule1(boolean[][] matrix) {
        int penalty = 0;
        
        // Check rows
        for (int row = 0; row < SIZE; row++) {
            int count = 1;
            boolean lastValue = matrix[row][0];
            for (int col = 1; col < SIZE; col++) {
                if (matrix[row][col] == lastValue) {
                    count++;
                } else {
                    if (count >= 5) {
                        penalty += count - 2;
                    }
                    count = 1;
                    lastValue = matrix[row][col];
                }
            }
            if (count >= 5) {
                penalty += count - 2;
            }
        }
        
        // Check columns
        for (int col = 0; col < SIZE; col++) {
            int count = 1;
            boolean lastValue = matrix[0][col];
            for (int row = 1; row < SIZE; row++) {
                if (matrix[row][col] == lastValue) {
                    count++;
                } else {
                    if (count >= 5) {
                        penalty += count - 2;
                    }
                    count = 1;
                    lastValue = matrix[row][col];
                }
            }
            if (count >= 5) {
                penalty += count - 2;
            }
        }
        
        return penalty;
    }
    
    private int calculatePenaltyRule2(boolean[][] matrix) {
        int penalty = 0;
        for (int row = 0; row < SIZE - 1; row++) {
            for (int col = 0; col < SIZE - 1; col++) {
                boolean value = matrix[row][col];
                if (matrix[row][col + 1] == value && 
                    matrix[row + 1][col] == value && 
                    matrix[row + 1][col + 1] == value) {
                    penalty += 3;
                }
            }
        }
        return penalty;
    }
    
    private int calculatePenaltyRule3(boolean[][] matrix) {
        int penalty = 0;
        int[] pattern1 = {1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0};
        int[] pattern2 = {0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1};
        
        // Check rows
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col <= SIZE - 11; col++) {
                if (matchesPattern(matrix, row, col, pattern1, true) || 
                    matchesPattern(matrix, row, col, pattern2, true)) {
                    penalty += 40;
                }
            }
        }
        
        // Check columns
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row <= SIZE - 11; row++) {
                if (matchesPattern(matrix, row, col, pattern1, false) || 
                    matchesPattern(matrix, row, col, pattern2, false)) {
                    penalty += 40;
                }
            }
        }
        
        return penalty;
    }
    
    private boolean matchesPattern(boolean[][] matrix, int row, int col, int[] pattern, boolean horizontal) {
        for (int i = 0; i < pattern.length; i++) {
            boolean expected = pattern[i] == 1;
            boolean actual = horizontal ? matrix[row][col + i] : matrix[row + i][col];
            if (actual != expected) {
                return false;
            }
        }
        return true;
    }
    
    private int calculatePenaltyRule4(boolean[][] matrix) {
        int darkCount = 0;
        int totalModules = SIZE * SIZE;
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (matrix[row][col]) {
                    darkCount++;
                }
            }
        }
        
        int percentage = (darkCount * 100) / totalModules;
        int deviation = Math.abs(percentage - 50) / 5;
        return deviation * 10;
    }
    
    private boolean[][] copyMatrix() {
        boolean[][] copy = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }
    
    private void addFormatInformation(ErrorCorrectionLevel level, int mask) {
        int formatBits = (level.getBits() << 3) | mask;
        int errorCorrection = calculateFormatErrorCorrection(formatBits);
        int format = (formatBits << 10) | errorCorrection;
        format ^= 0b101010000010010; // XOR with fixed pattern
        
        // Place format information
        for (int i = 0; i < 15; i++) {
            boolean bit = ((format >> i) & 1) == 1;
            
            // Top-left
            if (i < 6) {
                matrix[8][i] = bit;
            } else if (i < 8) {
                matrix[8][i + 1] = bit;
            } else if (i < 9) {
                matrix[7][8] = bit;
            } else {
                matrix[14 - i][8] = bit;
            }
            
            // Top-right and bottom-left
            if (i < 8) {
                matrix[SIZE - 1 - i][8] = bit;
            } else {
                matrix[8][SIZE - 15 + i] = bit;
            }
        }
    }
    
    private int calculateFormatErrorCorrection(int data) {
        int polynomial = 0b10100110111;
        int bits = data << 10;
        
        for (int i = 14; i >= 10; i--) {
            if (((bits >> i) & 1) == 1) {
                bits ^= polynomial << (i - 10);
            }
        }
        
        return bits;
    }
    
    public boolean[][] getMatrix() {
        return matrix;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                sb.append(matrix[row][col] ? "██" : "  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public String toASCII() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                sb.append(matrix[row][col] ? "X" : " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
