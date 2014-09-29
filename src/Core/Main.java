package Core;

import java.io.IOException;

public class Main {
	
	public static CoinjoinAppKit coinjoin;
		
	public static void main(String[] args) throws InterruptedException, IOException {
		coinjoin = new CoinjoinAppKit();
		coinjoin.run();
	}
		 
}

