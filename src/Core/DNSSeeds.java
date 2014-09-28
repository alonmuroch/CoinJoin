package Core;

import java.util.ArrayList;

public class DNSSeeds {
	
	ArrayList<Seed> seeds = new ArrayList<Seed>();
	
	public DNSSeeds() {
		Seed authenticator = new Seed("bitcoinauthenticator.org", 8000);
		seeds.add(authenticator);
	}
	
	public ArrayList<Seed> getSeeds(){
		return seeds;
	}
	
	public class Seed {
		String domain;
		int port;
		
		public Seed (String domain, int port){
			this.domain = domain;
			this.port = port;
		}
		
		public String getDomain(){
			return this.domain;
		}
		
		public int getPort(){
			return this.port;
		}
	}
	
}
