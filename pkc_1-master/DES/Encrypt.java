
import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.*;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import javax.crypto.*;

public class Encrypt {
	private Cipher ecipher;
	private Cipher dcipher;

  // Create an 8-byte initialization vector
  private byte[] iv = new byte[]{
  	(byte)0x8E, (byte)0x12, (byte)0x39, (byte)0x9C,
  	(byte)0x07, (byte)0x72, (byte)0x6F, (byte)0x5A
  	};

	Encrypt(String passPhrase, String ALG) {
  	// Prepare the parameter to the ciphers
    int iterationCount = 19; // Iteration count
    AlgorithmParameterSpec paramSpec = new PBEParameterSpec(iv, iterationCount);

 		try {
    	// Create the key
      KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), iv, iterationCount);
      SecretKey key = SecretKeyFactory.getInstance(ALG).generateSecret(keySpec);

      ecipher = Cipher.getInstance(key.getAlgorithm());
      dcipher = Cipher.getInstance(key.getAlgorithm());

      // Create the ciphers
      ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
      dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }
		catch (java.security.InvalidAlgorithmParameterException e) { System.err.println(e); }
		catch (java.security.spec.InvalidKeySpecException e) 	   { System.err.println(e); }
		catch (javax.crypto.NoSuchPaddingException e) 		   { System.err.println(e); }
		catch (java.security.NoSuchAlgorithmException e) 	   { System.err.println(e); }
		catch (java.security.InvalidKeyException e) 		   { System.err.println(e); }
	}

  public void encrypt(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
    try {
    	// Bytes written to out will be encrypted
      out = new CipherOutputStream(out, ecipher);

      // Read in the cleartext bytes and write to out to encrypt
      int numRead = 0;
      while ((numRead = in.read(buf)) >= 0) {
      	out.write(buf, 0, numRead);
      }
      out.close();
    }
		catch (java.io.IOException e) {
			System.err.println("encrypt: " + e);
		}
  }

	//
	// This method is implemented in Listing All Available Cryptographic Services
	//
	private static void printCryptoImpls(String serviceType) {
		String[] names;
    Set<String> result = new HashSet<String>();

    // All providers
    Provider[] providers = Security.getProviders();
    for (int i=0; i<providers.length; i++) {

      // Get services provided by each provider
      Set keys = providers[i].keySet();

      for (Iterator it=keys.iterator(); it.hasNext(); ) {

        String key = (String)it.next();

        key = key.split(" ")[0];

        if (key.startsWith(serviceType + ".")) {
        	result.add(key.substring(serviceType.length() + 1));
        }
				else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
        	// This is an alias
          result.add(key.substring(serviceType.length() + 11));
        }
      }
    }

    names =  (String[])result.toArray(new String[result.size()]);
		for(int i=0; i < names.length; i++) {
			System.out.println("\t"+names[i]);
		}
  }

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("USAGE: command inputfile algorithm password");
			return;
		}

		String inputfile = args[0];
		String algorithm = args[1];
		String password = args[2];

    if (algorithm.equals("3des")) {
      algorithm = "PBEWithMD5AndTripleDES";
    }
    else if (algorithm.equals("des")) {
      algorithm = "PBEWithMD5AndDES";
    } else {
			System.out.println("Use 'des' or '3des' for the algorithm argument.");
			return;
		}
		// algorithms PBEWithMD5AndDES and PBEWithMD5AndTripleDES
		try {
    	Encrypt DES = new Encrypt(password, algorithm);
    	DES.encrypt(new FileInputStream(inputfile), new FileOutputStream(inputfile + ".encrypted"));
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
