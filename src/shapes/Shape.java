package shapes;

import java.awt.*;

public class Shape {
    // basic information of the shape
    private Stroke stroke;
    private Color color;
    private String type;
    private int x1, y1, x2, y2;
    private int eraserWidth, eraserHeight;

    public Shape() {

    }

    // constructor for lines
    public Shape(int x1, int y1, int x2, int y2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    // constructor for eraser
    public Shape(int x1, int y1, Color color, int width, int height) {
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
        eraserWidth = width;
        eraserHeight = height;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        switch (type) {
            case "Free Draw":
                g2.setStroke(stroke);
                g2.drawLine(x1, y1, x2, y2);
                break;
            case "Line":
                g2.setStroke(stroke);
                g2.drawLine(x1, y1, x2, y2);
                break;
            case "Rectangle":
                g2.setStroke(stroke);
                if (x1 > x2) {
                    if (y1 > y2) {
                        g2.drawRect(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    } else {
                        g2.drawRect(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    }
                } else {
                    if (y1 < y2) {
                        g2.drawRect(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    } else {
                        g2.drawRect(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    }
                }
                break;
            case "Circle":
                g2.setStroke(stroke);
                if (x1 > x2) {
                    if (y1 > y2) {
                        g2.drawOval(x2, y2, Math.abs(x2 - x1), Math.abs(x2 - x1));
                    } else {
                        g2.drawOval(x2, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
                    }
                } else {
                    if (y1 < y2) {
                        g2.drawOval(x1, y1, Math.abs(x2 - x1), Math.abs(x2 - x1));
                    } else {
                        g2.drawOval(x1, y2, Math.abs(x2 - x1), Math.abs(x2 - x1));
                    }
                }
                break;
            case "Oval":
                g2.setStroke(stroke);
                if (x1 > x2) {
                    if (y1 > y2) {
                        g2.drawOval(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    } else {
                        g2.drawOval(x2, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    }
                } else {
                    if (y1 < y2) {
                        g2.drawOval(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    } else {
                        g2.drawOval(x1, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
                    }
                }
                break;
            case "Eraser":
                g2.drawRect(x1, y1, eraserWidth, eraserHeight);
                g2.fillRect(x1, y1, eraserWidth, eraserHeight);
                break;
            case "EraserBorder":
                g2.setColor(Color.BLACK);
                g2.drawRect(x1, y1, eraserWidth, eraserHeight);
                break;
        }
    }

    // getters and setters
    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getEraserWidth() {
        return eraserWidth;
    }

    public void setEraserWidth(int eraserWidth) {
        this.eraserWidth = eraserWidth;
    }

    public int getEraserHeight() {
        return eraserHeight;
    }

    public void setEraserHeight(int eraserHeight) {
        this.eraserHeight = eraserHeight;
    }
}
