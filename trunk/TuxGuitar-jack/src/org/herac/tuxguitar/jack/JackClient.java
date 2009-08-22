package org.herac.tuxguitar.jack;

public class JackClient{
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-jack-jni");
	
	static{
		System.loadLibrary(JNI_LIBRARY_NAME);
	}
	
	private long instance;
	private boolean open;
	private boolean openPorts;
	
	public JackClient() {
		this.instance = malloc();
		this.open = false;
		this.openPorts = false;
	}
	
	public void finalize(){
		if(this.instance != 0 ){
			this.free(this.instance);
			this.instance = 0;
		}
	}
	
	public boolean isOpen(){
		return (this.instance != 0 && this.open);
	}
	
	public void open(){
		if(this.instance != 0 && !this.open){
			this.open(this.instance);
			this.open = true;
		}
	}
	
	public void close( boolean force ){
		if( force ){
			this.closePorts();
		}
		if( !this.isPortsOpen() ){
			if(this.instance != 0 && this.open){
				this.close(this.instance);
				this.open = false;
			}
		}
	}
	
	public void close(){
		this.close( true );
	}
	
	public boolean isPortsOpen(){
		return (this.isOpen() && this.openPorts);
	}
	
	public void openPorts(){
		if(!this.isOpen()){
			this.open();
		}
		if(this.isOpen() && !this.openPorts){
			this.openPorts(this.instance, 1 );
			this.openPorts = true;
		}
	}
	
	public void closePorts(){
		if(this.isOpen() && this.openPorts){
			this.closePorts(this.instance);
			this.openPorts = false;
		}
		this.close( false );
	}
	
	public void addEventToQueue( int port , byte[] data){
		if(this.instance != 0 && this.open){
			this.addEventToQueue(this.instance, port, data );
		}
	}
	
	public long getTransportUID(){
		if(this.instance != 0 && this.open){
			return this.getTransportUID(this.instance);
		}
		return 0;
	}
	
	public long getTransportFrame(){
		if(this.instance != 0 && this.open){
			return this.getTransportFrame(this.instance);
		}
		return 0;
	}
	
	public long getTransportFrameRate(){
		if(this.instance != 0 && this.open){
			return this.getTransportFrameRate(this.instance);
		}
		return 0;
	}
	
	public void setTransportFrame( long frame ){
		if(this.instance != 0 && this.open){
			this.setTransportFrame(this.instance, frame );
		}
	}
	
	public void setTransportStart(){
		if(this.instance != 0 && this.open){
			this.setTransportStart(this.instance);
		}
	}
	
	public void setTransportStop(){
		if(this.instance != 0 && this.open){
			this.setTransportStop(this.instance);
		}
	}
	
	public boolean isTransportRunning(){
		if(this.instance != 0 && this.open){
			return this.isTransportRunning(this.instance);
		}
		return false;
	}
	
	private native long malloc();
	
	private native void free(long instance);
	
	private native void open(long instance);
	
	private native void close(long instance);
	
	private native void openPorts(long instance, int ports);
	
	private native void closePorts(long instance);
	
	private native long getTransportUID(long instance);
	
	private native long getTransportFrame(long instance);
	
	private native long getTransportFrameRate(long instance);
	
	private native void setTransportFrame(long instance, long frame);
	
	private native void setTransportStart(long instance);
	
	private native void setTransportStop(long instance);
	
	private native boolean isTransportRunning(long instance);
	
	private native void addEventToQueue(long instance, int port, byte[] data);
}
