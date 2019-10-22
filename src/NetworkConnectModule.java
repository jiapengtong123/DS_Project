import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// handle connection to server and return a rmi object to client to use
public class NetworkConnectModule {
    private String IP = "";
    private String PORT = "";
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private String rmiName = null;

    NetworkConnectModule() {

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

    // send our user name in a json format to server and get back a rmi name to use
    public String getRmiName(String username) {
        try {
            // convert username to json and send to server
            Gson gson = new Gson();
            output.writeUTF(gson.toJson(username));
            output.flush();

            // get back the rmi name, then we can use the server object
            String dataReceived = input.readUTF();
            rmiName = gson.fromJson(dataReceived, String.class);
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
}
