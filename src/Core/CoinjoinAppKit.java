package Core;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

import Core.DNSSeeds.Seed;
import Core.NetworkAddress.NetworkType;
import Core.Reject.ccode;

public class CoinjoinAppKit {

	public static byte[] nonce;
	NioServer server;
	public final int COINJOIN_VERSION = 10000;
	public static PeerGroup peergroup;
	
	public CoinjoinAppKit(){
		server = new NioServer(8000);
		SecureRandom sr = new SecureRandom();
		nonce = new byte[8];
		sr.nextBytes(nonce);
	}
	
	public void run() throws InterruptedException {
		System.out.println("CoinJoin Network Protocol 0.1.0");
		System.out.println("Type 'help' for a list of commands");
		String cmd = null;
		while (true){
			System.out.println("Enter a command:");
			Scanner in = new Scanner(System.in);
			System.out.print(">>> ");	
			cmd = in.nextLine();
			switch(cmd.toLowerCase()){
				case "start":
					start();
					break;
					
				case "next":
					break;	
					
				case "help":
					printHelp();
					break;
			}
		}
	}
	
	public void start() {
		System.out.println("CoinJoin: Starting server...");
		new Thread(new Runnable() {
		    public void run() {
		    	try {server.run();} 
				catch (Exception e) {e.printStackTrace();}
		    }
		}).start();
		System.out.println("CoinJoin: Connecting to peers...");
    	peergroup = new PeerGroup();
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
	
	
	
}
