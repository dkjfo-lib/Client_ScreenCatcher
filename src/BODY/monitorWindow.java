package HEAD;

import BODY.Client_Catcher;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class monitorWindow extends JComponent implements Runnable {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final JFrame window;

    public monitorWindow() {
        window = new JFrame();
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(this);
        window.setVisible(true);
    }

    public void paint(Graphics g) {
        if (Client_Catcher.lastReceivedMessage != null)
        g.drawImage(Client_Catcher.lastReceivedMessage, 0, 0, window.getWidth(), window.getHeight(), null);
    }

    @Override
    public void run() {
        paint(window.getGraphics());
    }
}
