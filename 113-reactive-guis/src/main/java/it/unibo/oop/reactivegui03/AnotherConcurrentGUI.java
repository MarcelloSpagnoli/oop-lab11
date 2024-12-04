package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.3;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel("0");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final JLabel timeDisplay = new JLabel("0.0");
    private final Agent agent = new Agent();
    private final TimeAgent timeAgent = new TimeAgent();

    /**
     * Public constructor.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        timeDisplay.setEnabled(false);
        display.setEnabled(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        panel.add(timeDisplay);
        this.getContentPane().add(panel);
        this.setVisible(true);


        stop.addActionListener(e -> {
            this.stop();
        });

        up.addActionListener(e -> {
            agent.goUp();
        });

        down.addActionListener(e -> {
            agent.goDown();
        });

        new Thread(agent).start();
    }

    private void stop() {
        agent.stopCounting();
        timeAgent.stopChrono();
        stop.setEnabled(false);
        up.setEnabled(false);
        down.setEnabled(false);
    }


    private final class Agent implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;
        static final boolean UP = true;
        static final boolean DOWN = false;

        private volatile boolean stop;
        private int counter;
        private boolean direction = true;

        @Override
        public void run() {
            new Thread(timeAgent).start();
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeLater(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (direction == UP) {
                        counter++;
                    } else {
                        counter--;
                    }
                Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } 
        }

        private void stopCounting() {
            this.stop = true;
        }

        private void goUp() {
            this.direction = UP;
        }

        private void goDown() {
            this.direction = DOWN;
        }
    }

    private final class TimeAgent implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;
        private static final double TIME_TO_ELAPSE_SECONDS = 10;

        private volatile boolean stop;
        @Override
        public void run() {
            try {
                final long t0 = System.currentTimeMillis();
                Double elapsed;
                do {
                    final long t1 = System.currentTimeMillis();
                    elapsed = (t1 - t0) / 1000.0;
                    final String nextText = new DecimalFormat("0.0").format(elapsed);
                    SwingUtilities.invokeLater(() -> 
                        AnotherConcurrentGUI.this.timeDisplay.setText(nextText));
                    Thread.sleep(10);
                } while (elapsed < TIME_TO_ELAPSE_SECONDS && !stop);
                AnotherConcurrentGUI.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void stopChrono() {
            this.stop = true;
        }
    }
}
