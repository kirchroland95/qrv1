# QR Code Version 1 Generator

A Java implementation of a QR Code Version 1 generator with animated terminal visualization.

## Overview

This project generates QR codes (Version 1, 21x21 modules) from text input and displays them in the terminal. The implementation includes full encoding, error correction using Reed-Solomon codes, and optimal mask pattern selection according to the QR Code specification.

## Features

- **Multiple encoding modes**: Numeric, Alphanumeric and Byte
- **Automatic encoding selection**: Chooses the most efficient encoding for the input text
- **Reed-Solomon error correction**: Implements error correction with four levels (L, M, Q, H)
- **Optimal mask selection**: Automatically selects the best mask pattern to minimize errors
- **Animated visualization**: Optional step-by-step QR code generation animation in terminal
- **Terminal rendering**: Displays QR codes using Unicode block characters

## Technical Implementation

### Core Components

- **QrV1.java**: Main QR code generator with encoding, masking, and rendering logic
- **ReedSolomonEncoder.java**: Implements Reed-Solomon error correction over GF(256)
- **GF256.java**: Galois Field arithmetic operations using lookup tables
- **Main.java**: Example usage and entry point

### Encoding Modes

The generator supports the following encoding modes and automatically selects the most efficient:
- **Numeric** (0-9): Up to 41 characters
- **Alphanumeric** (0-9, A-Z, space, $%*+-./:): Up to 25 characters
- **Byte** (ISO-8859-1): Up to 17 characters
- **Kanji** (Shift-JIS): Up to 10 characters (not yet supported)

### Error Correction Levels

Error correction level is automatically selected based on text length:
- **H (High)**: ~30% correction - for texts 0-7 characters
- **Q (Quartile)**: ~25% correction - for texts 8-11 characters
- **M (Medium)**: ~15% correction - for texts 12-14 characters
- **L (Low)**: ~7% correction - for texts 15-17 characters

## Usage

```java
QrV1 qr = new QrV1();

// Enable animation (optional)
qr.setAnimationEnabled(true);
// Or specify the animation delay as well
qr.setAnimationEnabled(true, 250);

// Generate QR code
qr.buildQr("Hello world!");
```

## Example Output

![QR Code Example](qr_example.png)

The QR code is displayed in the terminal using Unicode characters, creating a scannable code that can be read by standard QR code scanners.

## License

This project is provided as-is for educational purposes.