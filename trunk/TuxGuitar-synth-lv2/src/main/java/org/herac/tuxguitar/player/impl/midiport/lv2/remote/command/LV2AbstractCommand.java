package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;
import java.nio.charset.Charset;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public abstract class LV2AbstractCommand<T> implements LV2Command<T> {
	
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	private LV2Connection connection;
	
	public LV2AbstractCommand(LV2Connection connection) {
		this.connection = connection;
	}
	
	public LV2Connection getConnection() {
		return connection;
	}
	
	public void readBytes(byte[] buffer) throws IOException {
		this.getConnection().getInputStream().read(buffer);
	}
	
	public byte[] readBytes(int length) throws IOException {
		byte[] bytes = new byte[length];
		
		this.readBytes(bytes);
		
		return bytes;
	}
	
	public Boolean readBoolean() throws IOException {
		byte[] buffer = this.readBytes(1);
		
		return (buffer[0] == 1);
	}
	
	public Integer readInteger() throws IOException {
		byte[] buffer = this.readBytes(4);
		
		return ((buffer[0] & 0xFF) << 0) + ((buffer[1] & 0xFF) << 8) + ((buffer[2] & 0xFF) << 16) + ((buffer[3] & 0xFF) << 24);
	}
	
	public Float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readInteger());
	}
	
	public String readString() throws IOException {
		byte[] bytes = this.readBytes(this.readInteger());
		
		return new String(bytes, CHARSET);
	}
	
	public void writeBytes(byte[] bytes) throws IOException {
		this.getConnection().getOutputStream().write(bytes);
		this.getConnection().getOutputStream().flush();
	}
	
	public void writeBoolean(Boolean value) throws IOException {
        this.writeBytes(new byte[] { value ? (byte) 1 : (byte) 0});
	}
	
	public void writeInteger(Integer value) throws IOException {
		byte[] buffer = new byte[4];
        buffer[0] = (byte) ((value >>> 0) & 0xFF);
        buffer[1] = (byte) ((value >>> 8) & 0xFF);
        buffer[2] = (byte) ((value >>> 16) & 0xFF);
        buffer[3] = (byte) ((value >>> 24) & 0xFF);
        
        this.writeBytes(buffer);
	}
	
	public void writeFloat(float value) throws IOException {
		this.writeInteger(Float.floatToIntBits(value));
	}
	
	public void writeString(String value) throws IOException {
		byte[] bytes = value.getBytes(CHARSET);
		
		this.writeInteger(bytes.length);
		this.writeBytes(bytes);
	}
	
	public T safeProcess(T defaultValue) {
		synchronized (this.getConnection()) {
			try {
				return this.process();
			} catch (IOException e) {
				this.getConnection().close();
				
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
	
	public T safeProcess() {
		return this.safeProcess(null);
	}
}
