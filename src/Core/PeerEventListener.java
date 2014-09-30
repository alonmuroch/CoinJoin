package Core;

public class PeerEventListener {
	
	PeerEvent event = new PeerEvent();
	boolean wasSignalled = false;

	public void doWait() throws PeerTimeoutException{
		synchronized(event){
			while(!wasSignalled){
				try{
					event.wait(10000);
				} catch(InterruptedException e){}
				if (!wasSignalled){throw new PeerTimeoutException();}
			}
			wasSignalled = false;
		}
	  }

	public void doNotify(){
		synchronized(event){
			wasSignalled = true;
			event.notify();
		}
	}
	

	public class PeerEvent{
		boolean connected;
	}
	
}
