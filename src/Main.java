
public class Main {

	public static void main(String[] args) {
		
		QrV1 qr = new QrV1();

		// Enable animations
		// qr.setAnimationEnabled(true, 250);

		// Test 1: Empty string
		System.out.println("Test 1: Empty string");
		qr.buildQr("");
		
		// Test 2: Single byte
		System.out.println("\nTest 2: Single byte");
		qr.buildQr("a");
		
		// Test 3: Max byte length (19)
		System.out.println("\nTest 3: Max byte length (19)");
		qr.buildQr("a".repeat(19));
		
		// Test 4: Numeric mode
		System.out.println("\nTest 4: Numeric mode");
		qr.buildQr("12345");
		
		// Test 5: Alphanumeric mode
		System.out.println("\nTest 5: Alphanumeric mode");
		qr.buildQr("HELLO");
		
		// Test 6: Single kanji
		System.out.println("\nTest 6: Single kanji");
		qr.buildQr("魔");
	}
}
