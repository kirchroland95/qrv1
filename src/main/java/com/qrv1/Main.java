package com.qrv1;

/**
 * Main application class for QR Code V1 Generator
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String data = args[0];
        QRCodeV1.ErrorCorrectionLevel level = QRCodeV1.ErrorCorrectionLevel.M;
        
        // Parse error correction level if provided
        if (args.length > 1) {
            try {
                level = QRCodeV1.ErrorCorrectionLevel.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid error correction level: " + args[1]);
                System.err.println("Valid levels: L, M, Q, H");
                return;
            }
        }
        
        try {
            QRCodeV1 qrCode = new QRCodeV1(data, level);
            System.out.println("QR Code Version 1 - Generated for: " + data);
            System.out.println("Error Correction Level: " + level + " (" + level.getPercentage() + "%)");
            System.out.println();
            System.out.println(qrCode.toString());
        } catch (Exception e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printUsage() {
        System.out.println("QR Code Version 1 Generator");
        System.out.println();
        System.out.println("Usage: java -jar qrv1.jar <data> [error_correction_level]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  data                    The data to encode in the QR code");
        System.out.println("  error_correction_level  Error correction level (L, M, Q, H). Default: M");
        System.out.println();
        System.out.println("Error Correction Levels:");
        System.out.println("  L - Low (7% error correction)");
        System.out.println("  M - Medium (15% error correction)");
        System.out.println("  Q - Quartile (25% error correction)");
        System.out.println("  H - High (30% error correction)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar qrv1.jar \"HELLO WORLD\"");
        System.out.println("  java -jar qrv1.jar \"12345\" L");
        System.out.println("  java -jar qrv1.jar \"https://example.com\" H");
    }
}
