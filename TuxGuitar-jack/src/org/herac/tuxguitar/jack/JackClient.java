package org.herac.tuxguitar.jack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JackClient{
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-jack-jni");
	
	static{
		System.loadLibrary(JNI_LIBRARY_NAME);
	}
	
	private long instance;
	private boolean openTransport;
	private List<JackPort> jackPorts;
	private List<JackPortRegisterListener> jackPortRegisterListeners;
	
	public JackClient() {
		this.instance = malloc();
		this.jackPorts = new ArrayList<JackPort>();
		this.jackPortRegisterListeners = new ArrayList<JackPortRegisterListener>();
	}
	
	public void finalize(){
		if(this.instance != 0 ){
			this.free(this.instance);
			this.instance = 0;
		}
	}
	
	public void open(){
		if(this.instance != 0 && !this.isOpen() ){
			this.open(this.instance);
		}
	}
	
	public void close(){
		this.close( true );
	}
	
	public void close( boolean force ){
		if( force ){
			this.closePorts();
			this.closeTransport();
		}
		if( !this.isAnyJackPortOpen() && !this.isTransportOpen() ){
			if( this.isOpen() ){
				this.close(this.instance);
			}
		}
	}
	
	public boolean isOpen(){
		if( this.instance != 0 ){
			return this.isOpen(this.instance);
		}
		return false;
	}
	
	public JackPort openPort(String jackPortName){
		if( this.isOpen() ){
			JackPort jackPort = this.findPort(jackPortName);
			if( jackPort != null && !this.isPortOpen(jackPort) ){
				this.closePort(jackPort);
				
				jackPort = null;
			}
			if( jackPort == null ){
				long jackPortId = this.openPort(this.instance, jackPortName);
				if( jackPortId != 0 ){
					jackPort = new JackPort(jackPortId, jackPortName);
					
					this.jackPorts.add(jackPort);
					this.onPortRegistered();
				}
			}
			return jackPort;
		}
		return null;
	}
	
	public void closePort(JackPort jackPort){
		if( this.isOpen() ){
			JackPort jackPortToClose = this.findPort(jackPort.getJackPortName());
			if( jackPortToClose != null ) {
				this.closePort(this.instance, jackPortToClose.getJackPortId());
				this.jackPorts.remove( jackPortToClose );
			}
		}
	}
	
	public void closePorts(){
		if( this.isOpen() ) {
			List<JackPort> jackPorts = new ArrayList<JackPort>(this.jackPorts);
			Iterator<JackPort> it = jackPorts.iterator();
			while( it.hasNext() ){
				this.closePort((JackPort) it.next());
			}
		}
	}
	
	public JackPort findPort(String jackPortName){
		if( this.isOpen() ){
			Iterator<JackPort> it = this.jackPorts.iterator();
			while( it.hasNext() ){
				JackPort jackPort = (JackPort) it.next();
				if( jackPort.getJackPortName().equals(jackPortName) ){
					return jackPort;
				}
			}
		}
		return null;
	}
	
	public boolean isPortOpen(JackPort jackPort){
		if( this.isOpen() ){
			return this.isPortOpen(this.instance, jackPort.getJackPortId());
		}
		return false;
	}
	
	public boolean isAnyJackPortOpen(){
		return (this.isOpen() && !this.jackPorts.isEmpty());
	}
	
	public boolean isTransportOpen(){
		return (this.isOpen() && this.openTransport);
	}
	
	public void openTransport(){
		if(this.isOpen() && !this.openTransport){
			this.openTransport = true;
		}
	}
	
	public void closeTransport(){
		if(this.isOpen() && this.openTransport){
			this.openTransport = false;
		}
		this.close( false );
	}
	
	public void addEventToQueue(JackPort jackPort , byte[] data){
		if( this.isOpen() ){
			this.addEventToQueue(this.instance, jackPort.getJackPortId(), data );
		}
	}
	
	public long getTransportUID(){
		if( this.isOpen() ){
			return this.getTransportUID(this.instance);
		}
		return 0;
	}
	
	public long getTransportFrame(){
		if( this.isOpen() ){
			return this.getTransportFrame(this.instance);
		}
		return 0;
	}
	
	public long getTransportFrameRate(){
		if( this.isOpen() ){
			return this.getTransportFrameRate(this.instance);
		}
		return 0;
	}
	
	public void setTransportFrame( long frame ){
		if( this.isOpen() ){
			this.setTransportFrame(this.instance, frame );
		}
	}
	
	public void setTransportStart(){
		if( this.isOpen() ){
			this.setTransportStart(this.instance);
		}
	}
	
	public void setTransportStop(){
		if( this.isOpen() ){
			this.setTransportStop(this.instance);
		}
	}
	
	public boolean isTransportRunning(){
		if( this.isOpen() ){
			return this.isTransportRunning(this.instance);
		}
		return false;
	}
	
	public List<String> getPortNames(String type, long flags) {
		if( this.isOpen() ){
			return this.getPortNames(this.instance, type, flags);
		}
		return null;
	}
	
	public List<String> getPortConnections(String portName){
		if( this.isOpen() ){
			return this.getPortConnections(this.instance, portName);
		}
		return null;
	}
	
	public void connectPorts(String srcPortName, String dstPortName){
		if( this.isOpen() ){
			this.connectPorts(this.instance, srcPortName, dstPortName);
		}
	}
	
	public void onPortRegistered(){
		Iterator<JackPortRegisterListener> it = this.jackPortRegisterListeners.iterator();
		while( it.hasNext() ){
			JackPortRegisterListener jackPortRegisterListener = (JackPortRegisterListener) it.next();
			jackPortRegisterListener.onPortRegistered();
		}
	}
	
	public void addPortRegisterListener( JackPortRegisterListener listener ){
		if(!this.jackPortRegisterListeners.contains( listener ) ){
			this.jackPortRegisterListeners.add( listener );
		}
	}
	
	public void removePortRegisterListener( JackPortRegisterListener listener ){
		if( this.jackPortRegisterListeners.contains( listener ) ){
			this.jackPortRegisterListeners.remove( listener );
		}
	}

	private native long malloc();
	
	private native void free(long instance);
	
	private native void open(long instance);
	
	private native void close(long instance);
	
	private native long openPort(long instance, String portName);
	
	private native void closePort(long instance, long portId);
	
	private native long getTransportUID(long instance);
	
	private native long getTransportFrame(long instance);
	
	private native long getTransportFrameRate(long instance);
	
	private native void setTransportFrame(long instance, long frame);
	
	private native void setTransportStart(long instance);
	
	private native void setTransportStop(long instance);
	
	private native void addEventToQueue(long instance, long portId, byte[] data);
	
	private native boolean isOpen(long instance);
	
	private native boolean isPortOpen(long instance, long portId);
	
	private native boolean isTransportRunning(long instance);
	
	private native void connectPorts(long instance, String srcPortName, String dstPortName);
	
	private native List<String> getPortNames(long instance, String type, long flags);
	
	private native List<String> getPortConnections(long instance, String portName);
	
}
