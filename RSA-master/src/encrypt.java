public class encrypt {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Enter an input file and an output file.");
            System.exit(-1);
        }

        // p q pk
        RSA_WIT rsa = new RSA_WIT("977447", "649487", "6359");

        rsa.EncryptFile(args[0], args[1]); // Encrypt file

        System.out.println("FINISHED");
    }
}