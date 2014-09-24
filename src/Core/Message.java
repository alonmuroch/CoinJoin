package Core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Message {

	byte[] MAGIC_NUMBER = { 0x21, 0x0f, 0x36, 0x69 };
	Command command;
	int length;
	byte[] checksum;
	byte[] payload;
	
	public Message (Command cmd, byte[] PayLoad){
		command = cmd;
		payload = PayLoad;
		length =  payload.length;
		MessageDigest digest = null;
		try {digest = MessageDigest.getInstance("SHA-256");} 
		catch (NoSuchAlgorithmException e) {e.printStackTrace();}
		byte[] hashbytes = digest.digest(digest.digest(payload));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for (int i = 0; i<4; i++){
			outputStream.write(hashbytes[i]);
		}
		checksum = outputStream.toByteArray();
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(MAGIC_NUMBER);
		outputStream.write(command.getValue()); 
		outputStream.write(ByteBuffer.allocate(4).putInt(length).array());
		outputStream.write(checksum);
		outputStream.write(payload);
		byte output[] = outputStream.toByteArray();
		return output;
	}
	
}
