import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import shapes.Shape;

import javax.imageio.ImageIO;
import javax.net.ServerSocketFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
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
    private BufferedImage emptyImage = null;
    private static String messagePort = "3005";
    private static String drawingPort = "3006";
    private Boolean isUpdated = false;
    // chat room users
    private List<User> userList = new ArrayList<>();
    // users' messages
    private List<ChatMessage> chatMessages = new ArrayList<>();

    Server() {
        try {
            // define rmi port, 1099 is default
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        shapes = new ArrayList<Shape>();
        // set up the buffer image for all clients
        bufferedImage = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);
        g2 = bufferedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 600, 800);

        // setup an empty image for kickout users
        emptyImage = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D empty_g2 = emptyImage.createGraphics();
        empty_g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        empty_g2.setColor(Color.WHITE);
        empty_g2.fillRect(0, 0, 600, 800);

        // test data for chat messages
        chatMessages.add(new ChatMessage("foo11", "Tom", "hello_world".trim()));
    }

    public static void main(String[] args) {
        try {
            // start socket port
            Server server = new Server();
            Thread messageThread = new Thread(() -> server.messageStart(messagePort));
            Thread drawingThread = new Thread(() -> server.drawingStart(drawingPort));
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
            System.out.println("Waiting for client message connection-");

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
            Message message = new Message();

            while (!"stop".equals(option)) {
                message = serverNetworkModule.getMessage();
                option = message.getOption();
//                System.out.println("CLIENT " + " says: " + message.getData());
                serverNetworkModule.sendMessage(handleOptions(message));
            }

            stop(message.getID());
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
            String role = "normal";

            // add new user into user list and set role
            if (userList.size() == 0) {
                role = "manager";
                userList.add(new User(ID, (String) message.getData(), role, false));
            } else {
                role = "normal";
                userList.add(new User(ID, (String) message.getData(), role, false));
            }

            ClientUIInterface ui = new ClientUI("localhost", messagePort, drawingPort, ID, (String) message.getData(), role);

            // bind a new stub name with id
            registry.rebind(ID, ui);
            return new Message("", ID);
        }

        // add a new shape to the shape list, convert the canvas to image buffer and send back to client
        if ("add_shape".equals(message.getOption())) {
            Type typeShapeType = new TypeToken<List<Shape>>() {
            }.getType();
            Gson gson = new Gson();
            // covert message to shape data
            ArrayList<Shape> new_shapes = gson.fromJson(message.getData().toString(), typeShapeType);

//            for (Shape shape : new_shapes){
//                System.out.println(shape.getType());
//            }

            // if user in the list
            for (User user : userList) {
                if (user.getID().equals(message.getID())) {
                    // add new shape
                    shapes.addAll(new_shapes);
                    // update graphic
                    for (Shape shape : shapes) {
                        shape.draw(g2);
                    }
                    // send back new image buffer messages
                    return new Message("", "success");

                }
            }

            return new Message("", "fail");
        }

        // return the message list
        if ("message_userlist".equals(message.getOption())) {
//            System.out.println(message.getID());
            // user in the list can receive the messages
            for (User user : userList) {
                if (user.getID().equals(message.getID())) {
                    return new Message("", chatMessages);
                }
            }
            // user not in the list will receive an empty message
            return new Message("", new ArrayList<ChatMessage>());
        }

        // if user in the list, add his message
        if ("chat_message".equals(message.getOption())) {
            Type type = new TypeToken<ChatMessage>() {
            }.getType();
            Gson gson = new Gson();
            // user ID in list
            for (User user : userList) {
                if (user.getID().equals(message.getID())) {
                    chatMessages.add(gson.fromJson(message.getData().toString(), type));
                }
            }
        }

        // if user in the list, return all users in the room
        if ("user_list".equals(message.getOption())) {
            // user in the list can receive the messages
            for (User user : userList) {
                if (user.getID().equals(message.getID())) {
                    return new Message("", userList);
                }
            }
            // user not in the list will receive an empty message
            return new Message("", new ArrayList<User>());
        }

        // if a manager want to kickout a user
        if ("kick_out".equals(message.getOption())) {
            Gson gson = new Gson();
            for (User user : userList) {
                if (user.getID().equals(message.getID()) && user.getRole().equals("manager")) {
                    String kickout_username = gson.fromJson(message.getData().toString(), String.class);
                    kickOutUser(kickout_username, "");
                }
            }
        }
        // reset buffer image to the manager's image
        if ("open_canvas".equals(message.getOption())) {
            BufferedImage bufferedImage = decodeToImage((String) message.getData());
            openCanvas(bufferedImage);
        }
        return null;
    }


    // handle whiteboard shape data
    public void drawingStart(String port) {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        try (ServerSocket server = factory.createServerSocket(Integer.parseInt(port))) {
            System.out.println("Waiting for client drawing connection-");
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
//                synchronized (bufferedImage) {
//                    while (!isUpdated) {
//                        bufferedImage.wait();
//                    }
                Message message = serverNetworkModule.getMessage();
                serverNetworkModule.sendMessage(updateBufferImage(message));
//                }
            }
//            serverNetworkModule.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send back new image buffer messages
    private Message updateBufferImage(Message message) throws RemoteException {
        String ID = message.getID();

        // user ID in list
        for (User user : userList) {
            if (user.getID().equals(ID)) {
                // send the current image
                return new Message("", encodeToString(bufferedImage, "jpg"));
            }
        }

        // user is kick out not in the list, send an empty image
        return new Message("", encodeToString(emptyImage, "jpg"));
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

    // kick out a user from the list
    public void kickOutUser(String username, String id) {
        boolean userExist = false;
        int index = 0;

        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                userExist = true;
                index = userList.indexOf(user);
            }
        }

        if (userExist) {
            userList.remove(index);
        }
    }

    // user choose to stop, remove user from list
    public void stop(String ID) {
        int index = 0;
        for (User user : userList) {
            if (user.getID().equals(ID)) {
                index = userList.indexOf(user);
            }
        }
        userList.remove(index);
    }

    public void openCanvas(BufferedImage bufferedImage) {
        shapes.clear();
        g2 = bufferedImage.createGraphics();
        this.bufferedImage = bufferedImage;
    }

    // decode image string to a buffer image
    public static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
