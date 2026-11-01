import java.nio.charset.StandardCharsets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QrV1 {
	private String text = "Ver1";
	private int textLength;
	private int size = 23;
	private int[][] qr = new int[size][size]; // 0 = white, 1 = black
	private int errorCorrectionLevel;
	private int maskPattern;
	private int formatPattern;
	private int encoding;
	private int delay = 110;
	private boolean isAnimationEnabled = false;
	private static final String ALPHANUMERIC_TABLE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";
	private static final int[] EC_CAPACITY_BYTES = { 10, // M (Medium - errorCorrectionLevel 0)
			7, // L (Low - errorCorrectionLevel 1)
			17, // H (High - errorCorrectionLevel 2)
			13 // Q (Quartile - errorCorrectionLevel 3)
	};

	private static final int[] DATA_CAPACITY_BYTES = { 16, // M
			19, // L
			9, // H
			13 // Q
	};

	private int[][] pattern = { { 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 },
			{ 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 1, 1 } };

	private int[][] allocatedPositions = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 4, 4, 4, 4, 4, 4, 3, 4, 5, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };

	public QrV1() {
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				qr[r][c] = 0; // set every cell to white
			}
		}
	}

	public void buildQr() {
		// Draw orientation corners
		drawCorner();
		// Draw timing pattern
		drawTiming();
		// Set text and choose encoding
		setText(text);
		// Add encoding, length and message
		fillData();
		// Choose best mask
		maskPattern = selectBestMask();
		// Add Format
		qr = FormatStrips(qr, maskPattern, isAnimationEnabled);
		// Mask
		qr = maskData(qr, maskPattern, isAnimationEnabled);

		// Show QR in terminal
		printQR();

		// Save as PNG
		try {
			saveQrAsImage("myqr.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void buildQr(String text) {
		this.text = text;
		buildQr();
	}

	private void drawCorner() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				// UpperLeft
				qr[i + 1][j + 1] = pattern[i][j];
				// UpperRight
				qr[i + 1][15 + j] = pattern[i][j];
				// LowerLeft
				qr[15 + i][j + 1] = pattern[i][j];
				// Default block that is on only on this corner
				if (qr[i + 1][j + 1] == 1)
					clearScreenWithDelay(delay);
			}
		}
		qr[14][9] = 1;
		clearScreenWithDelay(delay);
	}

	private void drawTiming() {
		for (int i = 8; i < 14; i++) {
			// On pair numbers, 1, else 0
			if (i % 2 == 1) {
				qr[7][i] = 1;
				qr[i][7] = 1;
				clearScreenWithDelay(delay);
			}
		}
	}

	public void setText(String text) {
		int maxLength = 0;
		this.text = text;
		bestEncoding();
		switch (this.encoding) {
		case 1:
			maxLength = 41;
			break;
		case 2:
			maxLength = 25;
			break;
		case 4:
			maxLength = 17;
			break;

		case 8:
			maxLength = 10;
			break;
		}
		if (text == null) {
			this.text = "";
			this.textLength = 0;
		} else {
			// Truncate the string if it's longer than 17 characters
			if (text.length() > maxLength) {
				this.text = text.substring(0, maxLength);
			} else {
				this.text = text;
			}
			this.textLength = this.text.length();
		}
		setErrorCorrectionLevel();
	}

	public void setErrorCorrectionLevel() {
		switch (this.encoding) {
		case 1:
			setNumericErrorCorrection();
			break;
		case 2:
			// Alphanumeric
			setAlphanumericErrorCorrection();
			break;
		case 4:
			// Byte
			setByteErrorCorrection();
			break;
		case 8:
			// Kanji
			setKanjiErrorCorrection();
			break;
		default:
			// default to Byte
			setByteErrorCorrection();
			break;
		}

	}

	private void setNumericErrorCorrection() {
		// Numeric
		if (textLength >= 0 && textLength <= 17) {
			// H
			errorCorrectionLevel = 2;
		} else if (textLength >= 18 && textLength <= 27) {
			// Q
			errorCorrectionLevel = 3;
		} else if (textLength >= 28 && textLength <= 34) {
			// M
			errorCorrectionLevel = 0;
		} else if (textLength >= 35) {
			// L
			errorCorrectionLevel = 1;
		}
	}

	private void setAlphanumericErrorCorrection() {
		// Alphanumeric
		if (textLength >= 0 && textLength <= 10) {
			// H
			errorCorrectionLevel = 2;
		} else if (textLength >= 11 && textLength <= 16) {
			// Q
			errorCorrectionLevel = 3;
		} else if (textLength >= 17 && textLength <= 20) {
			// M
			errorCorrectionLevel = 0;
		} else if (textLength >= 21) {
			// L
			errorCorrectionLevel = 1;
		}
	}

	private void setByteErrorCorrection() {
		if (textLength >= 0 && textLength <= 7) {
			// H
			errorCorrectionLevel = 2;
		} else if (textLength >= 8 && textLength <= 11) {
			// Q
			errorCorrectionLevel = 3;
		} else if (textLength >= 12 && textLength <= 14) {
			// M
			errorCorrectionLevel = 0;
		} else if (textLength >= 15) {
			// L
			errorCorrectionLevel = 1;
		}
	}

	private void setKanjiErrorCorrection() {
		if (textLength >= 0 && textLength <= 4) {
			// H
			errorCorrectionLevel = 2;
		} else if (textLength >= 5 && textLength <= 7) {
			// Q
			errorCorrectionLevel = 3;
		} else if (textLength == 8) {
			// M
			errorCorrectionLevel = 0;
		} else if (textLength >= 9) {
			// L
			errorCorrectionLevel = 1;
		}
	}

	private void bestEncoding() {
		// 1. Numeric
		if (text.matches("[0-9]+")) {
			encoding = 1; // Mode Indicator: 0001
		}

		// 2. Alphanumeric
		else if (text.matches("[0-9A-Z $%*+\\-./:]+")) {
			encoding = 2; // Mode Indicator: 0010
		}

		// 3. Kanji: Requires special Shift-JIS range checking
		// Most generators skip this unless explicitly requested
		else if (isKanji(text)) {
			encoding = 8; // Mode Indicator: 1000
		}

		// 4. Byte: Should cover the rest
		else {
			encoding = 4; // Mode Indicator: 0100
		}
	}

	private boolean isKanji(String text) {
		try {
			byte[] bytes = text.getBytes("Shift_JIS");
			int i = 0;
			while (i < bytes.length) {
				int byte1 = bytes[i] & 0xFF;
				if (i + 1 < bytes.length) {
					int byte2 = bytes[i + 1] & 0xFF;
					// Check if the pair falls in the Shift-JIS Kanji ranges:
					// 0x8140-0x9FFC or 0xE040-0xEBBF
					boolean inRange1 = (byte1 >= 0x81 && byte1 <= 0x9F) && (byte2 >= 0x40 && byte2 <= 0xFC);
					boolean inRange2 = (byte1 >= 0xE0 && byte1 <= 0xEB) && (byte2 >= 0x40 && byte2 <= 0xBB);

					if (!inRange1 && !inRange2)
						return false;
					i += 2;
				} else {
					return false; // Kanji must be pairs
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void fillData() {
		switch (this.encoding) {
		case 1: // Numeric Mode (Indicator: 0001)
			fillDataNumeric();
			break;

		case 2: // Alphanumeric Mode (Indicator: 0010)
			fillDataAlphaNumeric();
			break;

		case 4: // Byte Mode (Indicator: 0100)
			fillDataByte();
			break;

		case 8: // Kanji Mode (Indicator: 1000)
			fillDataKanji();
			break;

		default:
			throw new IllegalArgumentException("Unsupported encoding mode: " + encoding);
		}
	}

	private void fillDataNumeric() {
		// Create combined String of data from mode(4bit) + count(10bit) + data(max137)
		// + terminator(1or4) + padding(until capacity) + EC
		// in bits format using only 1 and 0
		String data = "";

		// 1. Add mode bit by bit
		data += String.format("%4s", Integer.toBinaryString(encoding)).replace(' ', '0');

		// 2. Add the text length
		data += String.format("%10s", Integer.toBinaryString(textLength)).replace(' ', '0');

		// 3. Add the data
		data += NumericToBinary(text);

		// 4. Add Terminator 0 or 0000 bits to signify end of message
		if (data.length() == 151) {
			// Special case for 41 numbers, adding the 4 bit terminator
			// will increase the size from 151 to 155
			// while we only have 152 bits to use
			// Add only one 0 as a terminator
			data += "0";
		} else {
			data += "0000";
		}
		while (data.length() % 8 != 0) {
			data += "0";
		}

		// 5. Add padding to fill the data to it's capacity
		int targetBits = DATA_CAPACITY_BYTES[errorCorrectionLevel] * 8;
		String pad1 = "11101100"; // 236
		String pad2 = "00010001"; // 17
		boolean usePad1 = true;

		while (data.length() < targetBits) {
			data += (usePad1) ? pad1 : pad2;
			usePad1 = !usePad1;
		}

		// 6. Calculate EC
		byte[] dataCodewords = binaryStringToBytes(data);
		byte[] ecBlocks = ReedSolomonEncoder.calculateEcBlocks(dataCodewords, EC_CAPACITY_BYTES[errorCorrectionLevel]);

		// 7. Add the EC
		data += ReedSolomonEncoder.bytesToBinaryString(ecBlocks);

		// Add data to QR array
		int counter = 0;
		int row = size - 2;
		int col = size - 2;
		boolean dirUp = true;

		while (counter < data.length()) {
			if (allocatedPositions[row][col] == 1 || allocatedPositions[row][col] == 4
					|| allocatedPositions[row][col - 1] == 4) {
				dirUp = !dirUp;
				row += dirUp ? -1 : 1;
				col -= 2;
			}
			if (allocatedPositions[row][col] == 2) {
				row += dirUp ? -1 : 1;
			}
			if (allocatedPositions[row][col] == 3) {
				col -= 1;
			}
			if (allocatedPositions[row][col] == 5) {
				row = 13;
				col = 9;
			}
			qr[row][col] = (data.charAt(counter) == '1') ? 1 : 0;
			if (qr[row][col] == 1)
				clearScreenWithDelay(delay);
			qr[row][col - 1] = (data.charAt(counter + 1) == '1') ? 1 : 0;
			if (qr[row][col - 1] == 1)
				clearScreenWithDelay(delay);
			row += dirUp ? -1 : 1;
			counter += 2;
		}
	}

	// Converts the input text into a sequence of bits
	private String NumericToBinary(String text) {
		StringBuilder sb = new StringBuilder();
		while (text.length() > 0) {
			if (text.length() >= 3) {
				String chunk = text.substring(0, 3);
				int value = Integer.parseInt(chunk);
				sb.append(String.format("%10s", Integer.toBinaryString(value)).replace(' ', '0')); // 3 digits = 10 bits
				text = text.substring(3); // Cut them off
			} else if (text.length() == 2) {
				int value = Integer.parseInt(text);
				sb.append(String.format("%7s", Integer.toBinaryString(value)).replace(' ', '0')); // 2 digits = 7 bits
				text = text.substring(2); // Cut them off
			} else {
				int value = Integer.parseInt(text);
				sb.append(String.format("%4s", Integer.toBinaryString(value)).replace(' ', '0')); // 1 digits = 4 bits
				text = text.substring(1); // Cut them off
			}
		}

		return sb.toString();
	}

	private void fillDataAlphaNumeric() {
		// Create combined String of data from mode(4bit) + count(9bit) + data(138) +
		// terminator(variable) + padding(until capacity) + EC
		// in bits format using only 1 and 0
		String data = "";

		// 1. Add mode bit by bit
		data += String.format("%4s", Integer.toBinaryString(encoding)).replace(' ', '0');

		// 2. Add the text length
		data += String.format("%9s", Integer.toBinaryString(textLength)).replace(' ', '0');

		// 3. Add the data
		data += AlphaNumericToBinary(text);

		// 4. Add Terminator 0 or 0000 bits to signify end of message
		int targetBits = DATA_CAPACITY_BYTES[errorCorrectionLevel] * 8;
		int remainingBits = targetBits - data.length();
		int terminatorBits = Math.min(4, remainingBits);
		for (int i = 0; i < terminatorBits; i++) {
			data += "0";
		}

		// 5. Add padding to fill the data to it's capacity
		while (data.length() % 8 != 0 && data.length() < targetBits) {
			data += "0";
		}
		String pad1 = "11101100"; // 236
		String pad2 = "00010001"; // 17
		boolean usePad1 = true;

		while (data.length() < targetBits) {
			data += (usePad1) ? pad1 : pad2;
			usePad1 = !usePad1;
		}

		// 6. Calculate EC
		byte[] dataCodewords = binaryStringToBytes(data);
		byte[] ecBlocks = ReedSolomonEncoder.calculateEcBlocks(dataCodewords, EC_CAPACITY_BYTES[errorCorrectionLevel]);

		// 7. Add the EC
		data += ReedSolomonEncoder.bytesToBinaryString(ecBlocks);

		// Add data to QR array
		int counter = 0;
		int row = size - 2;
		int col = size - 2;
		boolean dirUp = true;

		while (counter < data.length()) {
			if (allocatedPositions[row][col] == 1 || allocatedPositions[row][col] == 4
					|| allocatedPositions[row][col - 1] == 4) {
				dirUp = !dirUp;
				row += dirUp ? -1 : 1;
				col -= 2;
			}
			if (allocatedPositions[row][col] == 2) {
				row += dirUp ? -1 : 1;
			}
			if (allocatedPositions[row][col] == 3) {
				col -= 1;
			}
			if (allocatedPositions[row][col] == 5) {
				row = 13;
				col = 9;
			}
			qr[row][col] = (data.charAt(counter) == '1') ? 1 : 0;
			if (qr[row][col] == 1)
				clearScreenWithDelay(delay);
			qr[row][col - 1] = (data.charAt(counter + 1) == '1') ? 1 : 0;
			if (qr[row][col - 1] == 1)
				clearScreenWithDelay(delay);
			row += dirUp ? -1 : 1;
			counter += 2;
		}

	}

	// Converts the input text into a sequence of bits
	private String AlphaNumericToBinary(String text) {
		StringBuilder sb = new StringBuilder();
		while (text.length() > 0) {
			if (text.length() >= 2) {
				int firstLetter = ALPHANUMERIC_TABLE.indexOf(text.charAt(0));
				int secondLetter = ALPHANUMERIC_TABLE.indexOf(text.charAt(1));
				int value = firstLetter * 45 + secondLetter;
				sb.append(String.format("%11s", Integer.toBinaryString(value)).replace(' ', '0')); // 2 char = 11 bits
				text = text.substring(2); // Cut them off
			} else {
				int lastLetter = ALPHANUMERIC_TABLE.indexOf(text.charAt(0));
				sb.append(String.format("%6s", Integer.toBinaryString(lastLetter)).replace(' ', '0')); // 1 char = 6
																										// bits
				text = ""; // empty
			}
		}

		return sb.toString();
	}

	private void fillDataByte() {
		// Create combined String of data from mode(4) + count(8) + data + terminator(4)
		// + padding(until capacity) + EC
		// in bits format using only 1 and 0
		String data = "";

		// 1. Add mode bits
		data += String.format("%4s", Integer.toBinaryString(encoding)).replace(' ', '0');

		// 2. Add the text length
		data += String.format("%8s", Integer.toBinaryString(textLength)).replace(' ', '0');

		// 3. Add the data
		data += textToBinary(text);

		// 4. Add Terminator 0000 bits to signify end of message
		data += "0000";

		// 5. Add padding to fill the data to it's capacity
		int targetBits = DATA_CAPACITY_BYTES[errorCorrectionLevel] * 8;
		String pad1 = "11101100"; // 236
		String pad2 = "00010001"; // 17
		boolean usePad1 = true;

		while (data.length() < targetBits) {
			data += (usePad1) ? pad1 : pad2;
			usePad1 = !usePad1;
		}

		// 6. Calculate EC
		byte[] dataCodewords = binaryStringToBytes(data);
		byte[] ecBlocks = ReedSolomonEncoder.calculateEcBlocks(dataCodewords, EC_CAPACITY_BYTES[errorCorrectionLevel]);

		// 7. Add the EC
		data += ReedSolomonEncoder.bytesToBinaryString(ecBlocks);

		// Add data to QR array
		int counter = 0;
		int row = size - 2;
		int col = size - 2;
		boolean dirUp = true;

		while (counter < data.length()) {
			if (allocatedPositions[row][col] == 1 || allocatedPositions[row][col] == 4
					|| allocatedPositions[row][col - 1] == 4) {
				dirUp = !dirUp;
				row += dirUp ? -1 : 1;
				col -= 2;
			}
			if (allocatedPositions[row][col] == 2) {
				row += dirUp ? -1 : 1;
			}
			if (allocatedPositions[row][col] == 3) {
				col -= 1;
			}
			if (allocatedPositions[row][col] == 5) {
				row = 13;
				col = 9;
			}
			qr[row][col] = (data.charAt(counter) == '1') ? 1 : 0;
			if (qr[row][col] == 1)
				clearScreenWithDelay(delay);
			qr[row][col - 1] = (data.charAt(counter + 1) == '1') ? 1 : 0;
			if (qr[row][col - 1] == 1)
				clearScreenWithDelay(delay);
			row += dirUp ? -1 : 1;
			counter += 2;
		}

	}

	// Converts the input text into a sequence of 8-bit binary strings (Byte Mode)

	private String textToBinary(String text) {
		// Get the bytes representing the characters
		byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);

		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			// Use Integer.toBinaryString to convert the byte to a binary string.
			String binaryString = Integer.toBinaryString(b & 0xFF);

			// Pad the resulting binary string with leading zeros
			sb.append(String.format("%08d", Integer.parseInt(binaryString)));
		}
		return sb.toString();
	}

	private void fillDataKanji() {
		// Create combined String of data from mode(4) + count(8) + data +
		// terminator(variable) +
		// + padding(until capacity) + EC
		// in bits format using only 1 and 0
		String data = "";

		// 1. Add mode bits (Kanji)
		data += String.format("%4s", Integer.toBinaryString(encoding)).replace(' ', '0');

		// 2. Calculate Length as 1 kanji character is 2 bytes
		byte[] bytes;
		try {
			bytes = text.getBytes("Shift_JIS");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (bytes.length % 2 != 0) {
			throw new IllegalArgumentException("Invalid Shift_JIS length for Kanji mode");
		}

		int kanjiCharCount = bytes.length / 2;

		// 3. Add the text length
		data += (String.format("%8s", Integer.toBinaryString(kanjiCharCount)).replace(' ', '0'));

		// 4. Encode Kanji
		for (int i = 0; i < bytes.length; i += 2) {
			// Get the two bytes
			int b1 = bytes[i] & 0xFF;
			int b2 = bytes[i + 1] & 0xFF;

			// Combine the two bytes
			int sjis = (b1 << 8) | b2;

			// Validate Shift JIS ranges
			if (!((sjis >= 0x8140 && sjis <= 0x9FFC) || (sjis >= 0xE040 && sjis <= 0xEBBF))) {
				throw new IllegalArgumentException("Invalid Kanji character for QR");
			}

			int subtractor = (sjis <= 0x9FFC) ? 0x8140 : 0xC140;
			int value = sjis - subtractor;

			int finalValue = ((value >> 8) * 0xC0) + (value & 0xFF);

			data += (String.format("%13s", Integer.toBinaryString(finalValue)).replace(' ', '0'));
		}

		int targetBits = DATA_CAPACITY_BYTES[errorCorrectionLevel] * 8;

		// 5. Terminator (max 4 bits, no overflow)
		int remaining = targetBits - data.length();
		int terminatorLength = Math.min(4, Math.max(0, remaining));
		data += ("0".repeat(terminatorLength));

		// 6. Pad to byte boundary
		while (data.length() % 8 != 0) {
			data += ('0');
		}

		// 7. Add padding to fill the data to it's capacity (236 / 17)
		String pad1 = "11101100";
		String pad2 = "00010001";
		boolean usePad1 = true;

		while (data.length() + 8 <= targetBits) {
			data += (usePad1 ? pad1 : pad2);
			usePad1 = !usePad1;
		}

		// 8. Calculate EC
		byte[] dataCodewords = binaryStringToBytes(data.toString());
		byte[] ecBlocks = ReedSolomonEncoder.calculateEcBlocks(dataCodewords, EC_CAPACITY_BYTES[errorCorrectionLevel]);

		// 9. Add the EC
		data += ReedSolomonEncoder.bytesToBinaryString(ecBlocks);

		// Add data to QR array
		int counter = 0;
		int row = size - 2;
		int col = size - 2;
		boolean dirUp = true;

		while (counter < data.length()) {
			if (allocatedPositions[row][col] == 1 || allocatedPositions[row][col] == 4
					|| allocatedPositions[row][col - 1] == 4) {
				dirUp = !dirUp;
				row += dirUp ? -1 : 1;
				col -= 2;
			}
			if (allocatedPositions[row][col] == 2) {
				row += dirUp ? -1 : 1;
			}
			if (allocatedPositions[row][col] == 3) {
				col -= 1;
			}
			if (allocatedPositions[row][col] == 5) {
				row = 13;
				col = 9;
			}
			qr[row][col] = (data.charAt(counter) == '1') ? 1 : 0;
			if (qr[row][col] == 1)
				clearScreenWithDelay(delay);
			qr[row][col - 1] = (data.charAt(counter + 1) == '1') ? 1 : 0;
			if (qr[row][col - 1] == 1)
				clearScreenWithDelay(delay);
			row += dirUp ? -1 : 1;
			counter += 2;
		}
	}

	private int selectBestMask() {
		int bestMask = 0;
		int lowestPenalty = Integer.MAX_VALUE;

		for (int mask = 0; mask < 8; mask++) {
			int penalty = 0;
			int[][] newQr = new int[qr.length][];
			for (int i = 0; i < qr.length; i++) {
				newQr[i] = qr[i].clone();
			}
			newQr = FormatStrips(newQr, mask, false);
			newQr = maskData(newQr, mask, false);
			// Calculate penalties
			penalty += calculateRule1(newQr);
			penalty += calculateRule2(newQr);
			penalty += calculateRule3(newQr);
			penalty += calculateRule4(newQr);

			if (lowestPenalty > penalty) {
				lowestPenalty = penalty;
				bestMask = mask;
			}
		}
		return bestMask;
	}

	private int[][] FormatStrips(int[][] newQr, int mask, boolean animate) {
		// Add 2 bits
		int fiveBitFormat = errorCorrectionLevel; // 000xx

		// Add next 3 bits
		fiveBitFormat = (fiveBitFormat << 3) | mask; // xxyyy

		// Generator polynomial: x^10 + x^8 + x^5 + x^4 + x^2 + x^1 + 1
		int genPoly = 0b10100110111;

		// Create a 15-bit format of the 5 bits
		int format = fiveBitFormat << 10;
		int shift = 0;
		// While the format length is greater than 10
		while (bitLength(format) > 10) {
			shift = bitLength(format) - 11;
			format ^= (genPoly << shift);
		}

		// Get last 10 bits for error correction
		int ec = format & 0x3FF;

		// 3. Append initial 5 bits with ec
		int combined = (fiveBitFormat << 10) | ec;

		// 4. Apply mask
		formatPattern = combined ^ 0b101010000010010;

		shift = 14;
		for (int i = 1; i <= 21; i++) {
			if (allocatedPositions[9][i] == 4) {
				newQr[9][i] = (formatPattern >> shift) & 1;
				shift -= 1;
				if (animate) {
					clearScreenWithDelay(delay);
				}
			}
		}
		shift = 14;
		for (int i = 21; i >= 1; i--) {
			if (allocatedPositions[i][9] == 5) {
				newQr[i][9] = (formatPattern >> shift) & 1;
				shift -= 1;
				if (animate) {
					clearScreenWithDelay(delay);
				}
			}
		}
		return newQr;
	}

	private int[][] maskData(int[][] newQr, int mask, boolean animate) {

		for (int i = 1; i <= 21; i++) {
			for (int j = 1; j <= 21; j++) {
				if (allocatedPositions[i][j] == 0 && maskBlock(i - 1, j - 1, mask)) {
					newQr[i][j] ^= 1;
					if (animate) {
						clearScreenWithDelay(delay);
					}
				}
			}
		}
		return newQr;
	}

	private int calculateRule1(int[][] calcQr) {
		int penalty = 0;
		int size = calcQr.length;

		// Check rows
		for (int row = 1; row < size - 1; row++) {
			int sequence = 0;
			int current = -1;
			for (int col = 1; col < size - 1; col++) {
				if (current == calcQr[row][col]) {
					sequence++;
				} else {
					if (sequence >= 5) {
						penalty += 3 + (sequence - 5);
					}
					current = calcQr[row][col];
					sequence = 1;
				}
			}
			if (sequence >= 5) {
				penalty += 3 + (sequence - 5);
			}
		}

		// Check columns
		for (int col = 1; col < size - 1; col++) {
			int sequence = 0;
			int current = -1;
			for (int row = 1; row < size - 1; row++) {
				if (current == calcQr[row][col]) {
					sequence++;
				} else {
					if (sequence >= 5) {
						penalty += 3 + (sequence - 5);
					}
					current = calcQr[row][col];
					sequence = 1;
				}
			}
			if (sequence >= 5) {
				penalty += 3 + (sequence - 5);
			}
		}
		return penalty;
	}

	private int calculateRule2(int[][] calcQr) {
		int penalty = 0;
		int size = calcQr.length;
		int sum = 0;
		for (int row = 1; row < size - 2; row++) {
			for (int col = 1; col < size - 2; col++) {
				sum = calcQr[row][col] + calcQr[row][col + 1] + calcQr[row + 1][col] + calcQr[row + 1][col + 1];
				if (sum == 0 || sum == 4) {
					penalty += 3;
				}
			}
		}
		return penalty;
	}

	private int calculateRule3(int[][] calcQr) {
		int penalty = 0;
		int size = calcQr.length;
		String pattern1 = "10111010000";
		String pattern2 = "00001011101";

		// Horizontal
		for (int row = 1; row < size - 1; row++) {
			for (int col = 1; col < size - 11; col++) {
				StringBuilder sequence = new StringBuilder();
				for (int i = 0; i < 11; i++) {
					sequence.append(calcQr[row][col + i]);
				}
				String pattern = sequence.toString();
				if (pattern.equals(pattern1) || pattern.equals(pattern2)) {
					penalty += 40;
				}
			}
		}

		// Vertical
		for (int col = 1; col < size - 1; col++) {
			for (int row = 1; row < size - 11; row++) {
				StringBuilder sequence = new StringBuilder();
				for (int i = 0; i < 11; i++) {
					sequence.append(calcQr[row + i][col]);
				}
				String pattern = sequence.toString();
				if (pattern.equals(pattern1) || pattern.equals(pattern2)) {
					penalty += 40;
				}
			}
		}

		return penalty;
	}

	private int calculateRule4(int[][] calcQr) {
		// Count the total number of modules in the matrix.
		int size = calcQr.length;
		int blocks = (size - 2) * (size - 2);
		// Count how many dark modules there are in the matrix.
		int count = 0;
		for (int row = 1; row < size - 1; row++) {
			for (int col = 1; col < size - 1; col++) {
				count += calcQr[row][col];
			}
		}
		// Calculate the percent of modules in the matrix
		double percentage = (count * 100.0) / blocks;
		// Determine the previous and next multiple of five of this percent
		int deviation = (int) (Math.abs(percentage - 50.0) / 5.0);

		int penalty = deviation * 10;

		return penalty;
	}

	// Converts a binary string into a byte array. Assumes the string length is a

	private byte[] binaryStringToBytes(String binaryString) {
		int len = binaryString.length();
		byte[] bytes = new byte[len / 8];
		for (int i = 0; i < len; i += 8) {
			// Parse the 8-bit substring as an integer, then cast to byte
			bytes[i / 8] = (byte) Integer.parseInt(binaryString.substring(i, i + 8), 2);
		}
		return bytes;
	}

	// Position of the highest 1-bit
	private static int bitLength(int v) {
		return (v == 0) ? 0 : 32 - Integer.numberOfLeadingZeros(v);
	}

	// Mask row and column combinations according to pattern
	private static boolean maskBlock(int r, int c, int pattern) {
		return switch (pattern) {
		case 0 -> (r + c) % 2 == 0;
		case 1 -> r % 2 == 0;
		case 2 -> c % 3 == 0;
		case 3 -> (r + c) % 3 == 0;
		case 4 -> ((r / 2) + (c / 3)) % 2 == 0;
		case 5 -> (r * c) % 2 + (r * c) % 3 == 0;
		case 6 -> ((((r * c) % 2) + ((r * c) % 3)) % 2) == 0;
		case 7 -> ((((r + c) % 2) + ((r * c) % 3)) % 2) == 0;
		default -> false;
		};
	}

	public void setAnimationEnabled(boolean isAnimationEnabled) {
		this.isAnimationEnabled = isAnimationEnabled;
	}

	public void setAnimationEnabled(boolean isAnimationEnabled, int delay) {
		this.isAnimationEnabled = isAnimationEnabled;
		this.delay = delay;
	}

	public void printQR() {
		System.out.println("\n\n\n");
		for (int i = 0; i < 23; i++) {
			System.out.print(i + "\t\t\t");
			for (int j = 0; j < 23; j++) {
				System.out.print(qr[i][j] == 1 ? "██" : "  ");
			}
			System.out.println();
		}
	}

	void clearScreenWithDelay(long milliseconds) {
		if (isAnimationEnabled == true) {
			// 1. Clear the screen by printing blank lines
			for (int i = 0; i < 30; i++) {
				System.out.println();
			}
			printQR();
			// 2. Pause the thread
			try {
				Thread.sleep(milliseconds);

			} catch (InterruptedException e) {

				Thread.currentThread().interrupt();
				System.err.println("Delay interrupted: " + e.getMessage());
			}
		}

	}

	private void saveQrAsImage(String filename) throws IOException {

		int moduleSize = 20; // pixels per QR module
		int quietZone = 3; // white modules around the QR, 4 needed but 1 already surrounds the qr array

		int imageSize = (size + quietZone * 2) * moduleSize;

		BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = image.createGraphics();

		// White background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, imageSize, imageSize);

		// Black QR modules
		g.setColor(Color.BLACK);

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {

				if (qr[row][col] == 1) {

					int x = (col + quietZone) * moduleSize;
					int y = (row + quietZone) * moduleSize;

					g.fillRect(x, y, moduleSize, moduleSize);
				}
			}
		}

		g.dispose();

		ImageIO.write(image, "png", new File(filename));
	}

}
