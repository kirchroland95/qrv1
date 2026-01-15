import java.nio.charset.StandardCharsets;

public class QrV1 {
	private String text = "Ver1";
	private int textLength;
	private int size = 23;
	private int[][] qr = new int[size][size]; // 0 = white, 1 = black
	private int errorCorrectionLevel = 1; // ECC low
	private int maskPattern = 1;
	private int formatPattern = 0;
	private int encoding = 4;
	private int delay = 110;
	private boolean isAnimationEnabled = false;

	private static final int[] EC_CAPACITY_BYTES = { 10, // M (Medium - errorCorrectionLevel 0)
			7, // L (Low - errorCorrectionLevel 1)
			17, // H (High - errorCorrectionLevel 2)
			13 // Q (Quartile - errorCorrectionLevel 3)
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
		// Choose encoding
		// TODO add numeric, alphanumeric, byte and kanji check
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
	}

	public void buildQr(String text) {
		setText(text);
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

	// Come back to this and figure out if you want to pass the reference or create
	// a new qr, we could modify the passed one as it is fine if it is modified
	private int[][] FormatStrips(int [][] newQr, int mask, boolean animate) {
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
				if(animate) {					
					clearScreenWithDelay(delay);
				}
			}
		}
		shift = 14;
		for (int i = 21; i >= 1; i--) {
			if (allocatedPositions[i][9] == 5) {
				newQr[i][9] = (formatPattern >> shift) & 1;
				shift -= 1;
				if(animate) {
					clearScreenWithDelay(delay);					
				}
			}
		}
		return newQr;
	}

	private void fillData() {
		// Create combined String of data from mode + count + data + terminator + EC in
		// bits format using only 1 and 0
		String data = "";

		// 1. Add mode bit by bit as characters after casting to binary using only 4
		// bits, currently supporting only Byte
		data += String.format("%04d", Integer.parseInt(Integer.toBinaryString(encoding)));

		// 2. Add the text length
		data += String.format("%08d", Integer.parseInt(Integer.toBinaryString(textLength)));

		// 3. Add the data
		data += textToBinary(text);

		// 4. Add Terminator 0000 bits to signify end of message
		data += "0000";

		// 5. Calculate EC
		byte[] dataCodewords = binaryStringToBytes(data);
		byte[] ecBlocks = ReedSolomonEncoder.calculateEcBlocks(dataCodewords, EC_CAPACITY_BYTES[errorCorrectionLevel]);

		// 6. Add the EC
		data += ReedSolomonEncoder.bytesToBinaryString(ecBlocks);

		// Add data to QR array
		int counter = 0;
		int row = size - 2;
		int col = size - 2;
		boolean dirup = true;

		while (counter < data.length()) {
			if (allocatedPositions[row][col] == 1 || allocatedPositions[row][col] == 4
					|| allocatedPositions[row][col - 1] == 4) {
				dirup = !dirup;
				row += dirup ? -1 : 1;
				col -= 2;
			}
			if (allocatedPositions[row][col] == 2) {
				row += dirup ? -1 : 1;
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
			row += dirup ? -1 : 1;
			counter += 2;
		}

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

	public void setText(String text) {
		if (text == null) {
			this.text = "";
			this.textLength = 0;
		} else {
			// Truncate the string if it's longer than 17 characters
			if (text.length() > 17) {
				this.text = text.substring(0, 17);
			} else {
				this.text = text;
			}
			this.textLength = this.text.length();
		}
		setErrorCorrectionLevel();
	}

	public void setErrorCorrectionLevel() {
		int mapped;

		if (textLength >= 0 && textLength <= 7) {
			mapped = 7;
		} else if (textLength >= 8 && textLength <= 11) {
			mapped = 11;
		} else if (textLength >= 12 && textLength <= 14) {
			mapped = 14;
		} else if (textLength >= 15 && textLength <= 17) {
			mapped = 17;
		} else {
			mapped = 17; // out of range
		}
		switch (mapped) {
		case 7:
			// H
			errorCorrectionLevel = 2;
			break;
		case 11:
			// Q
			errorCorrectionLevel = 3;
			break;
		case 14:
			// M
			errorCorrectionLevel = 0;
			break;
		case 17:
			// L
			errorCorrectionLevel = 1;
			break;
		default:
			// default to L
			errorCorrectionLevel = 1;
			break;
		}
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

}
