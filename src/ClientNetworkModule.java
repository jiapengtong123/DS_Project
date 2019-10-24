import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import shapes.Shape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;
import java.util.List;

// handle connection to server and return a rmi object to client to use
public class ClientNetworkModule {
    private String IP = "";
    private String PORT = "";
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private String rmiName = null;

    ClientNetworkModule() {

    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPORT(String PORT) {
        this.PORT = PORT;
    }

    // start a connection use socket
    public String connect() {
        try {
            socket = new Socket(IP, Integer.parseInt(PORT));
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            System.out.println("connecting");
            return "successfully connect...";
        } catch (IOException e) {
            e.printStackTrace();
            return "error during connecting";
        }
    }

    public void stop() {
        try {
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send our user name in a json format to server and get back a rmi name to use
    public String getRmiName(String username) {
        try {
            // convert username to json and send to server
            Gson gson = new Gson();
            output.writeUTF(gson.toJson(new Message("connect_user", username)));
            output.flush();

            // get back the rmi name, then we can use the server object
            rmiName = (String) gson.fromJson(input.readUTF(), Message.class).getData();
            System.out.println(rmiName);
            return rmiName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // user rmi name to get the server object to use
    public ClientUIInterface getRmiObject(String rmiName) {
        try {
            // get registry ip and port
            Registry registry = LocateRegistry.getRegistry(IP, 1099);
            // get client ui interface
            ClientUIInterface UI = (ClientUIInterface) registry.lookup(rmiName);
            // return the rmi interface
            return UI;
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    // add a new shape to server
    public String sendShape(String ID, List<Shape> shapes) {
        try {
            // convert username to json and send to server
            Gson gson = new Gson();
            output.writeUTF(gson.toJson(new Message(ID, "add_shape", shapes)));
            output.flush();
            // get back buffer image object
            Message message = gson.fromJson(input.readUTF(), Message.class);
            return (String) message.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    // receive buffer image from server
    public BufferedImage receiveBufferImage() {
        try {
            Gson gson = new Gson();
            // get back buffer image object
            Message message = gson.fromJson(input.readUTF(), Message.class);
            BufferedImage bufferedImage = decodeToImage((String) message.getData());
            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendID(String ID, String option) {
        try {
            // convert username to json and send to server
            Gson gson = new Gson();
            output.writeUTF(gson.toJson(new Message(ID, option, "")));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessage> receiveMessagesList() {
        Type typeChatMessageType = new TypeToken<List<ChatMessage>>() {
        }.getType();
        try {
            Gson gson = new Gson();
            Message message = gson.fromJson(input.readUTF(), Message.class);
            List<ChatMessage> chatMessages = gson.fromJson(message.getData().toString(), typeChatMessageType);

            return chatMessages;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendChatMessage(String ID, ChatMessage chatMessage) {
        try {
            Gson gson = new Gson();
            output.writeUTF(gson.toJson(new Message(ID, "chat_message", chatMessage)));
            output.flush();
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public List<User> receiveUsersList() {
        Type type = new TypeToken<List<User>>() {
        }.getType();
        try {
            Gson gson = new Gson();
            Message message = gson.fromJson(input.readUTF(), Message.class);
            List<User> users = gson.fromJson(message.getData().toString(), type);

            return users;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
