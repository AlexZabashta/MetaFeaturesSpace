package test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Digit implements Serializable {

    private static final long serialVersionUID = 10L;
    public static final int SIZE = 28;

    public static Digit fromImage(int[][] image) {
        byte[] pixels = new byte[SIZE * SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int grey = image[x][y];
                if (grey < 0) {
                    grey = 0;
                }
                if (grey > 255) {
                    grey = 255;
                }

                pixels[index(x, y)] = (byte) ((0xFF) & grey);

            }
        }
        return new Digit(pixels, -1);
    }

    static int index(int x, int y) {
        return y * SIZE + x;
    }

    public static List<Digit> readDigits(String path) {

        List<Digit> digits = new ArrayList<>();

        try (RandomAccessFile data = new RandomAccessFile(path, "r")) {
            int size = data.readInt();
            System.out.println("Read " + size + " digits from " + path);
            int delta = size / 10;
            int notify = delta;

            for (int i = 0; i < size; i++) {
                int len = SIZE * SIZE;
                byte[] pixels = new byte[len];

                int off = 0;
                while (len > 0) {
                    int read = data.read(pixels, off, len);
                    if (read <= 0) {
                        throw new IOException("Unexpected end of file");
                    }
                    off += read;
                    len -= read;
                }

                int label = data.readUnsignedByte();
                digits.add(new Digit(pixels, label));
                if (i == notify) {
                    System.out.println("    " + i + " / " + size);
                    notify += delta;
                }
            }

            System.out.println("Success");
        } catch (IOException e) {

            System.out.println("Error " + e.getMessage());
        }
        System.out.println();
        return digits;
    }

    public final int label;

    public final byte[] pixels;

    public Digit(byte[] pixels, int label) {
        this.pixels = pixels;
        this.label = label;

    }

    double convert(byte ubyte) {
        return (ubyte & 0xff) / 255.0;
    }

    public void debug() {
        for (byte b : pixels) {
            if (b != 0) {
                System.out.println(convert(b));
            }
        }
    }

    public void drawOn(int[][] image) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                image[x][y] = pixels[index(x, y)] & 0xff;
            }
        }
    }

}
