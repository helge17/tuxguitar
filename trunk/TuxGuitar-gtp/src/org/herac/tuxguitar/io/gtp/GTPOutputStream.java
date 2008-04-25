package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class GTPOutputStream implements TGOutputStreamBase{
	
	private TGFactory factory;
	private OutputStream outputStream;
	
	public void init(TGFactory factory,OutputStream stream) {
		this.factory = factory;
		this.outputStream = stream;
	}
	
	protected TGFactory getFactory(){
		return this.factory;
	}
	
	protected void skipBytes(int count) throws IOException {
		for(int i = 0;i < count;i++){
			this.outputStream.write(0);
		}
	}
	
	protected void writeByte(byte v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeUnsignedByte(int v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeBytes(byte[] v) throws IOException {
		this.outputStream.write(v);
	}
	
	protected void writeBoolean(boolean v) throws IOException {
		this.outputStream.write(v ? 1 : 0);
	}
	
	protected void writeInt(int v) throws IOException {
		byte[] bytes = { (byte)(v & 0x00FF),(byte)((v >> 8) & 0x000000FF),(byte) ((v >> 16) & 0x000000FF),(byte)((v >> 24) & 0x000000FF) };
		this.outputStream.write(bytes);
	}
	
	protected void writeString(char[] chars, int maximumLength) throws IOException {
		int length = (maximumLength == 0 || maximumLength > chars.length ? chars.length : maximumLength );
		for(int i = 0 ; i < length; i ++){
			this.outputStream.write( chars[ i ] );
		}
	}
	
	protected void writeStringInteger(String string) throws IOException {
		char[] chars = string.toCharArray();
		this.writeInt( chars.length );
		this.writeString( chars , 0 );
	}
	
	protected void writeStringByte(String string,int size) throws IOException {
		char[] chars = string.toCharArray();
		this.writeByte( (byte)( size == 0 || size > chars.length ? chars.length : size ));
		this.writeString( chars , size );
		this.skipBytes( size - chars.length );
	}
	
	protected void writeStringByteSizeOfInteger(String string) throws IOException {
		char[] chars = string.toCharArray();
		this.writeInt( (chars.length + 1) );
		this.writeStringByte(string,chars.length);
	}
	
	protected void close() throws IOException{
		this.outputStream.flush();
		this.outputStream.close();
	}
}
