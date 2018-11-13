
import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.*;

/**
 * An implementation of the RSA algorithm. It encrypts and decrypts a file. To
 * be used an a demonstration program. It can be split to only encrypt or only
 * decrypt (now does it both).
 * <P>
 * If only the compiled code of this implementation is given to you, you'll need
 * to write a driver program that looks like this:
 * 
 * <pre>
 *
 */
public class RSA_WIT {
	public static void main(String[] args) {

		if (args.length != 3) {
			System.err.println("Provide 3 filenames: plain encrypted decrypted.");
			System.exit(-1);
		}

		RSA_WIT rsa = new RSA_WIT("977447", "649487", "6359"); // see the constructor for details.
		// p q publicKey

		rsa.PrintParameters();

		System.out.println(rsa);

		// rsa.EncryptDecrypt(args[0], args[2]); // encrypt/decrypt on the fly

		rsa.EncryptFile(args[0], args[1]); // Encrypt file
		rsa.DecryptFile(args[1], args[2]); // Decrypt file

		System.out.println("FINISHED");
	}

	private final int NB = 4; // block size for encryption

	private final static BigInteger one = new BigInteger("1");
	private final static SecureRandom random = new SecureRandom();

	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger modulus;
	private BigInteger p = null;
	private BigInteger q = null;
	private BigInteger phi = null;

	/**
	 * Perform encryption
	 */
	private BigInteger encrypt(BigInteger message) {
		return message.modPow(publicKey, modulus); // 2N-bit number
	}

	/**
	 * Perform decryption
	 */
	private BigInteger decrypt(BigInteger encrypted) {
		return encrypted.modPow(privateKey, modulus); // 2N-bit number
	}

	/**
	 * Formats the public and private keys as well as the modulus.
	 * 
	 * @return returns a formated string of containing the public, private and
	 *         modulus.
	 */
	public String toString() {
		String s = "";
		s += "+--------------------------------------\n";
		s += "|  public  = " + publicKey + "\n";
		s += "|  private = " + privateKey + "\n";
		s += "|  modulus = " + modulus + "\n";
		s += "+--------------------------------------";
		return s;
	}

	/**
	 * Print all parameters of of the algorithm used.
	 */
	public void PrintParameters() {
		System.out.println("+--------------------------------------");
		System.out.println("|p:       (" + p.bitLength() + "-bits) " + p);
		System.out.println("|q:       (" + q.bitLength() + "-bits) " + q);
		System.out.println("|phi:     (" + phi.bitLength() + "-bits) " + phi);
		System.out.println("|modulus: (" + modulus.bitLength() + "-bits) " + modulus);
		System.out.println("|PU key:  (" + publicKey.bitLength() + "-bits) " + publicKey);
		System.out.println("|PR key:  (" + privateKey.bitLength() + "-bits) " + privateKey);
		System.out.println("+--------------------------------------");
	}

	/**
	 * Sets up the public, private and modulus.
	 *
	 * <P>
	 * A brief review on exponents:
	 * 
	 * <pre>
	 	 * 0<=x<2^n
		 * 0<=y<2^n
		 * 0<= x*y < 2^n * 2^n which is 0<= x*y < 2^(2n)
		 *
		 * a1 = 2^(2n)
		 * b1 = 2^m
		 * a1 * b1 = 2^(2n) * 2^m = 2^(2n+m)
	 * </pre>
	 * <P>
	 * To generate a (let's say) 20-bit prime number write an execute the following
	 * program:
	 * 
	 * <pre>
	 * import java.math.BigInteger;
	 * import java.util.Random;
	 * import java.security.SecureRandom;
	 * 
	 * public class RandomPrime {
	 * 	public static void main(String[] args) {
	 * 		SecureRandom random = new SecureRandom();
	 * 		BigInteger prime = BigInteger.probablePrime(20, random); // for a 20-bit random prime number
	 * 		System.out.println(prime); // See your number.
	 * 	}
	 * }
	 * </pre>
	 *
	 */
	public RSA_WIT(String the_p, String the_q, String the_public_key) {
		p = new BigInteger(the_p); // N-bit number (20 bits)
		q = new BigInteger(the_q); // N-bit number (20 bits)
		phi = (p.subtract(one)).multiply(q.subtract(one)); // 2N-bit number

		modulus = p.multiply(q); // 2N-bit number (common value in practice = 2^16 + 1)
		publicKey = new BigInteger(the_public_key); // (13 bits)
		privateKey = publicKey.modInverse(phi); // 2N-bit number (same as phi)
	}

