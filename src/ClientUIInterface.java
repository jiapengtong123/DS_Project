import shapes.Shape;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientUIInterface extends Remote
{
    public void startUI(String ip, String messagePort, String drawingPort) throws RemoteException;
}
