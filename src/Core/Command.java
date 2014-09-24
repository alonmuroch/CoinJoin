package Core;

public enum Command {
	VERSION (new byte[] {0x76,0x65,0x72,0x73,0x69,0x6F,0x6E,0x00,0x00,0x00,0x00,0x00}),
	VERACK (new byte[] {0x76, 0x65, 0x72, 0x61, 0x63, 0x6B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),
	ADDR,
	GETADDR,
	INV,
	GETDATA,
	NOTFOUND,
	SESSIONCREATE,
	SESSIONJOIN,
	PING,
	PONG,
	REJECT;
	
	private byte[] value;
	
	Command(){
		
	}
	
    Command(byte[] val) {
        this.value = val;
    }

    public byte[] getValue() {
        return value;
    }
}
