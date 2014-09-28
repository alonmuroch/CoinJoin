package Core;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

import Core.DNSSeeds.Seed;
import Core.Reject.ccode;

public class CoinjoinAppKit {

	public static byte[] nonce;
	ArrayList<NioClient> clients = new ArrayList<NioClient>();
	NioServer server;
	public final int COINJOIN_VERSION = 10000;
	public static PeerGroup peergroup = new PeerGroup();
	
	public CoinjoinAppKit(){
		server = new NioServer(8000);
		SecureRandom sr = new SecureRandom();
		nonce = new byte[8];
		sr.nextBytes(nonce);
	}
	
	public void run() throws InterruptedException {
		System.out.println("CoinJoin Network Protocol 0.1.0");
		System.out.println("Type 'help' for a list of commands");
		String cmd;
		while (true){
			System.out.println("Enter a command:");
			Scanner in = new Scanner(System.in);
			System.out.print(">>> ");	
			cmd = in.nextLine();
			switch(cmd.toLowerCase()){
				case "start":
					startServer();
					startClientForDNSConnect();
					break;
					
				case "next":
					break;	
					
				case "help":
					printHelp();
					break;
			}
		}
	}
	
	public void startClientForDNSConnect(){
		new Thread(new Runnable() {
		    public void run() {
		    	DNSSeeds seeds = new DNSSeeds();
		    	ArrayList<Seed> s = seeds.getSeeds();
		    	for (Seed seed : s){
		    		NioClient client = new NioClient(seed.getDomain(), seed.getPort());
		    		clients.add(client);
		    		try {client.run();} 
		    		catch (InterruptedException | IOException e) {
		    			e.printStackTrace();
		    			System.out.println("Couldn't connect to DNS seeds.");
		    		}
		    		try {send(Command.VERSION);} catch (IOException e) {e.printStackTrace();}		
		    		while(true){
		    			try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
		    			if (!client.close){
		    				try {send(Command.PING);} catch (IOException e) {e.printStackTrace();}
		    			}
		    			else {break;}
		    		}
		    	}
		    }
		}).start();
	}
	
	public void startServer() {
		System.out.println("CoinJoin: Starting server...");
		new Thread(new Runnable() {
		    public void run() {
		    	try {server.run();} 
				catch (Exception e) {e.printStackTrace();}
		    }
		}).start();
	}
	
	public void printHelp(){
		System.out.println("Commands: ");
		System.out.println("    -start                   Starts coinjoin sever and clients");
		System.out.println("    -done                    Closes all connections and exits the app");
		System.out.println("    -addnode <ip>            Adds a node and attempts to  connect to it");
		System.out.println("    -addseed <domain name>   Adds a DNS seed. Will attempt to connect to it at startup");
		System.out.println("    -send <command>          Broadcasts command to connected peers");
		System.out.println("    -help                    Displays this help menu");
	}
	
	public void send(Command cmd) throws IOException{
			
			Message message = null;
			switch(cmd){
			case VERSION:
				Version ver = new Version(nonce);
				message = new Message(Command.VERSION, ver.serialize());
				System.out.println("Sending VERSION message...");
				System.out.println("");
				break;
			
			case PING:
				Ping ping = new Ping();
				message = new Message(Command.PING, ping.serialize());
				System.out.println("Sending PING message...");
				System.out.println("");
				break;
			
			case REJECT:
				Reject r = new Reject(Command.VERSION, ccode.REJECT_MALFORMED);
				message = new Message(Command.REJECT, r.serialize());
				System.out.println("Sending REJECT message...");
				System.out.println("");
				System.out.print(">>> ");
				break;
				
			case GETADDR:
				message = new Message(Command.GETADDR, new byte[0]);
				System.out.println("Sending GETADDR message...");
				System.out.println("");
				break;
			}
			for (NioClient client : clients){
				client.send(message);
			}
	}
	
}
