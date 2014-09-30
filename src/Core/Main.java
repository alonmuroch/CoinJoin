package Core;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	public static CoinjoinAppKit coinjoin;
	
	public static ArrayList<Peer> x = new ArrayList<Peer>();
	
		
	public static void main(String[] args) throws InterruptedException, IOException {
		coinjoin = new CoinjoinAppKit();
		coinjoin.run();
	}
		 
}

