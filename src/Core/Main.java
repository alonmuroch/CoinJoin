package Core;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	public static CoinjoinAppKit coinjoin;
		
	public static void main(String[] args) throws InterruptedException, IOException {
		coinjoin = new CoinjoinAppKit();
		coinjoin.run();
	}
		 
}

