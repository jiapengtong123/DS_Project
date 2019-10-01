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

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        // Drop down menu items
        JMenuItem newCanvas, saveCanvas, saveAs, openCanvas, closeProgram;
        newCanvas = new JMenuItem("New Canvas");
        saveCanvas = new JMenuItem("Save Canvas");
        saveAs = new JMenuItem("Save As");
        openCanvas = new JMenuItem("Open Canvas");
        closeProgram = new JMenuItem("Close Program");

        // Add items to File menu
        fileMenu.add(newCanvas);
        fileMenu.add(saveCanvas);
        fileMenu.add(saveAs);
        fileMenu.add(openCanvas);
        fileMenu.add(closeProgram);

        // Add File menu to menu bar
        menuBar.add(fileMenu);
        c.weightx = 0.5;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        pane.add(menuBar, c);

        // Background for drawingArea
        JDesktopPane drawPanel = new JDesktopPane();
        drawPanel.setPreferredSize(new Dimension(800, 600));
        drawPanel.setBackground(Color.gray);
        drawPanel.setVisible(true);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 8;
        pane.add(drawPanel, c);

        JButton btnColorChooser = new JButton("Color Chooser");
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        pane.add(btnColorChooser, c);

        JButton btnFreeDraw = new JButton("Free Draw");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        pane.add(btnFreeDraw, c);

        JButton btnLine = new JButton("Line");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 3;
        c.gridheight = 1;
        pane.add(btnLine, c);

        JButton btnCircle = new JButton("Circle");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 4;
        c.gridheight = 1;
        pane.add(btnCircle, c);

        JButton btnRectangle = new JButton("Rectangle");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 5;
        c.gridheight = 1;
        pane.add(btnRectangle, c);

        JButton btnOval = new JButton("Oval");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 6;
        c.gridheight = 1;
        pane.add(btnOval, c);

        JButton btnEraser = new JButton("Eraser");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 7;
        c.gridheight = 1;
        pane.add(btnEraser, c);

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

        btnEraser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Eraser");
            }
        });

        // Menu event listeners
        newCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                drawingArea = new DrawingArea();
                drawingArea.setSize(width, height);
                drawingArea.setPreferredSize(drawingArea.getPreferredSize());
                drawingArea.setBackground(Color.white);
                drawingArea.setVisible(true);
                c.fill = GridBagConstraints.BOTH;
                drawPanel.add(drawingArea, c);

            }

        });

        saveCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(null);

            }

        });

        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(null);

            }

        });

        openCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);

            }

        });

        closeProgram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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
