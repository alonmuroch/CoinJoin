package Core;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Parser {
	byte[] MAGIC_NUMBER = new byte[4];
	Command command;
	int length;
	byte[] checksum = new byte[4];	
	byte[] payload;

	public Parser(byte[] message){
		for (int i = 0; i<4; i++){MAGIC_NUMBER[i] = message[i];}
		byte[] cmd = new byte[12];
		int a = 0;
		for (int i = 4; i<16; i++){
			cmd[a] = message[i];
			a++;
		}
		if (Arrays.equals(cmd, Command.VERSION.getValue())){command = Command.VERSION;}
		else if (Arrays.equals(cmd, Command.VERACK.getValue())){command = Command.VERACK;}
		a = 0;
		byte len[] = new byte[4];
		for (int i = 16; i<20; i++){
			len[a]=message[i];
			a++;
		}
		length = Utils.byteArrayToInt(len);
		a = 0;
		for (int i = 20; i<24; i++){
			checksum[a]=message[i];
			a++;
		}
		payload = new byte[length];
		a = 0;
		for (int i = 24; i<message.length; i++){
			payload[a]=message[i];
			a++;
		}
		MessageDigest digest = null;
		try {digest = MessageDigest.getInstance("SHA-256");} 
		catch (NoSuchAlgorithmException e) {e.printStackTrace();}
		byte[] hashbytes = digest.digest(digest.digest(payload));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for (int i = 0; i<4; i++){
			outputStream.write(hashbytes[i]);
		}
		byte[] chk = outputStream.toByteArray();
		try {if (!Arrays.equals(checksum, chk)){throw new InvalidChecksumException();}} 
		catch (InvalidChecksumException e){e.printStackTrace();}
	}
	
	public void printMessage(){
		System.out.println("Magic Number: " + Utils.bytesToHex(MAGIC_NUMBER));
		System.out.println("Command: " + command.toString());
		System.out.println("Length: " + length);
		System.out.println("Checksum: " + Utils.bytesToHex(checksum));
		System.out.println("Payload: " + Utils.bytesToHex(payload));
	}
}
