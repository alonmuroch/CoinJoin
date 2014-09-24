package Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
	
	public final int COINJOIN_VERSION = 10000;
	
	 public static void main(String[] args) {
		 args = new String[1];
		 System.out.println("Are you client or server?");
		 Scanner in = new Scanner(System.in);
		 System.out.print(">>> ");
		 String s = in.nextLine().toLowerCase();
		 while(true){
			 if (s.equals("client")){
				 try {
					 	ClientWorker worker = new ClientWorker();
					 	new Thread(worker).start();
						NioClient client = new NioClient(InetAddress.getByName("162.213.253.147"), 8335, worker);
						Thread t = new Thread(client);
						t.setDaemon(true);
						t.start();
						client.connect();
						System.out.println("Connected to server, send version message? [y/n]");
						System.out.print(">>> ");
						s="";
						s = in.nextLine().toLowerCase();
						if (s.equals("y")){
							System.out.println("Sending version message...");
							Version ver = new Version();
							Message msg = new Message(Command.VERSION, ver.serialize());
							System.out.println(Utils.bytesToHex(msg.serialize()));
							client.send(msg.serialize());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				 break;
			 }
			 else if (s.equals("server")){
				 System.out.println("Listening...");
				 try {
						ServerWorker worker = new ServerWorker();
						new Thread(worker).start();
						new Thread(new NioServer(null, 8335, worker)).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				 break;
			 }
			 else {
				 System.out.println("Pick one.");
				 System.out.print(">>> ");
				 s = in.nextLine().toLowerCase();
			 }
		 }
		 
	 }
	 
}
