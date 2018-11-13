import java.util.Scanner;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class HideNumber {
    public static void main(String[] args) {
        int input = 0;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Please enter a number: ");
            input = sc.nextInt();
        } catch (Exception ex) {
            System.out.println("Invalid number, must be a valid integer.");
        }

        hide(input, "sample.png");
    }

    public static void hide(int num, String imageFile) {
        boolean[] bits = A3.getBits(num);
        int count = 0;

        try {
            BufferedImage inImage = ImageIO.read(new File(imageFile));
            int hight = inImage.getHeight();
            int width = inImage.getWidth();

            BufferedImage outImage = new BufferedImage(width, hight, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < hight; y++) {
                for (int x = 0; x < width; x++) {
                    // get rgb
                    int inARGB = inImage.getRGB(x, y);
                    int outARGB;

                    if (count < 32) {
                        // get bytes
                        byte[] bytesARGB = A3.getBytes(inARGB);

                        // set the last bits of ARGB
                        bytesARGB[0] = A3.setBit(bytesARGB[0], 0, bits[count]);
                        count++;
                        bytesARGB[1] = A3.setBit(bytesARGB[1], 0, bits[count]);
                        count++;
                        bytesARGB[2] = A3.setBit(bytesARGB[2], 0, bits[count]);
                        count++;
                        bytesARGB[3] = A3.setBit(bytesARGB[3], 0, bits[count]);
                        count++;

                        // convert back to int
                        outARGB = A3.makeInt(bytesARGB);
                    } else {
                        outARGB = inARGB;
                    }

                    // set pixel in the output
                    outImage.setRGB(x, y, outARGB);
                }
            }

            ImageIO.write(outImage, "png", new File("hidden.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}