package test;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class TestImgs extends JFrame {

    BinImgFileReader reader = new BinImgFileReader("imgdata\\bin");

    public void draw() {

        // Icon icon = new

        double[][][] rgb = reader.next();

        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                Color color = new Color((float) rgb[0][x][y], (float) rgb[1][x][y], (float) rgb[2][x][y]);
                image.setRGB(x, y, color.getRGB());
            }
        }

        graph.setIcon(new ImageIcon(image));

        repaint();

    }

    JLabel graph = new JLabel();

    public TestImgs() {

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();

                    if (keyCode == 32) {
                        draw();
                    }
                }
                return false;
            }
        });

     //   setLayout(null);

        add(graph);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                reader.close();
                System.exit(0);
            }
        });

        setBounds(640, 320, 640, 640);
        setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new TestImgs();
    }

}
