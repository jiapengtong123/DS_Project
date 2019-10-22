import com.google.gson.Gson;

import javax.net.ServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class Server {
    private Registry registry;

    Server() {
        try {
            // define rmi port, 1099 is default
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            // start socket port
            new Server().start("3005");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start(String port) {
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
            // Input stream
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output Stream
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            String sendData = "";
            String receiveData = "";
            Gson gson = new Gson();

            while (true) {
                receiveData = input.readUTF();
                receiveData = gson.fromJson(receiveData, String.class);

                System.out.println("CLIENT " + " says: " + receiveData);

                // generate an unique id and send back
                sendData = generateID(receiveData);

                // bind a new stub name with id
                registry.rebind(sendData, new ClientUI());

                output.writeUTF(sendData);
            }

//            input.close();
//            output.close();
//            clientSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // generate an unique rmi stub name for each client
    public String generateID(String username) {
        return username + UUID.randomUUID();
    }

    // bind a new client ui with the stub name


}