	/*
	 * A 40 bit BigInteger is represented as byte[5] (5*8=40bits). However,
	 * toByteArray() needs an additional bit for the sign when converting the
	 * BigInteger into a byte[]. That means that a 40-bit BigInteger needs 6 bytes
	 * to be stored. So, we are dumping into the file 6-byte BigIntegers (or 40/8 +
	 * 1 = 6).
	 */
	private void Write_BigInteger_to_file(FileOutputStream out, BigInteger bi) {
		int chunkSize = modulus.bitLength() / 8 + 1;

		byte[] dataIn = bi.toByteArray();
		byte[] dataOut = new byte[chunkSize];

		if (dataIn.length > chunkSize) { // don't know why we would ever get this!
			System.err.println("SIZE IS: " + dataIn.length);
			System.exit(-1);
		}

		// initialize our array to 0s
		for (int y = 0; y < dataOut.length; y++) {
			dataOut[y] = (byte) 0;
		}

		// convert d array (which has size NB+1 or less into an NB+1 sized array
		for (int i = 0, j = chunkSize - dataIn.length; i < dataIn.length; i++, j++) {
			dataOut[j] = dataIn[i];
		}

		// dump data into the file
		try {
			out.write(dataOut);
			out.flush();
		} catch (IOException ee) {
			System.err.println(ee);
			System.exit(-1);
		}
	}

	/*
	 * Entry point of application that expect 3 file names. <OL> <LI> plaintext file
	 * name (could be a binary file as well) MUST EXIST! <LI> encrypted file name
	 * (encrypted content of the plaintext file) <LI> decrypted file name (decrypt
	 * the encrypted file yielding the plaintext's contents) </OL>
	 */
	/*
	 * public static void main(String[] args) {
	 * 
	 * if( args.length != 3 ) {
	 * System.err.println("Provide 3 filenames: plain encrypted decrypted.");
	 * System.exit(-1); }
	 * 
	 * RSA_WIT rsa = new RSA_WIT("977447", "649487", "6359");
	 * 
	 * 
	 * rsa.PrintParameters();
	 * 
	 * System.out.println(rsa);
	 * 
	 * //rsa.EncryptDecrypt(args[0], args[2]); // encrypt/decrypt on the fly
	 * 
	 * rsa.EncryptFile(args[0], args[1]); // Encrypt file rsa.DecryptFile(args[1],
	 * args[2]); // Decrypt file
	 * 
	 * System.out.println("FINISHED"); }
	 */

	/**
	 * Encrypt/decrypt an input file on the fly. Encrypt the plaintext by a block
	 * and then decrypt that block and dump it in the output file. So, the input and
	 * the output file are identical.
	 * 
	 * @param inf  the name of the plaintext file.
	 * @param outf the name of the file that will be produced and will have the same
	 *             content as the plaintext file.
	 */
	public void EncryptDecrypt(String inf, String outf) {
		FileOutputStream out = null;
		FileInputStream in = null;
		try {
			out = new FileOutputStream(new File(outf));
			in = new FileInputStream(new File(inf));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(-1);
		}

		int w = 0;
		byte[] buffer = new byte[NB];
		try {
			while ((w = in.read(buffer)) != 0) { // read a block of data at a time
				if (w == NB) {
					BigInteger message = new BigInteger(1, buffer); // convert message into a number from byte[],
																	// without the 1, gives negative numbers.
					BigInteger encrypt = encrypt(message); // encrypt
					BigInteger decrypt = decrypt(encrypt); // decrypt right after we encrypt.

					// System.out.println(message.toByteArray().length + " " +
					// encrypt.toByteArray().length + " " + decrypt.toByteArray().length + " " +
					// message + " " + encrypt + " " + decrypt);

					byte[] dd = decrypt.toByteArray(); // get bytes of the decrypted message

					if (dd.length > NB) { // message was originally NB number of bytes
						byte[] gg = new byte[NB]; // if more than NB, get the last NB bytes only.
						for (int r = 0; r < NB; r++)
							gg[r] = (byte) 0; // init array to 0s

						for (int t = NB - 1, q = dd.length - 1; t >= 0 && q >= 0; q--, t--) { // grab only the last NB
																								// bytes
							gg[t] = dd[q];
						}
						out.write(gg); // dump the NB bytes into the output file
					} else if (dd.length == NB) {
						out.write(decrypt.toByteArray()); // correct size.
					} else { // need to dump NB number of byte (pre-pend 0s first)
						byte[] gg = new byte[NB];
						for (int r = 0; r < NB; r++)
							gg[r] = (byte) 0;

						for (int c = NB - 1, t = dd.length - 1; c >= 0 && t >= 0; c--, t--) {
							gg[c] = dd[t];
						}
						out.write(gg);
					}

				} else if (w == -1) {
					// no more data, processed entire file
					break;
				}
				/*
				 * The loop checks for 0 (that we never get). We'll get -1 at the end of the
				 * file - and we break We'll get less than NB and the else part above will
				 * handle it.
				 *
				 * else { // why in the world whould I ever get here? (input file is in blocks
				 * of NB...) BigInteger message = new BigInteger(1, buffer); BigInteger encrypt
				 * = encrypt(message); BigInteger decrypt = decrypt(encrypt);
				 * 
				 * out.write(decrypt.toByteArray());
				 * 
				 * break; }
				 */
			}
		} catch (IOException er) {
			System.err.println(er);
			System.exit(-1);
		}

		/*
		 * Close files
		 */
		try {
			out.close();
			in.close();
		} catch (IOException ee) {
			System.err.println(ee);
			System.exit(-1);
		}
	}

