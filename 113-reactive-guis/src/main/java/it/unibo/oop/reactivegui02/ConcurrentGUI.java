package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel("0");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Public GUI constructor.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();

        stop.addActionListener(e -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });

        up.addActionListener(e -> {
            agent.goUp();
        });

        down.addActionListener(e -> {
            agent.goDown();
        });

        agent.run();
    }


    private final class Agent implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;
        private static final Boolean UP = true;
        private static final Boolean DOWN = false;

        private volatile boolean stop;
        private int counter;
        private boolean direction;

        @Override
        public void run() {
            try {
                while (!this.stop) {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeLater(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (direction == UP) {
                        counter++;
                    } else {
                        counter--;
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Stops the counter.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
         * Makes the counter count up.
         */
        public void goUp() {
            this.direction = UP;
        }

        /**
         * Makes the counter count down.
         */
        public void goDown() {
            this.direction = DOWN;
        }
    }
}
