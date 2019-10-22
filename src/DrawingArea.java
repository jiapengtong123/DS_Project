import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.awt.event.FocusListener;

import shapes.Shape;

public class DrawingArea extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int width = 100;
    private int height = 100;

    private Graphics2D g2 = null;
    private BufferedImage bufferedImage = null;
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    // free draw start and end shapes
    private float startPointX = 0;
    private float startPointY = 0;
    private float endPointX = 0;
    private float endPointY = 0;
    private List<Shape> shapes = new ArrayList<>();

    // color and type
    private Color color = Color.BLACK;
    private BasicStroke stroke = (new BasicStroke(15f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND));
    private String type = "Select";
    // shape instance to store current shape, used for real time drawing
    private Shape shape = null;
    // reference Shapes
    private Shape eraserBorder = new Shape();
    private Shape boundingBox = new Shape();
    private Shape selectedShape = new Shape();
    // connect with server to transfer shape and images
    private NetworkConnectModule connection = new NetworkConnectModule();

    public DrawingArea() {
        // start connection
        connection.setIP("localhost");
        connection.setPORT("3005");
        connection.connect();
        startListeners();
    }

    public void startListeners() {
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
        if (stroke == null) {
            stroke = new BasicStroke(5);
        }
        if (bufferedImage == null) {
            // set up white board
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
        } else {
            g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform aTran = new AffineTransform();
            aTran.translate(0, 0);
            g2.transform(aTran);
            g2.drawImage(bufferedImage, aTran, this);
        }

        // get graphic instance
        g2 = (Graphics2D) g;

        // draw shapes
        drawShape();
    }

    private void mousePressedHandler(MouseEvent e) {
        switch (type) {
            case "Select":
                // Iterate through shapelist in reverse so newer shapes are selected over older ones
                for (int i = shapes.size() - 1; i >= -1; i--) {
                    // -1 means no shapes were selected
                    if (i == -1) {
                        shapes.remove(boundingBox);
                        if (selectedShape != null) {
                            selectedShape.setSelected(false);
                        }
                        selectedShape = null;
                        break;
                    }

                    Shape s = shapes.get(i);
                    //If the clicked point is within shape boundaries, create bounding box, select shape
                    if (s.getBounds().contains(e.getPoint()) && s != boundingBox && s.getType() != "Eraser" && s.getType() != "Free Draw") {
                        s.setSelected(true);
                        shapes.remove(boundingBox);
                        boundingBox = new Shape(s.getX1(), s.getY1(), s.getX2(), s.getY2(),
                                Color.RED);
                        boundingBox.setType("Rectangle");
                        selectedShape = s;
                        shapes.remove(s);
                        shapes.add(s);
                        shapes.add(boundingBox);
                        repaint();
                        break;
                    }
                    repaint();
                }
                ;
                break;
            case "Type":
                shapes.remove(boundingBox);
                setLayout(null);
                final JTextField textField = new JTextField(20);
                textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                textField.setSize(textField.getPreferredSize());
                int x = e.getX();
                int y = e.getY();
                textField.setLocation(x, y);
                add(textField);
                revalidate();
                repaint();
                textField.requestFocusInWindow();
                textField.addFocusListener(new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        shape = new Shape(x, y, color);
                        shape.setText(textField.getText());
                        shape.setType(type);
                        shapes.add(shape);
                        remove(textField);
                    }

                    @Override
                    public void focusGained(FocusEvent arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                repaint();
                break;
            case "Eraser":
                shapes.remove(boundingBox);
                break;
            default:
                shapes.remove(eraserBorder);
                shapes.remove(boundingBox);
                startPointX = e.getX();
                startPointY = e.getY();
                shape = new Shape((int) startPointX, (int) startPointY,
                        (int) startPointX, (int) startPointY, color);
                shape.setType(type);
                shape.setStroke(stroke);
                shapes.add(shape);
                break;
        }
    }

    private void mouseDraggedHandler(MouseEvent e) {
        switch (type) {
            case "Select":
                if (selectedShape != null) {
                    endPointX = e.getX();
                    endPointY = e.getY();
                    selectedShape.setX2((int) endPointX);
                    selectedShape.setY2((int) endPointY);
                    boundingBox.setX2((int) endPointX);
                    boundingBox.setY2((int) endPointY);
                    repaint();
                }
                break;
            case "Free Draw":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape temp = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                temp.setType(type);
                temp.setStroke(stroke);
                temp.setStrokeSize(stroke.getLineWidth());
                shapes.add(temp);
                startPointX = endPointX;
                startPointY = endPointY;
                repaint();
                break;
            case "Eraser":
                // draws a white stroke
                endPointX = e.getX();
                endPointY = e.getY();
                temp = new Shape(e.getX(), e.getY(), (int) endPointX, (int) endPointY,
                        BACKGROUND_COLOR);
                temp.setType(type);
                temp.setType(type);
                temp.setStroke(stroke);
                temp.setStrokeSize(stroke.getLineWidth());
                shapes.add(temp);
                startPointX = endPointX;
                startPointY = endPointY;

                shapes.remove(eraserBorder);
                eraserBorder.setX1(e.getX());
                eraserBorder.setY1(e.getY());
                eraserBorder.setType("EraserBorder");
                shapes.add(eraserBorder);
                for (int i = shapes.size() - 1; i >= -1; i--) {
                    if (i == -1) {
                        break;
                    }
                    Shape s = shapes.get(i);
                    //If the eraser shape intersects an object, erase it.
                    if (s.getBounds().contains(e.getPoint()) && s != boundingBox) {
                        shapes.remove(s);
                        repaint();
                        break;
                    }
                    repaint();
                }
                ;

                repaint();

                break;
            case "Type":
                break;
            default:
                endPointX = e.getX();
                endPointY = e.getY();
                shape.setX2((int) endPointX);
                shape.setY2((int) endPointY);
                shape.setStrokeSize(stroke.getLineWidth());
                repaint();
                break;
        }
    }

    private void mouseReleasedHandler(MouseEvent e) {
        switch (type) {
            case "Free Draw":
                break;
            case "Line":
                break;
            case "Rectangle":
                break;
            case "Circle":
                break;
            case "Oval":
                break;
            case "Eraser":
                shapes.remove(eraserBorder);
                repaint();
                break;
            case "Select":
                break;
        }
        // when mouse released, add new a shape to server
        bufferedImage = connection.sendShapeAndGetImage(shape);
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

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(BasicStroke stroke) {
        this.stroke = stroke;
    }

    // Save the canvas as an image file
    public void saveImage(String type, File file) {
        System.out.println(width + " " + height);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gimg = img.createGraphics();
        printAll(gimg);
        gimg.dispose();
        String filename = file.toString() + '.' + type;
        File fileExt = new File(filename);
        try {
            ImageIO.write(img, type, fileExt);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addBg(BufferedImage bg) {
        this.bufferedImage = bg;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
