package test;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

public class PrepareData {
    static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public static void main(String[] args) {
        for (File src : new File("imgdata\\fullsize").listFiles()) {
            String srcName = src.getName();

            String dstPath = "imgdata\\bin\\" + srcName;

            File dst = new File(dstPath);
            if (!dst.exists()) {
                dst.mkdirs();
            }

            for (File img : src.listFiles()) {
                try {
                    BufferedImage full = ImageIO.read(img);
                    BufferedImage stnd = resize(full, 256, 256);

                    String outName = dstPath + "\\" + img.getName().replace("jpg", "bin");

                    try (RandomAccessFile out = new RandomAccessFile(new File(outName), "rw")) {
                        for (int x = 0; x < 256; x++) {
                            for (int y = 0; y < 256; y++) {
                                out.writeInt(stnd.getRGB(x, y));
                            }
                        }
                    }

                    System.out.println(outName);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        }
    }

}
