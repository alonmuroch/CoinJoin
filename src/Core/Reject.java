package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Reject {
	
	Command cmd;
	ccode reason;

	public Reject(Command cmd, ccode reason){
		this.cmd = cmd;
		this.reason = reason;
	}
	
	public Reject(byte[] payload){
		byte[] cmd = new byte[12];
		for (int i=0; i<12; i++){cmd[i]=payload[i];}
		if (Arrays.equals(cmd, Command.VERSION.getValue())){this.cmd = Command.VERSION;}
		else if (Arrays.equals(cmd, Command.VERACK.getValue())){this.cmd = Command.VERACK;}
		else if (Arrays.equals(cmd, Command.PING.getValue())){this.cmd = Command.PING;}
		else if (Arrays.equals(cmd, Command.PONG.getValue())){this.cmd = Command.PONG;}
		else if (Arrays.equals(cmd, Command.REJECT.getValue())){this.cmd = Command.REJECT;}
		byte[] reason = new byte[1];
		for (int i=12; i<13; i++){reason[0]=payload[i];}
		if (Arrays.equals(reason, ccode.REJECT_DUPLICATE.getValue())){this.reason = ccode.REJECT_DUPLICATE;}
		else if (Arrays.equals(reason, ccode.REJECT_INVALID.getValue())){this.reason = ccode.REJECT_INVALID;}
		else if (Arrays.equals(reason, ccode.REJECT_MALFORMED.getValue())){this.reason = ccode.REJECT_MALFORMED;}
		else if (Arrays.equals(reason, ccode.REJECT_OBSOLETE.getValue())){this.reason = ccode.REJECT_OBSOLETE;}
	}
	
	public enum ccode {
		REJECT_MALFORMED (new byte[] {0x01}),
		REJECT_INVALID (new byte[] {0x10}),
		REJECT_OBSOLETE (new byte[] {0x11}),
		REJECT_DUPLICATE (new byte[] {0x12});
		
		private byte[] value;
		
		ccode(){
			
		}
		
	    ccode(byte[] val) {
	        this.value = val;
	    }

	    public byte[] getValue() {
	        return value;
	    }
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(cmd.getValue());
		outputStream.write(reason.getValue());
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
	public void printReject(){
		System.out.println("Command: " + cmd.toString());
		System.out.println("Reason: " + reason.toString());
	}
	
}
