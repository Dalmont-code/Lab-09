package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    final JButton up = new JButton("Up");
    final JButton down = new JButton("Down");
    final JButton stop = new JButton("Stop");

    public ConcurrentGUI() {
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

        final Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener((e) -> {
            agent.stopCounting();
            this.stop.setEnabled(false);
            this.up.setEnabled(false);
            this.down.setEnabled(false);
        });
        up.addActionListener((e) -> agent.goUp());
        down.addActionListener((e) -> agent.goDown());

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
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    this.counter = this.down ? this.counter-1 : this.counter+1;
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
}
