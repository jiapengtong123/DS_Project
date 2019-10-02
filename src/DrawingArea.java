import javax.imageio.ImageIO;
import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    transient private Graphics2D g2 = null;
    transient private BufferedImage bufferedImage = null;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
    // free draw start and end shapes
    private float startPointX = 0;
    private float startPointY = 0;
    private float endPointX = 0;
    private float endPointY = 0;
    private List<Shape> shapes = new ArrayList<>();

    // color and type
    private Color color = Color.BLACK;
    transient private Stroke stroke = new BasicStroke(5);
    private String type = "Free Draw";
    // shape instance to store current shape, used for real time drawing
    private Shape shape = null;
    // eraser width and height
    private int eraserWidth = 20;
    private int eraserHeight = 20;
    private Shape eraserBorder = new Shape();
    private int borderSize = 3;

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
        if(stroke == null) {
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
        }

        // get graphic instance
        g2 = (Graphics2D) g;

        // draw shapes
        drawShape();
    }

    private void mousePressedHandler(MouseEvent e) {
        switch (type) {
            case "Eraser":
                break;
            case "Type":
            	setLayout(null);
            	final JTextField textField = new JTextField(20);
            	textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            	textField.setSize(textField.getPreferredSize());
            	int x = e.getX();
            	int y = e.getY();
            	textField.setLocation(x,y);
            	add(textField);
            	revalidate();
            	repaint();
            	textField.requestFocusInWindow();
            	textField.addFocusListener(new FocusListener() { 
                    public void focusLost(FocusEvent e) { 
                    	shape = new Shape(x,y,color);
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
            default:
                shapes.remove(eraserBorder);
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
            case "Free Draw":
                endPointX = e.getX();
                endPointY = e.getY();
                Shape temp = new Shape((int) startPointX, (int) startPointY,
                        (int) endPointX, (int) endPointY, color);
                temp.setType(type);
                temp.setStroke(stroke);
                shapes.add(temp);
                startPointX = endPointX;
                startPointY = endPointY;
                repaint();
                break;
            case "Eraser":
                // set eraser border attributes
                eraserBorder.setX1(e.getX() - (eraserWidth + borderSize) / 2);
                eraserBorder.setY1(e.getY() - (eraserHeight + borderSize) / 2);
                eraserBorder.setEraserWidth(eraserWidth + borderSize);
                eraserBorder.setEraserHeight(eraserHeight + borderSize);
                eraserBorder.setType("EraserBorder");
                shapes.remove(eraserBorder);
                shapes.add(eraserBorder);
                // create new white rectangles to cover old shapes
                Shape eraser = new Shape(e.getX() - eraserWidth / 2, e.getY() - eraserHeight / 2,
                        BACKGROUND_COLOR, eraserWidth, eraserHeight);
                eraser.setType(type);
                shapes.add(eraser);
                repaint();
                break;
            case "Type":
            	break;
            default:
                endPointX = e.getX();
                endPointY = e.getY();
                shape.setX2((int) endPointX);
                shape.setY2((int) endPointY);
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

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    
    // Save the canvas as an image file
    public void saveImage(String type, File file) {
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
}
