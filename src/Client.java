import java.rmi.RemoteException;

// client use rmi object to run client ui and server functions
public class Client extends Thread {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void run() {
        try {
            // start a connection by socket with ip and port
            ClientNetworkModule connection = new ClientNetworkModule();
            connection.setIP("localhost");
            connection.setPORT("3005");
            connection.connect();
            // server give a rmi stub name and use it to get the rmi object
            ClientUIInterface UI = connection.getRmiObject(connection.getRmiName("Jerry"));
            // start the whiteboard ui
            connection.stop();
            UI.startUI("localhost", "3005", "3006");
            System.out.println("start ui");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
