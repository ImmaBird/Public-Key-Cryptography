import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.*;

public class Encrypt {

	// Initialization Vector
	private final byte[] iv = new byte[] {
  	0x00, 0x01, 0x02, 0x03,
  	0x04, 0x05, 0x06, 0x07,
  	0x08, 0x09, 0x0a, 0x0b,
  	0x0c, 0x0d, 0x0e, 0x0f
  };

  private byte[] salt = "Some Data for the Salt".getBytes();
  private Cipher cipher_encr=null;
  private Cipher cipher_decr=null;

  /**
   * This method returns all available services types
   */
  private static String[] getServiceTypes() {
		boolean debug=false;  // true to print more information
  	Set<String> result = new HashSet<String>();

    // All all providers
    Provider[] providers = Security.getProviders();

		if(debug) {
			System.out.println("Number of Providers: " + providers.length);
		}

    for (int i=0; i<providers.length; i++) {
    	// Get services provided by each provider
  		Set keys = providers[i].keySet();

			if(debug) {
				System.out.println(providers[i]);
			}

      for (Iterator it=keys.iterator(); it.hasNext(); ) {
        String key = (String)it.next();

				if(debug) {
					System.out.println("\t"+key);
				}

        key = key.split(" ")[0];
        if (key.startsWith("Alg.Alias.")) {
        	// Strip the alias
          key = key.substring(10);
        }

        int ix = key.indexOf('.');
        result.add(key.substring(0, ix));

				if(debug) {
					System.out.println("\t"+key.substring(0, ix));
				}
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }

  /**
  * This method returns the available implementations for a service type
  */
	private static String[] getCryptoImpls(String serviceType) {
		boolean debug=false;  // true to print more information
    Set<String> result = new HashSet<String>();

    // All all providers
    Provider[] providers = Security.getProviders();

		if(debug)  System.out.println("Number of Providers: " + providers.length);

    for (int i=0; i<providers.length; i++) {
    	// Get services provided by each provider
    	Set keys = providers[i].keySet();

			if(debug) System.out.println(providers[i]);

      for (Iterator it=keys.iterator(); it.hasNext(); ) {
      	String key = (String)it.next();

				if(debug) System.out.println("\t"+key);

        key = key.split(" ")[0];

        if (key.startsWith(serviceType+".")) {
        	result.add(key.substring(serviceType.length()+1));
          if(debug) System.out.println("\t"+key.substring(serviceType.length()+1));
        }
        else if (key.startsWith("Alg.Alias."+serviceType+".")) {
        	// This is an alias
					result.add(key.substring(serviceType.length()+11));
					if(debug) System.out.println("\t"+key.substring(serviceType.length()+11));
        } else {
					if(debug)System.out.println("\tkey: "+ key);
				}
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }


	private static void ListAll() {
  	// List all Service types
  	String [] a = getServiceTypes();
  	for(int i = 0; i < a.length; i++ ) {
  		System.out.println(a[i]);
  	}

		// List all implementations of KeyGenerator
		// (Use "Cipher" to list all available Enc/Dec Algorithms)
		String[] names = getCryptoImpls("KeyGenerator");
		//String[] names = getCryptoImpls("Ciphers");
		System.out.println("Crypto Implementations for KeyGenerator:");
		for(int i = 0; i < names.length; i++) {
    	System.out.println("\t" + names[i]);
    }
	}


	/**
	* AES constructor.
	* Using 256 bits
	* @param passphrase the password to use to encrypt or decrypt.
	*/
	public Encrypt(String passphrase){
  	try {
    	SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray(), salt, 65536, 256); // 256 bits
    	SecretKey   secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
    	SecretKeySpec  secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

      cipher_encr = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher_encr.init(Cipher.ENCRYPT_MODE, secret , new IvParameterSpec(iv));

      cipher_decr = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher_decr.init(Cipher.DECRYPT_MODE, secret , new IvParameterSpec(iv));
    }
		catch(Exception e) {
    	System.err.println("[ERROR] with AES: " + e);
    	System.exit(-1);
    }
  }

  /**
   * AES encrypton of Serializable objects.
   * @param data Serializable Object to Encrypt.
   * @return an encrypted SealedObject Object.
   * @throws Exception
  */
	public SealedObject encrypt(Serializable data) throws Exception {
		return new SealedObject(data, cipher_encr);
  }

  /**
   * AES decrypton of SealedObject objects.
   * @param sealedObj is the encrypted Object
   * @return a Serializable Object
   * @throws Exception
  */
  public Serializable decrypt(SealedObject sealedObj) throws Exception {
		return (Serializable)sealedObj.getObject(cipher_decr);
  }



	/**
	 * Encrypts incoming stream and places it in another stream (good for file encryption).
	 * @param in the input stream (file stream) that will be encrypted.
	 * @param out the output stream where the encrypted data is placed.
	 */
	public void encrypt(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
		try {
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, cipher_encr);

			// Read in the cleartext bytes and write to out to encrypt
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	/**
	 * Decrypts incoming stream and places it in another stream (good for file decryption).
	 * @param in the input stream (file stream) that will be decrypted.
	 * @param out the output stream where the decrypted data is placed.
	 */
	public void decrypt(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
		try {
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, cipher_decr);

			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}


	public static void main(String args[]) {
		//ListAll();

		if (args.length != 2){
			System.out.println("USAGE: command inputfile password");
		}

		String inputfile = args[0];
		String password = args[1];

		try {
			Encrypt aes = new Encrypt(password);
			aes.encrypt(new FileInputStream(inputfile),new FileOutputStream(inputfile + ".encrypted"));

			//System.out.println("ENCRYPTING and DECRYPTING AN OBJECT...");
			//BigInteger bi1 = new BigInteger("7"); // it is serializable.
			//SealedObject so = aes.encrypt(bi1);
			// we can transmit this now.
  		//BigInteger bi2 = (BigInteger)aes.decrypt(so);
			//System.out.println("BigInteger is: " + bi2);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
