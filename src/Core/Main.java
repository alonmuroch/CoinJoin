package Core;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	public static CoinjoinAppKit coinjoin;
			
	public final int COINJOIN_VERSION = 10000;
	public static PeerGroup peergroup = new PeerGroup();
	static boolean test = true;
		
	public static void main(String[] args) throws InterruptedException, IOException {
		coinjoin = new CoinjoinAppKit();
		coinjoin.run();
		if (!test){
		args = new String[1];
		System.out.println("Are you client or server?");
		Scanner in = new Scanner(System.in);
		System.out.print(">>> ");
		String s = in.nextLine().toLowerCase();
		while(true){
			if (s.equals("client")){
				System.out.println("Enter IP address or domain name of server:");
				System.out.print(">>> ");
				s = in.nextLine().toLowerCase();
				try {new NioClient(s, 8000).run();} catch (Exception e) {e.printStackTrace();}
				break;
			}
			else if (s.equals("server")){
				System.out.println("Listening...");
				new NioServer(8000).run();
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
		 
}

