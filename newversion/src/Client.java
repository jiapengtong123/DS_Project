import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

// client use rmi object to run client ui and server functions


public class Client extends Thread

{
	
	
	 private static DatagramSocket ds;
    public static void main(String[] args)
    {
        Client client = new Client();
        client.start();
    }

    public void run()
    {
        try {
            // start a connection by socket with ip and port
        	
            ClientNetworkModule connection = new ClientNetworkModule();
            connection.setIP("localhost");
            connection.setPORT("3005");
            connection.connect();
          
            
            String name = JOptionPane.showInputDialog(null,"What is your name");	
        	
        	try {
				ds = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
        	byte[] b2=name.getBytes();
    		InetAddress ia = null;
			try {
				ia = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		DatagramPacket dp1 = new DatagramPacket(b2,b2.length,ia,9998);
    		try {
				ds.send(dp1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    		
    		byte[] b1 = new byte[9999];
    		DatagramPacket dp = new DatagramPacket(b1,b1.length);
    		try {
				ds.receive(dp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    		String ans = new String(dp.getData(),0,dp.getLength());
    		
    		System.out.println(ans);
    		
    		String Man="Manager";
    		String na="Notapproved";
    		String a="Approved";
    		
    		if(ans.equals(Man))
    			
    		{
    		
    			
    			
    			

				Runnable t2 = new Runnable()
				{
					
					
				
				public void run()
					
				{
    			
    			while(true)
    			{
    				
    				//listenting for the request of other client
    				System.out.println("Manager is listening to request for other users");
    				
    				byte[] b1x = new byte[9999];
    				DatagramPacket dpx = new DatagramPacket(b1x,b1x.length);
    				try {
    					ds.receive(dpx);
    					
    				} catch (IOException e)
    				{
    					
    					e.printStackTrace();
    				}
    				
    				String ansx = new String(dpx.getData(),0,dpx.getLength());
    				
    				int choice = JOptionPane.showConfirmDialog(null, "Can  "+ansx+ "join ?");
    				
    				byte[] bxx=String.valueOf(choice).getBytes();
    				
    				InetAddress iaxx = null;
    				try 
    				{
    					iaxx = InetAddress.getLocalHost();
    					
    				} catch (UnknownHostException e)
    				{
    					
    					e.printStackTrace();
    				}
    				DatagramPacket dp1xx = new DatagramPacket(bxx,bxx.length,iaxx,9998);
    				
    				try 
    				{
    					ds.send(dp1xx);
    					
    				} catch (IOException e) 
    				{
    					
    					e.printStackTrace();
    				}
    					
    			}
    			
    			
    			
				}
				
				
			};
			
			
			
		
				
				Thread t = new Thread(t2);
				t.start();
			
    			   
 	           ClientUIInterface UI = connection.getRmiObject(connection.getRmiName(name));
 	            
 	            // start the whiteboard ui
 	            connection.stop();
 	            
 	            UI.startUI("localhost", "3005", "3006");
 	            System.out.println("start ui");
         
            
    		}
    		
    		
    		if(ans.equals(a))
    			
    		{
    		
    		
    		
            
            ClientUIInterface UI = connection.getRmiObject(connection.getRmiName("Jerry"));
            // start the whiteboard ui
            connection.stop();
            
            UI.startUI("localhost", "3005", "3006");
            System.out.println("start ui");
        }	
    		
    		
    		
    		
    		else 
    		
    		{	
    		
    			JOptionPane.showInputDialog(null,"Not allowed by the manager, cheers!!");
    		}
    	
    	} 
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
