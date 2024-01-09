package core;

import dto.ScoreEvent;
import org.jetbrains.annotations.NotNull;
import utils.ApplicationConstants;
import utils.GamePainter;
import utils.ObjectCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PlayArea extends JPanel implements KeyListener, ActionListener {
    private Field field;
    private JLabel score;
    private JLabel lines;
    private JLabel linesLabel;
    private JLabel scoreLabel;
    private PauseButton pauseButton;
    private PauseFrame pauseFrame;
    private final List<ActionListener> actionListeners;
    private final Timer pauseKeyTimer;
    private boolean isGameStarted;

    {
        setupMainPanel();
        actionListeners = new ArrayList<>();
        pauseKeyTimer = new Timer(50, e -> {
            Timer timer = (Timer) e.getSource();
            timer.stop();
        });
        isGameStarted = false;
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    private void setupMainPanel() {
        setupPauseButton();
        setupTextScore();
        setupTextLines();
        setupScore();
        setupLines();
        setupField();
        setupPauseFrame();

        setBackground(new Color(253, 208, 59));
        setLayout(null);
        setPreferredSize(ApplicationConstants.getApplicationDimension());
    }

    private void setupPauseButton() {
        pauseButton = new PauseButton();
        pauseButton.setLocation(3, 3);
        pauseButton.addActionListener(this);
        this.add(pauseButton);
    }

    private void setupTextScore() {
        scoreLabel = ObjectCreator.createLabel("Score", 2, 20);
        scoreLabel.setOpaque(true);
        scoreLabel.setBounds(240, 200, 60, 25);
        this.add(scoreLabel);
    }


    private void setupTextLines() {
        linesLabel = ObjectCreator.createLabel("Lines", 2, 20);
        linesLabel.setOpaque(true);
        linesLabel.setBounds(243, 270, 53, 25);
        this.add(linesLabel);
    }

    private void setupScore() {
        score = ObjectCreator.createLabel("0", 0, 18);
        score.setBounds(240, 225, 60, 25);
        this.add(score);
    }

    private void setupLines() {
        lines = ObjectCreator.createLabel("0", 0, 18);
        lines.setBounds(243, 295, 60, 25);
        this.add(lines);
    }

    private void setupField() {
        field = new Field();
        field.setLocation(20, 30);
        field.setPreferredSize(new Dimension(100, 100));
        field.addActionListener(this);
        this.add(field);
    }

    private void setupPauseFrame() {
        pauseFrame = new PauseFrame();
        pauseFrame.setVisible(false);
        pauseFrame.addActionListener(this);
        setComponentZOrder(pauseFrame, 0);
        this.add(pauseFrame);
    }

    public void startGame() {
        field.startNewGame();
        isGameStarted = true;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        GamePainter.paintFigure(g, field.getNextFigure(), 200, 30);
    }

    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        if (!isGameStarted) {
            return;
        }
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (!pauseKeyTimer.isRunning()) {
                pauseButton.doClick();
                pauseKeyTimer.start();
            }
        } else if (pauseButton.getType() == 0) {
            switch (keyCode) {
                case KeyEvent.VK_A -> field.moveLeft();
                case KeyEvent.VK_S -> field.moveDown();
                case KeyEvent.VK_D -> field.moveRight();
                case KeyEvent.VK_E -> field.rotateLeft();
                case KeyEvent.VK_R -> field.rotateRight();
                case KeyEvent.VK_SPACE -> field.fallDown();
            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // empty body
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // empty body
    }

    @Override
    public void actionPerformed(@NotNull ActionEvent e) {
        if (e.getSource() == pauseButton) {
            pauseButton.changeType();
            pauseFrame.setVisible(pauseButton.getType() == 1);
            if (pauseButton.getType() == 0) {
                field.resumeGame();
            } else {
                field.pauseGame();
            }
            setComponentZOrder(pauseFrame, 0);
            setComponentZOrder(pauseButton, 0);
            return;
        }
        if (e instanceof ScoreEvent scoreEvent) {
            int curScore = Integer.parseInt(score.getText());
            score.setText(Integer.toString(curScore + scoreEvent.getScore()));
            int curLines = Integer.parseInt(lines.getText());
            lines.setText(Integer.toString(curLines + scoreEvent.getLines()));
            return;
        }
        switch (e.getActionCommand()) {
            case "open menu" -> {
                actionListeners.forEach(actionListener -> actionListener.actionPerformed(e));
                pauseButton.doClick();
            }
            case "resume game" -> pauseButton.doClick();
        }
    }
}
