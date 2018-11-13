import java.util.Scanner;

public class A {
	private static void displayNumber(int NUM) {
		for (int i = Integer.SIZE - 1; i >= 0; i--) {
			int b = (NUM >> i) & 0x01;
			System.out.print(b + " ");
		}
		System.out.println();
	}

	private static void displayNumberAsByte(int NUM) {
		for (int i = Byte.SIZE - 1; i >= 0; i--) {
			int b = (NUM >> i) & 0x01;
			System.out.print(b + " ");
		}
		System.out.println();
	}

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		System.out.print("Please give me a number (try 567890667): ");
		int N = in.nextInt();
		in.close();

		System.out.println("\nAn integer is " + Integer.SIZE + " bits long.");
		System.out.println("A byte is     " + Byte.SIZE + " bits long.\n");

		displayNumber(N);

		//////////////////////////////////////////////////////

		int a = (N >> 24) & 0xFF;
		int r = (N >> 16) & 0xFF;
		int g = (N >> 8) & 0xFF;
		int b = (N >> 0) & 0xFF;

		System.out.println();

		displayNumber(a);
		displayNumber(r);
		displayNumber(g);
		displayNumber(b);

		System.out.println();

		displayNumberAsByte(a);
		displayNumberAsByte(r);
		displayNumberAsByte(g);
		displayNumberAsByte(b);

		System.out.println();

		displayNumberAsByte(N);
		byte Nbyte = (byte) N; // use int as a byte (uses only 8 last bits)
		displayNumberAsByte(Nbyte);
		int NintAsByte = (byte) N; // (byte in an int) - Storage is 32 bits, but store a byte
		displayNumber(NintAsByte);

		System.out.println();

		int newNumber = (r << 24) | (b << 16) | (g << 8) | (a << 0);
		displayNumber(newNumber);
	}
}
