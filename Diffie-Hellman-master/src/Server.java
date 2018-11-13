import java.io.*;
import java.net.*;
import java.math.BigInteger;

public class Server extends Thread {
	private ServerSocket servSock = null;

	private void printInfo(Socket s) {
		InetAddress ia;
		System.out.println("\tLocal Port : " + s.getLocalPort());
		System.out.println("\tRemote Port: " + s.getPort());

		ia = s.getInetAddress(); // REMOTE
		System.out.println("\t==> Remote IP: " + ia.getHostAddress());
		System.out.println("\t==> Remote Name: " + ia.getHostName());
		System.out.println("\t==> Remote DNS Name: " + ia.getCanonicalHostName());

		ia = s.getLocalAddress(); // LOCAL
		System.out.println("\t==> Local IP: " + ia.getHostAddress());
		System.out.println("\t==> Local Name: " + ia.getHostName());
		System.out.println("\t==> Local DNS Name: " + ia.getCanonicalHostName());
	}

	public Server(int port) {
		try {
			servSock = new ServerSocket(port, 5);
			System.out.println("Listening on port " + port);
		} catch (Exception e) {
			System.err.println("[ERROR] + " + e);
		}
		System.out.println("Waiting for connections......");
		this.start();
	}

	public void run() {
		while (true) {
			try {
				Socket s = servSock.accept();
				System.out.println("Server accepted connection from: " + s.getInetAddress().getHostAddress());
				printInfo(s);
				System.out.println();

				new ClientHandler(s).start();
			} catch (Exception e) {
				System.err.println("[ERROR] + " + e);
			}
		}
		// servSock.close(); // At some point we need to close this (when we shutdown
		// the server), for now let's put it here
	}

	public static void main(String args[]) {
		new Server(1234);
	}

}

/**
 * Handles connection with the client.
 */
class ClientHandler extends Thread {
	private Socket s = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;

	public ClientHandler(Socket s) {
		this.s = s;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

	public void run() {
		try {
			// pick random large prime q
			BigInteger q = dh.getPrime(80);
			System.out.println("Generate q: " + q);

			// pick primitive root a < q
			BigInteger a = new BigInteger("3");
			System.out.println("Hardcoded a: " + a);

			// compute xa
			BigInteger xa = dh.getPrivateComponent(q);
			System.out.println("Generate private component: " + xa);

			// compute ya
			BigInteger ya = dh.getPublicComponent(a, xa, q);
			System.out.println("Compute public component: " + ya);

			// send q
			oos.writeObject(q);
			System.out.println("Send q ---> Client");

			// send a
			oos.writeObject(a);
			System.out.println("Send a ---> Client");

			// send ya
			oos.writeObject(ya);
			System.out.println("Send public component ---> Client");

			// get yb
			BigInteger yb = (BigInteger) ois.readObject();
			System.out.println("Receive public component <--- Client: " + yb);

			// compute key
			BigInteger key = dh.getKey(yb, xa, q);
			System.out.println("Compute key: " + key);

			// close streams
			oos.close();
			ois.close();

			// Close connection
			s.close();
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		}
		System.out.println("Session Ended\n");
	}
}
