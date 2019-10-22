import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;

public class ServerNetworkModule {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private Gson gson = new Gson();

    public ServerNetworkModule() {

    }

    public Message getMessage() {
        try {
            return gson.fromJson(input.readUTF(), Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(Message message) {
        try {
            output.writeUTF(gson.toJson(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getInput() {
        return input;
    }

    public void setInput(DataInputStream input) {
        this.input = input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void setOutput(DataOutputStream output) {
        this.output = output;
    }
}
