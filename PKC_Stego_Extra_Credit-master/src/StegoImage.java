import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;

import java.net.*;
import javax.imageio.*;
import java.io.*;
import java.math.BigInteger;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class StegoImage {

	private int counter = -1;
	private boolean moreImageData = true;

	private byte nextBit(BufferedImage bi2) {
		counter++;
		int w2 = bi2.getWidth();
		int h2 = bi2.getHeight();

		int ROW = counter / (w2 * 32);
		int COL = (counter / 32) % w2;

		int tbit = counter % 32;
		tbit = 31 - tbit; // first comes the MSB (this is the amount of shift)

		int rgb = bi2.getRGB(COL, ROW);

		int rr = ((rgb >> tbit) & 0x00000001);

		if (counter >= w2 * h2 * 32 - 1)
			moreImageData = false;

		return (byte) rr;

	}

	/*********************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 *
	 *
	 * THIS IS THE FUNCTION YOU NEED TO IMPLEMENT
	 *
	 *
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 * *******************************************************************************************************
	 *********************************************************************************************************/
	public BufferedImage showHiddenImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		int hiddenImageWidth = getImageHeaderW(image);
		int hiddenImageHeight = getImageHeaderH(image);
		int bitsPerByte = getImageBitsinByte(image);

		if (hiddenImageWidth <= 0 || hiddenImageHeight <= 0)
			return null;

		int hiddenImagePixels = hiddenImageWidth * hiddenImageHeight;
		int newPixel = 0;
		int bitCount = 0;
		int newPixelCount = 0;

		BufferedImage hiddenImage = new BufferedImage(hiddenImageWidth, hiddenImageHeight, image.getType());

		for (int y = 1; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(x, y);
				for (int i = 1; i < 4; i++) {
					for (int j = 0; j < bitsPerByte; j++) {
						newPixel = setBit(newPixel, bitCount, getBit(pixel, i, j));
						bitCount++;
						if (bitCount == 32) {
							hiddenImage.setRGB(newPixelCount % hiddenImageWidth, newPixelCount / hiddenImageWidth, newPixel);
							newPixelCount++;
							newPixel = 0;
							bitCount = 0;
							if (newPixelCount == hiddenImagePixels) {
								return hiddenImage;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public void printBytes(int num) {
		for (int i = 0; i < 32; i++) {
			System.out.print(((num >> 31 - i) & 0b1) + " ");
		}
		System.out.println();
	}

	// Int [0 1 2 3 4 5 6 7 | 8 9 10 11 12 13 14 15 | 16 17 18 19 20 21 22 23 | 24 25 26 27 28 29 30 31]
	public int getBit(int pixel, int bitNum, int byteNum) {
		int shift = (8*(bitNum+1)-1)-byteNum;
		return (pixel >> (31 - shift)) & 0b1;
	}

	// Int [0 1 2 3 4 5 6 7 | 8 9 10 11 12 13 14 15 | 16 17 18 19 20 21 22 23 | 24 25 26 27 28 29 30 31]
	public int setBit(int pixel, int bitNum, int value) {
		if (value == 0) {
			return pixel & (~((0b1) << (31 - bitNum)));
		} else {
			return pixel | (0b1 << (31 - bitNum));
		}

	}

	/*
	 * first write the Width, then the Height and then insert the number of bits per
	 * byte and then continue to fill in the rest of the first row unaltered.
	 */
	private void insertImageHeader(BufferedImage bi, BufferedImage bi2, BufferedImage bnew, int n) {
		int w2 = bi2.getWidth();
		int h2 = bi2.getHeight();

		int w = bi.getWidth();

		int num = w2;

		int iter = 0;
		for (int x = 0, f = 0; x < w; f += 4, x++) {
			int rgb = bi.getRGB(x, 0); // header goes in first raw

			int a = ((rgb >> 24) & 0xff);
			int r = ((rgb >> 16) & 0xff);
			int g = ((rgb >> 8) & 0xff);
			int b = ((rgb >> 0) & 0xff);

			if (f >= 32 && iter == 0) { // was doing width, now will do height
				// System.out.println("________________________HEIGHT________________________" +
				// f + " " + x);
				f = 0;
				num = h2;
				iter = 1;
			} else if (f >= 32 && iter == 1) {// was doing height, now will do bits per byte
				// System.out.println("________________________BITS PER
				// BYTE________________________" + f + " " + x);
				f = 0;
				num = n;
				iter = 2;
			} else if (f >= 32 && iter == 2) { // now will do the rest
				iter = 3;
			}

			if (f < 32 && iter <= 2) { // insert the number, but continue to complete the first raw of bixels
										// unchanged!

				byte abit = (byte) ((num >> 31 - f - 0) & 0x01);
				byte bbit = (byte) ((num >> 31 - f - 1) & 0x01);
				byte cbit = (byte) ((num >> 31 - f - 2) & 0x01);
				byte dbit = (byte) ((num >> 31 - f - 3) & 0x01);

				// System.out.println("HIDE: " + abit + " " +bbit+ " " +cbit + " " +dbit);

				if (abit == (byte) 1)
					a = a | (1 << 0);
				else
					a = a & ~(1 << 0);

				if (bbit == (byte) 1)
					r = r | (1 << 0);
				else
					r = r & ~(1 << 0);

				if (cbit == (byte) 1)
					g = g | (1 << 0);
				else
					g = g & ~(1 << 0);

				if (dbit == (byte) 1)
					b = b | (1 << 0);
				else
					b = b & ~(1 << 0);

				int new_rgb = (rgb & 0x00000000) | (a << 24) | (r << 16) | (g << 8) | (b << 0);
				bnew.setRGB(x, 0, new_rgb);
			} else {
				// now continue to fill in the rest of first row unaltered
				int new_rgb = (rgb & 0x00000000) | (a << 24) | (r << 16) | (g << 8) | (b << 0);
				bnew.setRGB(x, 0, new_rgb);
			}

		}
	}

	// hide image bi2 into bi
	// Store in header size of hiden image and its dimensions (OR only its
	// dimensions, I think)
	public BufferedImage hideImage(BufferedImage bi, BufferedImage bi2, int nbitsPerByte) {
		int w = bi.getWidth();
		int h = bi.getHeight();

		int w2 = bi2.getWidth();
		int h2 = bi2.getHeight();

		// convert bi2 into an array of bytes
		/*
		 * byte[] bytesOut=null; try{ ByteArrayOutputStream baos = new
		 * ByteArrayOutputStream(); ImageIO.write(bi2, "png", baos); baos.flush();
		 * bytesOut = baos.toByteArray(); baos.close(); } catch(IOException e){}
		 * 
		 * byte[] TEMPbytesOut=null; try{ ByteArrayOutputStream TEMPbaos = new
		 * ByteArrayOutputStream(); ImageIO.write(bi, "png", TEMPbaos);
		 * TEMPbaos.flush(); TEMPbytesOut = TEMPbaos.toByteArray(); TEMPbaos.close(); }
		 * catch(IOException e){}
		 */

		// System.out.println("NEED " + (bytesOut.length*8) + " bytes to hide this image
		// which is: " + bytesOut.length + " bytes big. (" + w2 +"x"+h2+")");
		// System.out.println("COVER " + TEMPbytesOut.length + " bytes big. (" + w
		// +"x"+h+")");

		BufferedImage bnew = new BufferedImage(w, h, bi.getType());

		int n = nbitsPerByte; // number of bits to hide in a byte

		insertImageHeader(bi, bi2, bnew, n);

		// reset the nextBit
		counter = -1;
		moreImageData = true;

		for (int y = 1; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int rgb = bi.getRGB(x, y);

				int alpha = (rgb >> 24) & 0xff;
				alpha = 255;

				int r = ((rgb >> 16) & 0xff);
				int g = ((rgb >> 8) & 0xff);
				int b = ((rgb >> 0) & 0xff);

				if (moreImageData) {
					byte abit = (byte) 0;

					for (int i = 0; i < n; i++) {
						if (moreImageData) {
							abit = nextBit(bi2);
							if (abit == (byte) 1)
								r = r | (1 << i);
							else
								r = r & ~(1 << i);
						}
					}

					for (int i = 0; i < n; i++) {
						if (moreImageData) {
							abit = nextBit(bi2);
							if (abit == (byte) 1)
								g = g | (1 << i);
							else
								g = g & ~(1 << i);
						}
					}

					for (int i = 0; i < n; i++) {
						if (moreImageData) {
							abit = nextBit(bi2);
							if (abit == (byte) 1)
								b = b | (1 << i); // set
							else
								b = b & ~(1 << i); // clear
						}
					}
				}

				int new_rgb = (rgb & 0x00000000) | (alpha << 24) | (r << 16) | (g << 8) | (b << 0);

				bnew.setRGB(x, y, new_rgb);
			}
		}

		//
		// Mark Stego Image as an image that contains another image as the secret.
		//
		int q = bnew.getRGB(0, 0);
		// System.out.println("q is: " + q);
		q |= (1 << 1); // set bit 1 (0 is the LSB) of the blue
		// System.out.println("q is: " + q);
		bnew.setRGB(0, 0, q);

		if (moreImageData) {
			JOptionPane.showMessageDialog(null, "Couldn't hide entire image in this cover image!", "WARNING",
					JOptionPane.WARNING_MESSAGE);

		}
		return bnew;
	}

	/*
	 * Return the width of the hidden image which is stored in the header
	 */
	private int getImageHeaderW(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		int n = 0;
		// System.out.println("\n------- W --------------------");
		for (int x = 0, f = 0; x < w && f < 32; f += 4, x++) {
			int rgb = bi.getRGB(x, 0); // header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);
			n = (n | (a << 31 - f - 0));
			int r = ((rgb >> 16) & 0x00000001);
			n = (n | (r << 31 - f - 1));
			int g = ((rgb >> 8) & 0x00000001);
			n = (n | (g << 31 - f - 2));
			int b = ((rgb >> 0) & 0x00000001);
			n = (n | (b << 31 - f - 3));
			// System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}

	/*
	 * Return the height of the hidden image which is stored in the header
	 */
	private int getImageHeaderH(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		int n = 0;
		// System.out.println("\n------- H --------------------");
		for (int x = 8, f = 0; x < w && f < 32; f += 4, x++) {
			int rgb = bi.getRGB(x, 0); // header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);
			n = (n | (a << 31 - f - 0));
			int r = ((rgb >> 16) & 0x00000001);
			n = (n | (r << 31 - f - 1));
			int g = ((rgb >> 8) & 0x00000001);
			n = (n | (g << 31 - f - 2));
			int b = ((rgb >> 0) & 0x00000001);
			n = (n | (b << 31 - f - 3));

			// System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}

	/*
	 * Return the number of bits hidden in a byte
	 */
	private int getImageBitsinByte(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		int n = 0;
		// System.out.println("\n------- Bits in Byte --------------------");
		for (int x = 16, f = 0; x < w && f < 32; f += 4, x++) {
			int rgb = bi.getRGB(x, 0); // header goes in first raw

			int a = ((rgb >> 24) & 0x00000001);
			n = (n | (a << 31 - f - 0));
			int r = ((rgb >> 16) & 0x00000001);
			n = (n | (r << 31 - f - 1));
			int g = ((rgb >> 8) & 0x00000001);
			n = (n | (g << 31 - f - 2));
			int b = ((rgb >> 0) & 0x00000001);
			n = (n | (b << 31 - f - 3));

			// System.out.println(a + " " + r + " " + g + " " + b);
		}
		return n;
	}
}
