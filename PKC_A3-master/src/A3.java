import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

/*
 * To compile: javac A3.java
 * To execute: java A3 (you need to have the sample.png image in the same directory)
 */

public class A3 {
	public static void changeImage(String inFileName, String outFileName) {
		BufferedImage inI = null;
		BufferedImage outI = null;
		try {
			inI = ImageIO.read(new File(inFileName)); // Load input image
			int W = inI.getWidth(); // get the width of the image
			int H = inI.getHeight(); // get the height of the image

			// Create same type output image as the input (but has no content)
			outI = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);

			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					// 1. Get the RGB color of a pixel (code given).
					int rgb = inI.getRGB(x, y);

					// 2. Extract the Red, Green, Blue channels from the input image.
					byte[] argbBytes = getBytes(rgb);
					byte red = argbBytes[1];
					byte green = argbBytes[2];
					byte blue = argbBytes[3];

					// 3. For each channel, if the Most significant bit is 1, set it to 0
					// and if it is 0, set it to 1.
					red = flipBit(red, 7);
					green = flipBit(green, 7);
					blue = flipBit(blue, 7);

					// 4. For each channel, set to 1 bit 6 (bit 0 is the LSB)
					// _ X _ _ _ _ _ _
					// The X above is the bit 6, which is the 7th bit counting from the back!
					red = setBit(red, 6, true);
					green = setBit(green, 6, true);
					blue = setBit(blue, 6, true);

					// 5. For each channel, set to 0 bit 3 (bit 0 is the LSB)
					red = setBit(red, 3, false);
					green = setBit(green, 3, false);
					blue = setBit(blue, 3, false);

					// 6. Construct a new RGB color where:
					// a) alpha -> 0xff
					// b) Red -> Green
					// c) Green -> Blue
					// d) Blue -> Red
					// Format: ARGB
					byte alpha = (byte) 0xFF;
					byte[] newRgbBytes = new byte[] { alpha, blue, red, green };
					int new_rgb = makeInt(newRgbBytes);

					outI.setRGB(x, y, new_rgb); // set the pixel of the output image to new_rgb
				}
			}

			ImageIO.write(outI, "png", new File(outFileName)); // write the image to the output file
		} catch (IOException ee) {
			System.err.println(ee);
			System.exit(-1);
		}
	}

	public static byte[] getBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (num & 0xFF);
		for (int i = 2; i >= 0; i--) {
			num = num >> 8;
			bytes[i] = (byte) (num & 0xFF);
		}
		return bytes;
	}

	public static void testGetBytes() {
		int test = -2689801;
		byte[] testBytes = getBytes(test);
		assert (makeInt(testBytes) == test);
	}

	public static byte flipBit(byte num, int n) {
		num ^= 1 << n;
		return num;
	}

	public static void testFlipBit() {
		byte test = (byte) 0b10010000;
		byte fliped = (byte) 0b01101111;
		for (int i = 0; i < 8; i++) {
			test = flipBit(test, i);
		}
		assert (test == fliped);
	}

	public static byte setBit(byte num, int n, boolean val) {
		if (val) {
			num &= ~(1 << n);
		} else {
			num |= 1 << n;
		}
		return num;
	}

	public static void testSetBit() {
		byte test1 = (byte) 0b10110010;
		byte expected1 = (byte) 0b00000000;
		for (int i = 0; i < 8; i++) {
			test1 = setBit(test1, i, false);
		}
		assert (test1 == expected1);

		byte test2 = (byte) 0b10110010;
		byte expected2 = (byte) 0b11111111;
		for (int i = 0; i < 8; i++) {
			test2 = setBit(test2, i, true);
		}
		assert (test2 == expected2);
	}

	public static boolean getBit(byte num, int n) {
		return (num & (1 << n)) != 0;
	}

	public static void testGetBit() {
		byte test = (byte) 0b10101100;
		assert (getBit(test, 7));
		assert (!getBit(test, 6));
		assert (getBit(test, 5));
		assert (!getBit(test, 4));
		assert (getBit(test, 3));
		assert (getBit(test, 2));
		assert (!getBit(test, 1));
		assert (!getBit(test, 0));
	}

	public static int makeInt(byte[] bytes) {
		int num = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (3 - i) * 8;
			num += (bytes[i] & 0x000000FF) << shift;
		}
		return num;
	}

	public static void testMakeInt() {
		byte[] test = new byte[] { (byte) 0xF0, 0x20, 0x30, 0x40 };
		int result = makeInt(test);
		int expected = 0xF0203040;
		assert (result == expected);

		byte[] expectedBytes = getBytes(result);
		assert (expectedBytes[0] == test[0]);
		assert (expectedBytes[1] == test[1]);
		assert (expectedBytes[2] == test[2]);
		assert (expectedBytes[3] == test[3]);
	}

	public static void printByte(byte num) {
		for (int i = 7; i >= 0; i--) {
			System.out.print(getBit(num, i) ? 1 : 0);
		}
		System.out.println();
	}

	public static void runTests() {
		testGetBytes();
		testGetBit();
		testSetBit();
		testFlipBit();
		testMakeInt();
	}

	public static void main(String[] args) {
		runTests();
		changeImage("sample.png", "out.png");
	}

	// additional convienence methods
	public static boolean[] getBits(int num) {
		boolean[] bits = new boolean[32];
		byte[] bytes = A3.getBytes(num);
		int count = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 8; j++) {
				bits[count] = A3.getBit(bytes[3 - i], j);
				count++;
			}
		}

		return bits;
	}

	public static int makeInt(boolean[] bits) {
		byte[] bytes = new byte[4];
		int count = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 8; j++) {
				bytes[3 - i] = A3.setBit(bytes[3 - i], j, bits[count]);
				count++;
			}
		}

		return makeInt(bytes);
	}
}
