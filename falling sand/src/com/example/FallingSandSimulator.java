package com.example;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class FallingSandSimulator extends JPanel implements ActionListener, MouseMotionListener, ComponentListener {
    private int[][] grid;
    private int w = 5;
    private int cols, rows;
    private int hueValue = 200;
    private Timer timer;
    private Random random = new Random();

    public FallingSandSimulator() {
        addMouseMotionListener(this);
        addComponentListener(this);
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        resizeGrid();
    }

    private int[][] make2DArray(int cols, int rows) {
        int[][] arr = new int[cols][];
        for (int i = 0; i < cols; i++) {
            arr[i] = new int[rows];
            for (int j = 0; j < rows; j++) {
                arr[i][j] = 0;
            }
        }
        return arr;
    }

    private boolean withinCols(int i) {
        return i >= 0 && i <= cols - 1;
    }

    private boolean withinRows(int j) {
        return j >= 0 && j <= rows - 1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int mouseCol = e.getX() / w;
        int mouseRow = e.getY() / w;

        int matrix = 5;
        int extent = matrix / 2;
        for (int i = -extent; i <= extent; i++) {
            for (int j = -extent; j <= extent; j++) {
                if (random.nextFloat() < 0.75f) {
                    int col = mouseCol + i;
                    int row = mouseRow + j;
                    if (withinCols(col) && withinRows(row)) {
                        grid[col][row] = hueValue;
                    }
                }
            }
        }
        hueValue += 1;
        if (hueValue > 360) {
            hueValue = 1;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Not used
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSand();
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        resizeGrid();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // Not used
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Not used
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Not used
    }

    private void updateSand() {
        int[][] nextGrid = make2DArray(cols, rows);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int state = grid[i][j];
                if (state > 0) {
                    int below = (j + 1 < rows) ? grid[i][j + 1] : -1;
                    int dir = random.nextBoolean() ? 1 : -1;

                    int belowA = -1;
                    int belowB = -1;
                    if (withinCols(i + dir) && j + 1 < rows) {
                        belowA = grid[i + dir][j + 1];
                    }
                    if (withinCols(i - dir) && j + 1 < rows) {
                        belowB = grid[i - dir][j + 1];
                    }

                    if (below == 0) {
                        nextGrid[i][j + 1] = state;
                    } else if (belowA == 0) {
                        nextGrid[i + dir][j + 1] = state;
                    } else if (belowB == 0) {
                        nextGrid[i - dir][j + 1] = state;
                    } else {
                        nextGrid[i][j] = state;
                    }
                }
            }
        }
        grid = nextGrid;
    }

    private void resizeGrid() {
        cols = getWidth() / w;
        rows = getHeight() / w;
        grid = make2DArray(cols, rows);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (grid[i][j] > 0) {
                    // Convert HSB to RGB
                    Color color = Color.getHSBColor(grid[i][j] / 360f, 1f, 1f);
                    g2d.setColor(color);
                    int x = i * w;
                    int y = j * w;
                    g2d.fillRect(x, y, w, w);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Falling Sand Simulator");
            FallingSandSimulator simulator = new FallingSandSimulator();
            frame.add(simulator);
            frame.setSize(500, 300);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}