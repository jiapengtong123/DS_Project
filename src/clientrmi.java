  
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import shapes.Shape; 
  
public class clientrmi implements Serializable
{ 
    public static DrawingArea drawingArea=null;
    public static List<Shape> shapes = new ArrayList<Shape>();
  
    private static DatagramSocket ds;
    
   

	public static void main(String args[]) throws Exception 
    { 
    	String name = JOptionPane.showInputDialog(null,"What is your name");	
    	
    	ds = new DatagramSocket();
		
    	byte[] b2=name.getBytes();
		InetAddress ia = InetAddress.getLocalHost();
		DatagramPacket dp1 = new DatagramPacket(b2,b2.length,ia,9998);
		ds.send(dp1);
		
		byte[] b1 = new byte[1024];
		DatagramPacket dp = new DatagramPacket(b1,b1.length);
		ds.receive(dp);
		
		String ans = new String(dp.getData(),0,dp.getLength());
		
		System.out.println(ans);
		String Man="Manager";
		String na="Notapproved";
		String a="Approved";
		
		if(ans.equals(Man))
		{
			
			System.out.println("Manager is connected");
			
			ClientUI obj = new ClientUI();
			obj.start();
			
			
			int counter=0;
			while(true)
			{
				counter++;
				
				//listenting for the request of other client
				System.out.println("Manager is listening to request for other users");
				
				byte[] b1x = new byte[1024];
				DatagramPacket dpx = new DatagramPacket(b1x,b1x.length);
				try {
					ds.receive(dpx);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String ansx = new String(dp.getData(),0,dp.getLength());
				
				int choice = JOptionPane.showConfirmDialog(null, "New client requestjoin"+ "?");
				
				byte[] bxx=String.valueOf(choice).getBytes();
				InetAddress iaxx = null;
				try {
					iaxx = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatagramPacket dp1xx = new DatagramPacket(bxx,bxx.length,iaxx,9998);
				
				try {
					ds.send(dp1xx);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//choicesent
				
			
				
				//drawingimagesending
				
				ByteArrayOutputStream bStream = new ByteArrayOutputStream();
				ObjectOutput oo = null;
				try {
					oo = new ObjectOutputStream(bStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				try {
					oo.writeObject(shapes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				byte[] bo = bStream.toByteArray();
				
				InetAddress iao = null;
				try {
					iao = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatagramPacket dp1o = new DatagramPacket(bo,bo.length,iao,9998);
				
				try {
					ds.send(dp1o);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				System.out.println("Canvas sent");
				
				
				
			
			
		
			
				Runnable t2 = new Runnable()
				{
				public void run()
					
				{
				
				while(true)
				{
					
					ByteArrayOutputStream bStream = new ByteArrayOutputStream();
					ObjectOutput oo = null;
					try
					{
						oo = new ObjectOutputStream(bStream);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					try
					{
						oo.writeObject(shapes);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

					byte[] bo = bStream.toByteArray();
					
					InetAddress iao = null;
					try
					{
						iao = InetAddress.getLocalHost();
					} catch (UnknownHostException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatagramPacket dp1o = new DatagramPacket(bo,bo.length,iao,9997);
					
					try 
					{
						ds.send(dp1o);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//receiving the latest canvas
					
					byte[] bye = new byte[99999];
					DatagramPacket doooe = new DatagramPacket(bye,bye.length);
					try 
					{
						ds.receive(doooe);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					byte[] datae = doooe.getData();
					ByteArrayInputStream ine = new ByteArrayInputStream(datae);
					ObjectInputStream ise = null;
					try 
					{
						ise = new ObjectInputStream(ine);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					try 
					{
						shapes = (ArrayList)ise.readObject();
					} catch (ClassNotFoundException | IOException e)
					
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
					
				}

				
				
				
			
	
			
			
		}
		
		
	};
	
	if(counter==1)
	{	
		Thread t = new Thread(t2);
		t.start();
	}
	
			
	}	
}
		
		
		
		else if (ans.equals(a))
		{	
			
			
			System.out.println("waiting for canvas ");
			byte[] byyy = new byte[99999];
			DatagramPacket dooo = new DatagramPacket(byyy,byyy.length);
			ds.receive(dooo);
			System.out.println("canvas recieved");
			
			
			byte[] data = dooo.getData();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);
			
			System.out.println("coverting canvas object to use");
			shapes = (ArrayList)is.readObject(); 
			
			System.out.println(" object received successfully ");
			
			
			ClientUI obj = new ClientUI();
			
			obj.start();
			
			System.out.println("you are not a manager");
			
			Runnable to= new Runnable()
			{
				public void run()
				{
				while(true)
				{
					
					
					
					ByteArrayOutputStream bStream = new ByteArrayOutputStream();
					ObjectOutput oo = null;
					try {
						oo = new ObjectOutputStream(bStream);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					try {
						oo.writeObject(shapes);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

					byte[] bo = bStream.toByteArray();
					
					InetAddress iao = null;
					try {
						iao = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatagramPacket dp1o = new DatagramPacket(bo,bo.length,iao,9997);
					
					try {
						ds.send(dp1o);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//receiving the latest canvas
					
					byte[] bye = new byte[99999];
					DatagramPacket doooe = new DatagramPacket(bye,bye.length);
					try {
						ds.receive(doooe);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					byte[] datae = doooe.getData();
					ByteArrayInputStream ine = new ByteArrayInputStream(datae);
					ObjectInputStream ise = null;
					try {
						ise = new ObjectInputStream(ine);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					try {
						shapes = (ArrayList)ise.readObject();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
					
					
				}	
				}
			};
			Thread tx = new Thread(to);
			tx.start();
			
			
			
		}
			
		else 
			{
				JOptionPane.showInputDialog(null,"Request rejected by Manager");	
			}
			
		}
		
	}
			

		
	

 