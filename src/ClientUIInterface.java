import shapes.Shape;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientUIInterface extends Remote {
    public void startUI() throws RemoteException;

    public void addShape(Shape shape) throws RemoteException;
}
