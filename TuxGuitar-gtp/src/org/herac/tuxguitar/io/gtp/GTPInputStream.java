package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class GTPInputStream extends GTPFileFormat implements TGInputStreamBase{
	
	private int versionIndex;
	private String version;
	private String[] versions;
	private InputStream stream;
	
	public GTPInputStream(GTPSettings settings, String[] versions){
		super(settings);
		this.versions = versions;
	}
	
	public void init(TGFactory factory,InputStream stream) {
		super.init(factory);
		this.stream = stream;
		this.version = null;
	}
	
	protected String getVersion(){
		return this.version;
	}
	
	protected int getVersionIndex(){
		return this.versionIndex;
	}
	
	public boolean isSupportedVersion(String version) {
		for (int i = 0; i < this.versions.length; i++) {
			if (version.equals(this.versions[i])) {
				this.versionIndex = i;
				return true;
			}
		}
		return false;
	}
	
	public boolean isSupportedVersion(){
		try{
			readVersion();
			return isSupportedVersion(getVersion());
		}catch(Exception e){
			return false;
		}catch(Error e){
			return false;
		}
	}
	
	protected void readVersion(){
		try {
			if(this.version == null){
				this.version = readStringByte(30, DEFAULT_VERSION_CHARSET);
			}
		} catch (IOException e) {
			this.version = "NOT_SUPPORTED";
		}
	}
	
	protected int read() throws IOException {
		return this.stream.read();
	}
	
	protected int read(byte[] bytes) throws IOException {
		return this.stream.read(bytes);
	}
	
	protected int read(byte[] bytes,int off,int len) throws IOException {
		return this.stream.read(bytes,off,len);
	}
	
	protected void skip(int bytes) throws IOException{
		this.stream.read(new byte[bytes]);
	}
	
	protected int readUnsignedByte() throws IOException {
		return (this.stream.read() & 0xff);
	}
	
	protected byte readByte() throws IOException {
		return ((byte)this.stream.read());
	}
	
	protected boolean readBoolean() throws IOException {
		return (this.stream.read() == 1);
	}
	
	protected int readInt() throws IOException {
		byte[] bytes = new byte[4];
		this.stream.read(bytes);
		return ((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
	}
	
	protected long readLong() throws IOException {
		byte[] bytes = new byte[8];
		this.stream.read(bytes);
		return ((long) (bytes[7] & 0xff) << 56) | ((long) (bytes[6] & 0xff) << 48) | ((long) (bytes[5] & 0xff) << 40) | ((long) (bytes[4] & 0xff) << 32) |
			   ((long) (bytes[3] & 0xff) << 24) | ((long) (bytes[2] & 0xff) << 16) | ((long) (bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
	}
	
	protected String readString(int size, int len, String charset) throws IOException{
		byte[] bytes = new byte[ (size > 0?size:len) ];
		this.stream.read(bytes);
		return newString(bytes,(len >= 0?len:size), charset);
	}
	
	protected String readString(int length, String charset) throws IOException{
		return readString(length, length, charset);
	}
	
	protected String readString(int length) throws IOException{
		return readString(length, getSettings().getCharset());
	}
	
	protected String readStringInteger(String charset) throws IOException {
		return readString( readInt(), charset);
	}
	
	protected String readStringInteger() throws IOException {
		return readStringInteger( getSettings().getCharset() );
	}
	
	protected String readStringByte(int size, String charset) throws IOException {
		return readString( size, readUnsignedByte(), charset);
	}
	
	protected String readStringByte(int size) throws IOException {
		return readStringByte( size, getSettings().getCharset() );
	}
	
	protected String readStringByteSizeOfByte(String charset) throws IOException {
		return readStringByte( (readUnsignedByte() - 1), charset);
	}
	
	protected String readStringByteSizeOfByte() throws IOException {
		return readStringByteSizeOfByte( getSettings().getCharset() );
	}
	
	protected String readStringByteSizeOfInteger(String charset) throws IOException {
		return readStringByte( (readInt() - 1), charset);
	}
	
	protected String readStringByteSizeOfInteger() throws IOException {
		return readStringByteSizeOfInteger( getSettings().getCharset() );
	}
	
	private String newString(byte[] bytes, int length, String charset) {
		try {
			return new String(new String(bytes, 0, length, charset).getBytes(DEFAULT_TG_CHARSET), DEFAULT_TG_CHARSET);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new String(bytes, 0, length);
	}
	
	protected void close() throws IOException{
		this.stream.close();
	}
}
