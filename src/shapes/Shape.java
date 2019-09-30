package shapes;

import java.awt.*;

public class Shape {
    // basic information of the shape
    private Stroke stroke;
    private Color color;
    private String type;
    private int x1, y1, x2, y2;

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

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        switch (type) {
            case "Free Draw":
                g2.drawLine(x1, y1, x2, y2);
                break;
            case "Line":
                g2.drawLine(x1, y1, x2, y2);
                break;
            case "Rectangle":
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
}
