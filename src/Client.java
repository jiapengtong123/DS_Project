import java.rmi.RemoteException;

// client use rmi object to run client ui and server functions
public class Client {
    public static void main(String[] args) {
        try {
            // start a connection by socket with ip and port
            NetworkConnectModule connection = new NetworkConnectModule();
            connection.setIP("localhost");
            connection.setPORT("3005");
            connection.connect();
            // server give a rmi stub name and use it to get the rmi object
            ClientUIInterface UI = connection.getRmiObject(connection.getRmiName("Tom"));
            // start the whiteboard ui
            UI.startUI();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
