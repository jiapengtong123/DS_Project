import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientUIInterface extends Remote {
    public void startUI() throws RemoteException;
}
