import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class FindNumber {
    public static void main(String[] args) {
        System.out.println("Searching for the hidden number...");

        int result = find("hidden.png");

        System.out.println("The number hidden the image is: " + result);
    }

    public static int find(String imageFile) {
        boolean[] bits = new boolean[32];
        int count = 0;

        try {
            BufferedImage inImage = ImageIO.read(new File(imageFile));
            int hight = inImage.getHeight();
            int width = inImage.getWidth();

            for (int y = 0; y < hight; y++) {
                for (int x = 0; x < width && count < 32; x++) {
                    // get rgb
                    int inARGB = inImage.getRGB(x, y);

                    // get bytes
                    byte[] bytesARGB = A3.getBytes(inARGB);

                    // get the last bits of ARGB
                    bits[count] = A3.getBit(bytesARGB[0], 0);
                    count++;
                    bits[count] = A3.getBit(bytesARGB[1], 0);
                    count++;
                    bits[count] = A3.getBit(bytesARGB[2], 0);
                    count++;
                    bits[count] = A3.getBit(bytesARGB[3], 0);
                    count++;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        return A3.makeInt(bits);
    }
}