
public class Main {

	public static void main(String[] args) {
		
		QrV1 qr = new QrV1();
		
		// Enable animations
		qr.setAnimationEnabled(true, 250);
		
		// Set text then build
//		qr.setText("Hello world!");
//		qr.buildQr();
		
		// Set text and build in one go
		qr.buildQr("Hello world!");

	}
}
