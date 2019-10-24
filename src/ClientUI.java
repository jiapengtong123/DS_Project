import shapes.Shape;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientUI extends UnicastRemoteObject implements ClientUIInterface {
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    DrawingArea drawingArea = null;
    // Color Chooser, stroke
    private ColorChooser chooser = null;
    private BasicStroke stroke = (new BasicStroke(15f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND));

    private String ID = null;
    private String username = null;
    private String role = null;
    private JTextArea textAreaChatMessages;
    private JTextArea textAreaUserList;

//    public static void main(String[] args) throws RemoteException {
//        ClientUI ui = new ClientUI();
//        ui.start();
//    }

    public void startUI(String ip, String messagePort, String drawingPort) throws RemoteException {
        start();
    }

    public ClientUI(String ip, String messagePort, String drawingPort, String ID, String username, String role) throws RemoteException {
        super();
        drawingArea = new DrawingArea(ip, messagePort, drawingPort, ID);
        chooser = new ColorChooser();
        this.ID = ID;
        this.username = username;
        this.role = role;

        // start connection for receiving chat messages and user list
        Thread t = new Thread(() -> {
            ClientNetworkModule module = new ClientNetworkModule();
            module.setIP(ip);
            module.setPORT(messagePort);
            module.connect();

            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // send ID and get back chat messages and user list
                module.sendID(ID, "message_userlist");
                List<ChatMessage> chatMessages = module.receiveMessagesList();
                // reset message contents
                textAreaChatMessages.setText("");
                for (ChatMessage e : chatMessages) {
                    textAreaChatMessages.append(e.getUsername() + ": " +
                            String.join(" ", e.getContent().split("_")) + "\n");
                }
                // get all user names
                module.sendID(ID, "user_list");
                List<User> users = module.receiveUsersList();
                textAreaUserList.setText("");
                for (User user : users) {
                    textAreaUserList.append(user.getUsername() + "\n");
                }
            }
        });
        t.start();
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
//        newCanvas = new JMenuItem("New Canvas");
//        saveCanvas = new JMenuItem("Save Canvas");
        saveAs = new JMenuItem("Save As");
        openCanvas = new JMenuItem("Open Canvas");
        closeProgram = new JMenuItem("Close Program");

        // Add items to File menu
//        fileMenu.add(newCanvas);
//        fileMenu.add(saveCanvas);
        fileMenu.add(saveAs);
        // if is a manager show open option
        if ("manager".equals(role)) {
            fileMenu.add(openCanvas);
        }
        fileMenu.add(closeProgram);

        // Add File menu to menu bar
        menuBar.add(fileMenu);
        c.weightx = 0.5;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 3;
        pane.add(menuBar, c);

        // Background for drawingArea
//        JDesktopPane drawPanel = new JDesktopPane();
//        drawPanel.setPreferredSize(new Dimension(800, 600));
//        drawPanel.setBackground(Color.gray);
//        drawPanel.setVisible(true);
//        c.fill = GridBagConstraints.BOTH;
//        c.weighty = 1;
//        c.gridx = 0;
//        c.gridy = 1;
//        c.gridwidth = 1;
//        c.gridheight = 15;
//        pane.add(drawPanel, c);

        // test create drawing area
        drawingArea.setSize(600, 800);
        drawingArea.setBackground(Color.white);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 20;
        pane.add(drawingArea, c);

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

        JButton btnEraserSmaller = new JButton("-");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 8;
        c.gridheight = 1;
        pane.add(btnEraserSmaller, c);

        JButton btnEraserBigger = new JButton("+");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 9;
        c.gridheight = 1;
        pane.add(btnEraserBigger, c);

        JTextField textField;
        textField = new JTextField();
        c.insets = new Insets(0, 0, 5, 5);
        c.weightx = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 10;
        c.gridwidth = 1;
        pane.add(textField, c);

        JButton btnStroke = new JButton("Set Stroke");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 11;
        c.gridheight = 1;
        pane.add(btnStroke, c);

        JButton btnType = new JButton("Type text");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 12;
        c.gridheight = 1;
        pane.add(btnType, c);

        textAreaUserList = new JTextArea();
        JScrollPane jsp_userlist = new JScrollPane(textAreaUserList);
        c.insets = new Insets(0, 0, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 13;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipady = 100;
        pane.add(jsp_userlist, c);

        // add the textarea with a scroll panel
        textAreaChatMessages = new JTextArea();
        JScrollPane jsp = new JScrollPane(textAreaChatMessages);
        c.insets = new Insets(0, 0, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 14;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipady = 100;
        pane.add(jsp, c);

        // text field used to input message
        JTextField textFieldSend;
        textFieldSend = new JTextField();
        c.insets = new Insets(0, 0, 5, 5);
        c.weightx = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 15;
        c.gridwidth = 1;
        c.ipady = 5;
        pane.add(textFieldSend, c);

        JButton btnSend = new JButton("Send");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 16;
        c.gridheight = 1;
        pane.add(btnSend, c);

        // text field used to input message
        JTextField textFieldKickOut;
        textFieldKickOut = new JTextField();
        c.insets = new Insets(0, 0, 5, 5);
        c.weightx = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 17;
        c.gridwidth = 1;
        c.ipady = 5;
        pane.add(textFieldKickOut, c);

        JButton btnKickOut = new JButton("Kick Out");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 18;
        c.gridheight = 1;
        pane.add(btnKickOut, c);

        JButton btnQuit = new JButton("Quit");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 19;
        c.gridheight = 1;
        pane.add(btnQuit, c);

        // event listeners
        btnColorChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ColorChooser.drawingArea = drawingArea;
                chooser.showColorChooser();
            }
        });

        btnFreeDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Free_Draw");
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

        btnEraserSmaller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setEraserHeight(drawingArea.getEraserHeight() - 5);
                drawingArea.setEraserWidth(drawingArea.getEraserWidth() - 5);
            }
        });

        btnEraserBigger.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setEraserHeight(drawingArea.getEraserHeight() + 5);
                drawingArea.setEraserWidth(drawingArea.getEraserWidth() + 5);
            }
        });

        btnStroke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String stroke = textField.getText();
                drawingArea.setStroke(new BasicStroke(Float.parseFloat(stroke), BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
            }
        });

        btnType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                drawingArea.setType("Type");
            }
        });

        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (drawingArea != null) {
                    System.out.println(ID);
                    drawingArea.getMessageConnection().sendChatMessage(ID, new ChatMessage(ID, username,
                            String.join("_", textFieldSend.getText().trim().split(" "))));
                }
            }
        });

        btnKickOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (drawingArea != null) {
                    drawingArea.getMessageConnection().sendKickOutUsername(ID,
                            String.join("_", textFieldKickOut.getText().trim().split(" ")));
                }
            }
        });

        btnQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (drawingArea != null) {
                    drawingArea.getMessageConnection().stop(ID);
                }
            }
        });


        // Menu event listeners
