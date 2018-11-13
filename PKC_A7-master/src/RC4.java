import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class RC4 {
	public void ksa(byte state[], byte key[], int len) {
		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}

		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (j + btoi(state[i]) + btoi(key[i % len])) % 256;
			byte temp = state[i];
			state[i] = state[j];
			state[j] = temp;
		}
	}

	public void prga(byte state[], String input, String output) {
		byte[] buffer = new byte[1];
		try {
			FileInputStream fis = new FileInputStream(input);
			FileOutputStream fos = new FileOutputStream(output);
			byte result[] = new byte[1];
			byte temp = 0;
			int i = 0;
			int j = 0;
			int K = 0;
			while (fis.read(buffer) == 1) {
				i = (i + 1) % 256;
				j = (j + btoi(state[i])) % 256;
				temp = state[i];
				state[i] = state[j];
				state[j] = temp;
				K = state[(btoi(state[i]) + btoi(state[j])) % 256];
				result[0] = (byte) (buffer[0] ^ K);
				fos.write(result);
			}
			fis.close();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static int btoi(byte b) {
		return b & 0xFF;
	}

	public static void main(String args[]) {
		byte state[] = new byte[256];
		byte key[] = "MyPassword".getBytes();
		int PASS_size = key.length;

		RC4 rc4 = new RC4();

		// ENCRYPT
		rc4.ksa(state, key, PASS_size);
		rc4.prga(state, "a.png", "b.png");

		// DECRYPT
		rc4.ksa(state, key, PASS_size);
		rc4.prga(state, "b.png", "c.png");

		// Files a.png and c.png must be identical.
	}
}