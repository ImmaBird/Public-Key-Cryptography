import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Hash {

    public static void main(String[] args) {
        // validate input
        if (args.length != 1) {
            System.err.println("USAGE: java Hash filename");
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("Invalid fileName.");
        }

        // try to create an instance of the hashing class
        HashTester hash;
        try {
            hash = new HashTester("SHA-256");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        // Open the input file
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            // read a line from the file into the variable line
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                hash.update(line);
            }

            String hashValue = hash.formatHashValue(hash.digest());
            System.out.println(hashValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}