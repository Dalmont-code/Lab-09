package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    final JButton up = new JButton("Up");
    final JButton down = new JButton("Down");
    final JButton stop = new JButton("Stop");

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent buttons = new Agent();
        new Thread(buttons).start();
        new Thread(new Timeout(buttons)).start();

        stop.addActionListener((e) -> {
            buttons.stopCounting();
            this.disableButtons();
        });
        up.addActionListener((e) -> buttons.goUp());
        down.addActionListener((e) -> buttons.goDown());
    }

    private void disableButtons() {
        this.stop.setEnabled(false);
        this.up.setEnabled(false);
        this.down.setEnabled(false);
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter = this.down ? this.counter - 1 : this.counter + 1;
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void goUp() {
            this.down = false;
        }

        public void goDown() {
            this.down = true;
        }
    }

    private class Timeout implements Runnable {

        private static long TIMER = 10000;
        private final Agent buttons;

        public Timeout(Agent buttons) {
            this.buttons = buttons;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(TIMER);
                this.buttons.stopCounting();
                AnotherConcurrentGUI.this.disableButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
