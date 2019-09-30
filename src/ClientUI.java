import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;

public class ClientUI {
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    //    private Canvas canvas = null;
    DrawingArea drawingArea = null;
    // canvas width and height
    private static final int width = 800;
    private static final int height = 600;
    // Color Chooser
    private ColorChooser chooser = null;

    public static void main(String[] args) throws RemoteException {
        ClientUI ui = new ClientUI();
        ui.start();
    }

    public ClientUI() {
        chooser = new ColorChooser();
        drawingArea = new DrawingArea();
    }

    private void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            //natural height, maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        // elements and layout
        drawingArea.setSize(width, height);
        drawingArea.setBackground(Color.white);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 7;
        pane.add(drawingArea, c);

        JButton btnColorChooser = new JButton("Color Chooser");
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        pane.add(btnColorChooser, c);

        JButton btnFreeDraw = new JButton("Free Draw");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        pane.add(btnFreeDraw, c);

        JButton btnLine = new JButton("Line");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        pane.add(btnLine, c);

        JButton btnCircle = new JButton("Circle");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 3;
        c.gridheight = 1;
        pane.add(btnCircle, c);

        JButton btnRectangle = new JButton("Rectangle");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 4;
        c.gridheight = 1;
        pane.add(btnRectangle, c);

        JButton btnOval = new JButton("Oval");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 5;
        c.gridheight = 1;
        pane.add(btnOval, c);

        // event listeners
        btnColorChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ColorChooser.drawingArea = drawingArea;
                chooser.showColorChooser();
            }
        });

        btnFreeDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Free Draw");
            }
        });

        btnLine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Line");
            }
        });

        btnRectangle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Rectangle");
            }
        });

        btnCircle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Circle");
            }
        });

        btnOval.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Oval");
            }
        });
    }

    private void buildUI() {
        JFrame frame = new JFrame("White Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                buildUI();
            }
        });
    }

    public void paint(Graphics g) {
        g.fillOval(100, 100, 200, 200);
    }
}
