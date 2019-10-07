import java.rmi.Naming;

public class clientrmi 
{
	public static void main (String a[]) throws Exception
	{
		rmi oj =(rmi)Naming.lookup("letstry");
		
		 
		oj.start();
		System.out.println("client started");
		 

		
		
		
	}
	
	
}