
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class serverrmi implements Serializable
{

	
	static int i=0;
	
	static Vector<DatagramPacket> soc = new Vector<>(); 
	static Vector<DatagramPacket> cursoc = new Vector<>(); 
	static Vector<Object> draw = new Vector<>(); 
	
	public static void main(String[] a) throws Exception
{
		 DatagramSocket ds = new DatagramSocket(9998);
	
	
	
	while(true)
	{
		System.out.println("waiting........... : ");
		byte[] b1 = new byte[1024];
		DatagramPacket dp = new DatagramPacket(b1,b1.length);
		ds.receive(dp);
		
		
		String nameofstudent = new String(dp.getData(),0,dp.getLength());
		
		System.out.println("New client request received : ");
		soc.add(dp);
		
		
		
		if(i==0)
		{
			
			String name= "Manager";
			
			byte[] b2=name.getBytes();
			InetAddress ia = InetAddress.getLocalHost();
			DatagramPacket dp1 = new DatagramPacket(b2,b2.length,ia,dp.getPort());
			ds.send(dp1);
			
			
			
			System.out.println("Adding this client as Manager "+i); 
			i++; 
	        cursoc.add(dp);
		
			
		}
		else if(i>0)
		{
			
			
				DatagramPacket x=cursoc.firstElement();
				byte[] b2=nameofstudent.getBytes();
				InetAddress ia = InetAddress.getLocalHost();
				DatagramPacket dp1 = new DatagramPacket(b2,b2.length,ia,x.getPort());
				ds.send(dp1);
			
				byte[] b2x = new byte[1024];
				DatagramPacket dpx = new DatagramPacket(b2x,b2x.length);
				ds.receive(dpx);
				String reply = new String(dpx.getData(),0,dpx.getLength());
				
				
				int choice=Integer.parseInt(reply.trim());
				
				if(choice==JOptionPane.YES_OPTION)
				{
					//getting the canvas from manager
					
					System.out.println("waiting for the canves");
					byte[] b2o = new byte[9999];
					DatagramPacket dpxo = new DatagramPacket(b2o,b2o.length);
					ds.receive(dpxo);
					byte[] data = dpxo.getData();
					System.out.println("canvas received");
					
					//approval sending
					
					System.out.println("Approved by Manager adding now");
					String ans="Approved";
					byte[] b2xy=ans.getBytes();
					InetAddress iaxy = InetAddress.getLocalHost();
					DatagramPacket dpxy = new DatagramPacket(b2xy,b2xy.length,iaxy,dp.getPort());
					ds.send(dpxy);

					//canvas sending
										
					InetAddress it = InetAddress.getLocalHost();
					DatagramPacket im = new DatagramPacket(data,data.length,it,dp.getPort());
					ds.send(im);
					
					System.out.println("canvas sent to new client");
					
			        cursoc.add(dp);
			        
			        i++;
			        
				}
				else 
				{
					
					String ans="NotApproved";
					byte[] b2xy=ans.getBytes();
					InetAddress iaxy = InetAddress.getLocalHost();
					DatagramPacket dpxy = new DatagramPacket(b2xy,b2xy.length,iaxy,dp.getPort());
					ds.send(dpxy);
					System.out.println("Rejected by Manager ");
					
					
					
				}
				
			}
		
		Thread t = new Thread(()->
		{
		
			DatagramSocket d = null;
			try {
				d = new DatagramSocket(9997);
			} catch (SocketException e3)
			{
				
				e3.printStackTrace();
			}
			while(true)
			{
				byte[] b2x = new byte[9999];
				DatagramPacket dpxox = new DatagramPacket(b2x,b2x.length);
				try 
				{
					d.receive(dpxox);
					
				} catch (IOException e2) 
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				byte[] datax = dpxox.getData();
				
				for (DatagramPacket yeah:cursoc)
				{
					
					InetAddress itxx = null;
					try 
					{
						
						itxx = InetAddress.getLocalHost();
						
					} catch (UnknownHostException e1) 
					{
						
						e1.printStackTrace();
					}
					DatagramPacket im = new DatagramPacket(datax,datax.length,itxx,yeah.getPort());
					try
					{
						d.send(im);
					} catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}
			
		});
		
		if (i==0)
		{
			t.start();
		}
		
		
		
		
		
	}
 }
}

