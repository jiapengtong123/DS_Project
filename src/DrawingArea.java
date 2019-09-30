import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import shapes.Shape;

public class DrawingArea extends JPanel {
    private static final int width = 800;
    private static final int height = 600;
    private Graphics2D g2 = null;
    private BufferedImage bufferedImage = null;
    // free draw start and end shapes
    private float startPointX = 0;
    private float startPointY = 0;
    private float endPointX = 0;
    private float endPointY = 0;
    private List<Shape> shapes = new ArrayList<>();

    // color and type
    private Color color = Color.BLACK;
    private String type = "Free Draw";

    public DrawingArea() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mousePressedHandler(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                mouseDraggedHandler(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                mouseReleasedHandler(e);
            }
        });
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage == null) {
            // set up white board
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
        }

        // get graphic instance
        g2 = (Graphics2D) g;

        // draw shapes
        drawShape();
    }

    private void mousePressedHandler(MouseEvent e) {
        switch (type) {
            case "Free Draw":
                startPointX = e.getX();
                startPointY = e.getY();
                break;
            case "Line":
                startPointX = e.getX();
                startPointY = e.getY();
                break;
            case "Rectangle":
                startPointX = e.getX();
                startPointY = e.getY();
                break;
            case "Circle":
                startPointX = e.getX();
                startPointY = e.getY();
                break;
            case "Oval":
                startPointX = e.getX();
                startPointY = e.getY();
                break;
        }
    }

    private void mouseDraggedHandler(MouseEvent e) {
        switch (type) {
            case "Free Draw":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape shape = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                shape.setType(type);
                    shapes.add(shape);
                startPointX = endPointX;
                startPointY = endPointY;
                repaint();
                break;
            case "Line":
                break;
        }
    }

    private void mouseReleasedHandler(MouseEvent e) {
        switch (type) {
            case "Free Draw":
                break;
            case "Line":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape shapeLine = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                shapeLine.setType(type);
                shapes.add(shapeLine);
                repaint();
                break;
            case "Rectangle":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape shapeRect = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                shapeRect.setType(type);
                shapes.add(shapeRect);
                repaint();
                break;
            case "Circle":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape shapeCircle = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                shapeCircle.setType(type);
                shapes.add(shapeCircle);
                repaint();
                break;
            case "Oval":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape shapeOval = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                shapeOval.setType(type);
                shapes.add(shapeOval);
                repaint();
                break;
        }
    }

    public void drawShape() {
        for (Shape shape : shapes) {
            shape.draw(g2);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }
}
