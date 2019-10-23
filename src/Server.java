import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import shapes.Shape;

import javax.imageio.ImageIO;
import javax.net.ServerSocketFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Server {
    private Registry registry;
    private List<Shape> shapes;
    private Graphics2D g2 = null;
    private BufferedImage bufferedImage = null;
    private List<ClientUIInterface> clients = new ArrayList<>();

    Server() {
        try {
            // define rmi port, 1099 is default
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        shapes = new ArrayList<Shape>();
        bufferedImage = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);
        g2 = bufferedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 600, 800);
    }

    public static void main(String[] args) {
        try {
            // start socket port
            Server server = new Server();
            Thread messageThread = new Thread(() -> server.messageStart("3005"));
            Thread drawingThread = new Thread(() -> server.drawingStart("3006"));
            // start thread for handle message and add new shapes
            messageThread.start();
            // start thread for sending buffer image to all clients
            drawingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // start message thread
    public void messageStart(String port) {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try (ServerSocket server = factory.createServerSocket(Integer.parseInt(port))) {
            System.out.println("Waiting for client connection-");

            // Wait for connections.
            while (true) {
                Socket client = server.accept();
                System.out.println("Client " + ": Applying for connection!");

                // Start a new thread for a connection
                Thread t = new Thread(() -> serveClient(client));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // receive socket connection request, create an UUID for client rmi stub
    private void serveClient(Socket client) {
        try (Socket clientSocket = client) {
            ServerNetworkModule serverNetworkModule = new ServerNetworkModule();
            serverNetworkModule.setInput(new DataInputStream(clientSocket.getInputStream()));
            serverNetworkModule.setOutput(new DataOutputStream(clientSocket.getOutputStream()));
            // store option use to stop server thread
            String option = "";

            while (!"stop".equals(option)) {
                Message message = serverNetworkModule.getMessage();
                option = message.getOption();
                System.out.println("CLIENT " + " says: " + message.getData());
                serverNetworkModule.sendMessage(handleOptions(message));
            }

            serverNetworkModule.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // generate an unique rmi stub name for each client
    public String generateID(String username) {
        return username + UUID.randomUUID();
    }

    public Message handleOptions(Message message) throws RemoteException {
        // if a new user connect, create a rmi object form him and return the stub name
        if ("connect_user".equals(message.getOption())) {
            // generate an unique id and send back, data should be username
            String ID = generateID((String) message.getData());
            ClientUIInterface ui = new ClientUI();
            clients.add(ui);
            // bind a new stub name with id
            registry.rebind(ID, ui);
            return new Message("", ID);
        }

        // add a new shape to the shape list, convert the canvas to image buffer and send back to client
        if ("add_shape".equals(message.getOption())) {
            Type typeShapeType = new TypeToken<Shape>() {
            }.getType();
            Gson gson = new Gson();

            System.out.println("add a new shape into white board.");
            // covert message to shape data
            Shape new_shape = gson.fromJson(message.getData().toString(), typeShapeType);
            // add new shape
            shapes.add(new_shape);
            // update graphic
            for (Shape shape : shapes) {
                shape.draw(g2);
            }
            // send back new image buffer messages
            return new Message("", "success");
        }
        return null;
    }


    // handle whiteboard shape data
    public void drawingStart(String port) {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try (ServerSocket server = factory.createServerSocket(Integer.parseInt(port))) {
            System.out.println("Waiting for client connection-");
            // Wait for connections.
            while (true) {
                Socket client = server.accept();
                System.out.println("Client " + ": Applying for connection!");

                // Start a new thread for a connection
                Thread t = new Thread(() -> serveDrawingClient(client));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serveDrawingClient(Socket client) {
        try (Socket clientSocket = client) {
            ServerNetworkModule serverNetworkModule = new ServerNetworkModule();
            serverNetworkModule.setInput(new DataInputStream(clientSocket.getInputStream()));
            serverNetworkModule.setOutput(new DataOutputStream(clientSocket.getOutputStream()));
            while (true) {
                serverNetworkModule.sendMessage(updateBufferImage());
            }
//            serverNetworkModule.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // send back new image buffer messages
    private Message updateBufferImage() throws RemoteException {
        return new Message("", encodeToString(bufferedImage, "png"));
    }

    // encode buffer image to string to transfer by json
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
}
