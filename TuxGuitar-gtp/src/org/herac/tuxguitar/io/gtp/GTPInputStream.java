package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class GTPInputStream implements TGInputStreamBase{

    private int versionIndex;
    private String version;
    private String[] versions;
    private TGFactory factory;
    private InputStream stream;
    
    public GTPInputStream(String[] versions){
    	this.versions = versions;
    }
    
    public void init(TGFactory factory,InputStream stream) {	
    	this.factory = factory;
    	this.stream = stream;
    	this.version = null;
    }    
    
    protected String getVersion(){
    	return this.version;
    }
    
    protected int getVersionIndex(){
    	return this.versionIndex;
    }
    
    protected TGFactory getFactory(){
    	return this.factory;
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
				this.version = readStringByte(30);
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

    protected String readString(int size, int len) throws IOException{
    	byte[] bytes = new byte[ (size > 0?size:len) ];
        this.stream.read(bytes);
        return newString(bytes,(len >= 0?len:size));    	
    }

    protected String readString(int length) throws IOException{
    	return readString(length, length);
    }
    
    protected String readStringInteger() throws IOException {
        return readString( readInt() );
    }
    
    protected String readStringByte(int size) throws IOException {
    	return readString( size, readUnsignedByte() );
    } 
    
    protected String readStringByteSizeOfByte() throws IOException {
        return readStringByte( (readUnsignedByte() - 1) );
    }

    protected String readStringByteSizeOfInteger() throws IOException {
    	return readStringByte( (readInt() - 1) );
    }

    private String newString(byte[] bytes, int length) {
    	char[] chars = new char[length];
    	for(int i = 0; i < chars.length; i++){
    		chars[i] = (char) (bytes[i] & 0xff);
    	}
    	return new String(chars);
    }

    protected void close() throws IOException{
        this.stream.close();
    }
}