	public void EncryptFile(String inf, String outf) {

		FileOutputStream out = null;
		FileInputStream in = null;
		try {
			out = new FileOutputStream(new File(outf));
			in = new FileInputStream(new File(inf));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(-1);
		}

		int bytesRead = 0;
		byte[] buffer = new byte[NB];
		try {
			int padSize = NB - in.available() % 4;

			while ((bytesRead = in.read(buffer)) != 0) {
				if (bytesRead == NB) {
					BigInteger message = new BigInteger(1, buffer);
					BigInteger encrypt = encrypt(message);
					Write_BigInteger_to_file(out, encrypt);

				} else if (bytesRead == -1) {
					break;

				} else {
					if (padSize + bytesRead != NB) {
						System.err.println("Incorrect pad size: " + padSize);
						System.err.println("bytesRead: " + padSize);
						System.exit(-1);
					}

					byte[] dataOut = new byte[NB];

					for (int i = 0, j = padSize; i < bytesRead; i++, j++) {
						dataOut[j] = buffer[i];
					}

					BigInteger message = new BigInteger(1, dataOut);
					BigInteger encrypt = encrypt(message);
					Write_BigInteger_to_file(out, encrypt);
					break;
				}
			}

			// Append pad size to file
			BigInteger bigintPadSize = BigInteger.valueOf(padSize);
			Write_BigInteger_to_file(out, encrypt(bigintPadSize));

		} catch (IOException er) {
			System.err.println(er);
			System.exit(-1);
		}

		/*
		 * Close files
		 */
		try {
			out.close();
			in.close();
		} catch (IOException ee) {
			System.err.println(ee);
			System.exit(-1);
		}

	}

	/**
	 * Decrypt a file and place it in another file.
	 * 
	 * @param fname   encrypted file name
	 * @param outfile file containing the produced (decrypted) content.
	 */
	public void DecryptFile(String fname, String outfile) {
		FileOutputStream out = null; // OUTPUT FILE (encrypted file)
		FileInputStream in = null; // INPUT FILE (decrypted file)

		try {
			out = new FileOutputStream(new File(outfile));
			in = new FileInputStream(new File(fname));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(-1);
		}

		int chunkSize = modulus.bitLength() / 8 + 1; // calculate how many BYTES the module is (ss).
		byte[] buffer = new byte[chunkSize]; // modulus is 5 bytes (40 bits) - need one more byte
		try {

			int outSize = NB;

			while (in.read(buffer) == chunkSize) {
				BigInteger enc = new BigInteger(buffer); // convert the encrypted bytes into a BigInteger
				BigInteger decrypt = decrypt(enc); // Decrypt data
				byte[] dataIn = decrypt.toByteArray(); // Convert the decrypted BigInteger into an array of bytes

				if (in.available() == chunkSize) {
					// get padding size at back
					in.read(buffer);
					outSize = decrypt(new BigInteger(1, buffer)).intValue();
				}

				if (dataIn.length == outSize) {
					out.write(dataIn);

				} else {
					byte[] dataOut = new byte[outSize];

					for (int i = outSize - 1, j = dataIn.length - 1; i >= 0 && j >= 0; i--, j--) {
						dataOut[i] = dataIn[j];
					}

					out.write(dataOut);
				}
			}

		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(-1);
		} finally {
			try {
				if (in != null)
					in.close(); // Close the inputfile
				if (out != null)
					out.close(); // Close the outputfile

			} catch (IOException e) {
			}
		}
	}
}
