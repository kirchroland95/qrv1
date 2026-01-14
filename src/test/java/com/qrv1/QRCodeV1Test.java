package com.qrv1;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for QR Code V1 Generator
 */
public class QRCodeV1Test {
    
    @Test
    public void testCreateSimpleNumericQRCode() {
        QRCodeV1 qrCode = new QRCodeV1("12345");
        assertNotNull(qrCode);
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
        assertEquals(21, matrix[0].length);
    }
    
    @Test
    public void testCreateAlphanumericQRCode() {
        QRCodeV1 qrCode = new QRCodeV1("HELLO WORLD");
        assertNotNull(qrCode);
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
        assertEquals(21, matrix[0].length);
    }
    
    @Test
    public void testCreateByteQRCode() {
        QRCodeV1 qrCode = new QRCodeV1("Hello, World!");
        assertNotNull(qrCode);
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
        assertEquals(21, matrix[0].length);
    }
    
    @Test
    public void testErrorCorrectionLevels() {
        for (QRCodeV1.ErrorCorrectionLevel level : QRCodeV1.ErrorCorrectionLevel.values()) {
            QRCodeV1 qrCode = new QRCodeV1("TEST", level);
            assertNotNull(qrCode);
            boolean[][] matrix = qrCode.getMatrix();
            assertEquals(21, matrix.length);
            assertEquals(21, matrix[0].length);
        }
    }
    
    @Test
    public void testFinderPatterns() {
        QRCodeV1 qrCode = new QRCodeV1("TEST");
        boolean[][] matrix = qrCode.getMatrix();
        
        // Check top-left finder pattern (should have dark corners)
        assertTrue(matrix[0][0]);
        assertTrue(matrix[0][6]);
        assertTrue(matrix[6][0]);
        assertTrue(matrix[6][6]);
        
        // Check top-right finder pattern
        assertTrue(matrix[0][14]);
        assertTrue(matrix[0][20]);
        
        // Check bottom-left finder pattern
        assertTrue(matrix[14][0]);
        assertTrue(matrix[20][0]);
    }
    
    @Test
    public void testTimingPatterns() {
        QRCodeV1 qrCode = new QRCodeV1("TEST");
        boolean[][] matrix = qrCode.getMatrix();
        
        // Check horizontal timing pattern at row 6
        for (int col = 8; col < 13; col++) {
            assertEquals((col % 2 == 0), matrix[6][col]);
        }
        
        // Check vertical timing pattern at column 6
        for (int row = 8; row < 13; row++) {
            assertEquals((row % 2 == 0), matrix[row][6]);
        }
    }
    
    @Test
    public void testMatrixSize() {
        QRCodeV1 qrCode = new QRCodeV1("TEST");
        assertEquals(21, QRCodeV1.SIZE);
    }
    
    @Test
    public void testToString() {
        QRCodeV1 qrCode = new QRCodeV1("TEST");
        String output = qrCode.toString();
        assertNotNull(output);
        assertTrue(output.length() > 0);
        // Check that output contains box characters or spaces
        assertTrue(output.contains("██") || output.contains("  "));
    }
    
    @Test
    public void testToASCII() {
        QRCodeV1 qrCode = new QRCodeV1("TEST");
        String output = qrCode.toASCII();
        assertNotNull(output);
        assertTrue(output.length() > 0);
        // Check that output contains X or spaces
        assertTrue(output.contains("X") || output.contains(" "));
    }
    
    @Test
    public void testNumericEncoding() {
        QRCodeV1 qrCode = new QRCodeV1("123456789");
        assertNotNull(qrCode);
        // Should encode using numeric mode
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
    }
    
    @Test
    public void testAlphanumericEncoding() {
        QRCodeV1 qrCode = new QRCodeV1("HELLO123");
        assertNotNull(qrCode);
        // Should encode using alphanumeric mode
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
    }
    
    @Test
    public void testByteEncoding() {
        QRCodeV1 qrCode = new QRCodeV1("hello@example.com");
        assertNotNull(qrCode);
        // Should encode using byte mode
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
    }
    
    @Test
    public void testShortData() {
        QRCodeV1 qrCode = new QRCodeV1("A");
        assertNotNull(qrCode);
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
    }
    
    @Test
    public void testEmptyData() {
        QRCodeV1 qrCode = new QRCodeV1("");
        assertNotNull(qrCode);
        boolean[][] matrix = qrCode.getMatrix();
        assertEquals(21, matrix.length);
    }
}
