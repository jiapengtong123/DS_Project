import java.rmi.*;

public class serverrmi
{
	public static void main (String a[]) throws Exception
	{
		ClientUI obj = new ClientUI();
		Naming.rebind("letstry", obj);
		System.out.println("server has started");
		
	}

}