//        newCanvas.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//
//            	// Get new canvas dimensions
//            	JPanel container = new JPanel();
//            	JTextField canvasWidth = new JTextField(4);
//            	JTextField canvasHeight = new JTextField(4);
//            	JLabel canvasWarning = new JLabel("The current canvas will be replaced");
//
//            	if (drawingArea != null) {
//            		container.setLayout(new GridLayout(0, 1, 2, 2));
//            		container.add(canvasWarning);
//            	}
//
//            	container.add(new JLabel("Width (px): "));
//            	container.add(canvasWidth);
//
//            	container.add(new JLabel("Height (px): "));
//            	container.add(canvasHeight);
//
//            	int option = JOptionPane.showConfirmDialog(drawPanel, container, "Please enter new canvas dimensions", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//
//            	if (option == JOptionPane.YES_OPTION) {
//            		if (drawingArea != null) {
//            			drawingArea.setVisible(false);
//            			drawPanel.remove(drawingArea);
//            		}
//	                drawingArea = new DrawingArea();
//	                drawingArea.setSize(Integer.parseInt(canvasWidth.getText()), Integer.parseInt(canvasHeight.getText()));
//	                drawingArea.setHeight(Integer.parseInt(canvasHeight.getText()));
//	                drawingArea.setWidth(Integer.parseInt(canvasWidth.getText()));
//	                drawingArea.setPreferredSize(drawingArea.getPreferredSize());
//	                drawingArea.setBackground(Color.white);
//	                drawingArea.setVisible(true);
//	                c.fill = GridBagConstraints.BOTH;
//	                drawPanel.add(drawingArea, c);
//            	}
//
//            }
//
//        });

