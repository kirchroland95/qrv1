package com.qrv1;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for building bit sequences
 */
class BitBuilder {
    private List<Boolean> bits;
    
    public BitBuilder() {
        this.bits = new ArrayList<>();
    }
    
    public void append(int value, int bitCount) {
        for (int i = bitCount - 1; i >= 0; i--) {
            bits.add(((value >> i) & 1) == 1);
        }
    }
    
    public int length() {
        return bits.size();
    }
    
    public byte[] toByteArray() {
        int byteCount = (bits.size() + 7) / 8;
        byte[] bytes = new byte[byteCount];
        
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                bytes[byteIndex] |= (1 << bitIndex);
            }
        }
        
        return bytes;
    }
}
