import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {

        try {
            //Connect to the rmiregistry that is running on localhost
            Registry registry = LocateRegistry.getRegistry("localhost");

            //Retrieve the stub/proxy for the remote math object from the registry
//            IRemoteCanvas remoteMath = (IRemoteCanvas) Naming.lookup("//localhost:3005/Canvas");
            //Call methods on the remote object as if it was a local object
            System.out.println("Client: calling remote methods");
//            remoteMath.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