//        saveCanvas.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser chooser = new JFileChooser();
//                chooser.showSaveDialog(null);
//                File file = chooser.getSelectedFile();
//                try {
//
//                    FileOutputStream fileOut = new FileOutputStream(file);
//                    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//                    objectOut.writeObject(drawingArea);
//                    objectOut.close();
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//
//        });

        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
                FileNameExtensionFilter filter2 = new FileNameExtensionFilter("JPEG", "jpg");
                chooser.setFileFilter(filter);
                chooser.setFileFilter(filter2);
                chooser.setAcceptAllFileFilterUsed(false);

                chooser.showSaveDialog(null);
                String type = "png";

                // Set file extension type
                if (chooser.getFileFilter() instanceof FileNameExtensionFilter) {
                    String[] exts = ((FileNameExtensionFilter) chooser.getFileFilter()).getExtensions();
                    type = exts[0];
                }

                drawingArea.saveImage(type, chooser.getSelectedFile());
            }

        });

        openCanvas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File file = chooser.getSelectedFile();
                FileInputStream fi;

                try {
                    BufferedImage img = ImageIO.read(file);
                    int width = img.getWidth();
                    int height = img.getHeight();
                    System.out.println(width + " " + height);

//                    drawingArea.setBufferedImage(img);
//                    drawingArea.startListeners();
//                    drawingArea.setStroke(stroke);
//                    drawingArea.setVisible(true);
//                    drawingArea.setSize(width, height);
//                    c.fill = GridBagConstraints.BOTH;

                    // reset server image
                    drawingArea.clearShapes();
                    drawingArea.getMessageConnection().openCanvas(ID, img);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

//				try {
//					fi = new FileInputStream(file);
//	    			ObjectInputStream oi = new ObjectInputStream(fi);
//	    			if (drawingArea != null) {
//            			drawingArea.setVisible(false);
//            			drawPanel.remove(drawingArea);
//            		}
//
//	    			drawingArea = (DrawingArea) oi.readObject();
//	    			drawingArea.startListeners();
//	    			oi.close();
//
//	    			drawingArea.setStroke(stroke);
//	    			drawingArea.setVisible(true);
//	    			drawingArea.setPreferredSize(drawingArea.getPreferredSize());
//	                c.fill = GridBagConstraints.BOTH;
//	                drawPanel.add(drawingArea, c);
//
//				} catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					try {
//						BufferedImage img = ImageIO.read(file);
//						int width = img.getWidth();
//						int height = img.getHeight();
//						System.out.println(width + " " + height);
//						if (drawingArea != null) {
//	            			drawingArea.setVisible(false);
//	            			drawPanel.remove(drawingArea);
//	            		}
//
//		    			drawingArea = new DrawingArea();
//		    			drawingArea.addBg(img);
//		    			drawingArea.startListeners();
//		    			drawingArea.setStroke(stroke);
//		    			drawingArea.setVisible(true);
//		    			drawingArea.setSize(width,height);
//		                c.fill = GridBagConstraints.BOTH;
//		                drawPanel.add(drawingArea, c);
//
//					} catch (IOException e2) {
//						e1.printStackTrace();
//					}
//				} catch (ClassNotFoundException e1) {
//					e1.printStackTrace();
//				}


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
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
