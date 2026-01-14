# QR Code Version 1 Generator

A Java implementation of a QR code generator specifically for QR Code Version 1 (21x21 modules).

## Features

- Generates QR Code Version 1 (21x21 modules)
- Supports multiple encoding modes:
  - Numeric (0-9)
  - Alphanumeric (0-9, A-Z, space, $, %, *, +, -, ., /, :)
  - Byte (any data)
- Four error correction levels:
  - L (Low) - 7% error correction
  - M (Medium) - 15% error correction
  - Q (Quartile) - 25% error correction
  - H (High) - 30% error correction
- Reed-Solomon error correction
- Automatic mask pattern selection
- CLI and programmatic API

## Building

Build the project using Maven:

```bash
mvn clean package
```

## Usage

### Command Line

Generate a QR code from the command line:

```bash
java -jar target/qrv1-1.0.0.jar "HELLO WORLD"
```

With custom error correction level:

```bash
java -jar target/qrv1-1.0.0.jar "12345" L
java -jar target/qrv1-1.0.0.jar "DATA" H
```

### Programmatic API

```java
import com.qrv1.QRCodeV1;

// Create QR code with default error correction (M)
QRCodeV1 qrCode = new QRCodeV1("HELLO WORLD");

// Create QR code with specific error correction level
QRCodeV1 qrCode = new QRCodeV1("DATA", QRCodeV1.ErrorCorrectionLevel.H);

// Get the matrix
boolean[][] matrix = qrCode.getMatrix();

// Print to console
System.out.println(qrCode.toString());

// Get ASCII representation
String ascii = qrCode.toASCII();
```

## Testing

Run the test suite:

```bash
mvn test
```

## QR Code Version 1 Specifications

- Size: 21x21 modules
- Maximum data capacity (Error Correction Level L):
  - Numeric: 41 digits
  - Alphanumeric: 25 characters
  - Binary: 17 bytes

## License

This project is open source.