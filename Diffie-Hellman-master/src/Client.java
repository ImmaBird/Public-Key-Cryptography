import java.io.*;
import java.net.*;
import java.math.BigInteger;

class Client {
	public static void main(String args[]) {
		try {
			//
			// Instead of "localhost", you can pass the IP address of the server
			// The port number the server is listening on is: 1234 (see server code).
			//
			Socket s = new Socket("localhost", 1234);
			System.out.println("Local Port: " + s.getLocalPort());
			System.out.println("Server Port: " + s.getPort());

			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			// get q
			BigInteger q = (BigInteger) ois.readObject();
			System.out.println("Receive q <--- Server: " + q);

			// get a
			BigInteger a = (BigInteger) ois.readObject();
			System.out.println("Receive a <--- Server: " + a);

			// get yb
			BigInteger yb = (BigInteger) ois.readObject();
			System.out.println("Receive public component <--- Server: " + yb);

			// compute xa
			BigInteger xa = dh.getPrivateComponent(q);
			System.out.println("Generate private component: " + xa);

			// compute ya
			BigInteger ya = dh.getPublicComponent(a, xa, q);
			System.out.println("Compute public component: " + ya);

			// send ya
			oos.writeObject(ya);
			System.out.println("Send public component ---> Server");

			// compute key
			BigInteger key = dh.getKey(yb, xa, q);
			System.out.println("Compute key: " + key);

			// close streams
			oos.close();
			ois.close();

			/*
			 * Close connection
			 */
			s.close();
		} catch (Exception e) {
			System.err.print("[ERROR] ::" + e);
		}
	}
}
