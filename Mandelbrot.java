package fiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Mandelbrot extends JPanel implements MouseWheelListener ,KeyListener{

    private int width = 1000;
    private int height = 1000;
    private final int maxIter = 100;
    private final double oxmin = -2.0, oxmax = 1.0, oymin = -1.5, oymax = 1.5;
    private double xmin = -2.0, xmax = 1.0, ymin = -1.5, ymax = 1.5;
    private BufferedImage image;
    private double zoomFactor = 2.0;
    private final int STEP = 100;



    public Mandelbrot() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        generateMandelbrotSet();
        setFocusable(true);
        requestFocusInWindow();
        addMouseWheelListener(this);
        addKeyListener(this);
    }
    private int mandelbrot(Complex c) {
        Complex z = new Complex(0, 0);
        for (int n = 0; n < maxIter; n++) {
            if (z.abs() > 2.0) {
                return n;
            }
            z = z.mul(z).add(c);
        }
        return maxIter;
    }

    private void generateMandelbrotSet() {
        double xScale = (xmax - xmin) / (width - 1);
        double yScale = (ymax - ymin) / (height - 1);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double x = xmin + j * xScale;
                double y = ymin + i * yScale;
                Complex c = new Complex(x, y);
                int iter = mandelbrot(c);
                int color = Color.HSBtoRGB((float) iter / maxIter, 0.5f, iter < maxIter ? 1 : 0);
                image.setRGB(j, i, color);
            }
        }
    }
    private void zoom(int x, int y) {
        double xCenter = xmin + x * (xmax - xmin) / width;
        double yCenter = ymin + y * (ymax - ymin) / height;

        double xRange = (xmax - xmin) / zoomFactor;
        double yRange = (ymax - ymin) / zoomFactor;

        xmin = xCenter - xRange / 2;
        xmax = xCenter + xRange / 2;
        ymin = yCenter - yRange / 2;
        ymax = yCenter + yRange / 2;
    }
    private void zoomout(int x, int y){
        double xCenter = xmin + x * (xmax - xmin) / width;
        double yCenter = ymin + y * (ymax - ymin) / height;
        double xRange = (xmax - xmin) / zoomFactor;
        double yRange = (ymax - ymin) / zoomFactor;
        xmin = xCenter - xRange * 2;
        xmax = xCenter + xRange * 2;
        ymin = yCenter - yRange * 2;
        ymax = yCenter + yRange * 2;
    }
    private void reset(){
        xmin = oxmin;
        xmax = oxmax;
        ymin = oymin;
        ymax = oymax;
    }
    private void mover() {
        double xRange = (xmax - xmin) / width * STEP;
        xmin += xRange;
        xmax += xRange;
    }
    private void movel() {
        double xRange = (xmax - xmin) / width * STEP;
        xmin -= xRange;
        xmax -= xRange;
    }
    private void moveu() {
        double yRange = (ymax - ymin) / height * STEP;
        ymin -= yRange;
        ymax -= yRange;
    }
    private void moved() {
        double yRange = (ymax - ymin) / height * STEP;
        ymin += yRange;
        ymax += yRange;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mandelbrot");
        Mandelbrot panel = new Mandelbrot();
        frame.add(panel);
        frame.setSize(panel.width, panel.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (e.getWheelRotation() < 0){
            zoom(x,y);
            generateMandelbrotSet();
            repaint();
        }
        else {
            zoomout(x, y);
            generateMandelbrotSet();
            repaint();
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            reset();
            generateMandelbrotSet();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP){
            moveu();
            generateMandelbrotSet();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            moved();
            generateMandelbrotSet();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            mover();
            generateMandelbrotSet();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            movel();
            generateMandelbrotSet();
            repaint();
        }

    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}


    private static class Complex {
        private final double re;
        private final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex add(Complex b) {
            return new Complex(this.re + b.re, this.im + b.im);
        }

        public Complex mul(Complex b) {
            return new Complex(this.re * b.re - this.im * b.im, this.re * b.im + this.im * b.re);
        }

        public double abs() {
            return Math.sqrt(this.re * this.re + this.im * this.im);
        }
    }
}
